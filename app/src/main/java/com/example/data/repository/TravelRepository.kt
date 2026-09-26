package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.BookingEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.NotificationEntity
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.ChatMessage
import com.example.data.model.NotificationItem
import com.example.data.model.PackageCategory
import com.example.data.model.Review
import com.example.data.model.TravelPackage
import com.example.data.model.UserProfile
import com.example.data.remote.FirebaseAuthService
import com.example.data.remote.FirestoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class TravelRepository(
    private val context: Context,
    private val db: AppDatabase
) {

    private val scope = CoroutineScope(Dispatchers.IO)
    val authService = FirebaseAuthService(context)
    val firestoreService = FirestoreService(context)

    // In-memory state for UI
    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "usr_alawla_01",
            name = "Merwan Abdusomed",
            email = "merwanabdusomed@gmail.com",
            phone = "+251 911 955 8887",
            passportNumber = "EP892304",
            nationality = "Ethiopian",
            isAdmin = false,
            isLoggedIn = true,
            firebaseUid = null
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _packages = MutableStateFlow<List<TravelPackage>>(emptyList())
    val packages: StateFlow<List<TravelPackage>> = _packages.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _isAgentTyping = MutableStateFlow(false)
    val isAgentTyping: StateFlow<Boolean> = _isAgentTyping.asStateFlow()

    init {
        seedInitialPackages()
        seedInitialReviews()
        seedInitialDataIfEmpty()
    }

    fun toggleAdminMode(enabled: Boolean) {
        _currentUser.value = _currentUser.value.copy(isAdmin = enabled)
    }

    fun updateUserProfile(name: String, email: String, phone: String, passport: String, nationality: String) {
        _currentUser.value = _currentUser.value.copy(
            name = name,
            email = email,
            phone = phone,
            passportNumber = passport,
            nationality = nationality
        )
    }

    fun updatePackagePrice(packageId: String, newPriceEtb: Long) {
        _packages.value = _packages.value.map { pkg ->
            if (pkg.id == packageId) {
                pkg.copy(priceEtb = newPriceEtb)
            } else {
                pkg
            }
        }
        scope.launch {
            firestoreService.updatePackagePrice(packageId, newPriceEtb)
        }
    }

    fun updatePackage(updatedPackage: TravelPackage) {
        _packages.value = _packages.value.map { pkg ->
            if (pkg.id == updatedPackage.id) updatedPackage else pkg
        }
        scope.launch {
            firestoreService.savePackage(updatedPackage)
        }
    }

    fun addPackage(newPackage: TravelPackage) {
        _packages.value = listOf(newPackage) + _packages.value
        scope.launch {
            firestoreService.savePackage(newPackage)
        }
    }

    fun deletePackage(packageId: String) {
        _packages.value = _packages.value.filter { it.id != packageId }
    }

    // Authentication integration
    suspend fun signIn(email: String, pass: String): Result<UserProfile> {
        val result = authService.signIn(email, pass)
        result.onSuccess { profile ->
            _currentUser.value = profile
            scope.launch {
                firestoreService.saveUserProfile(profile)
            }
        }
        return result
    }

    suspend fun signUp(email: String, pass: String, name: String, phone: String): Result<UserProfile> {
        val result = authService.signUp(email, pass, name, phone)
        result.onSuccess { profile ->
            _currentUser.value = profile
            scope.launch {
                firestoreService.saveUserProfile(profile)
            }
        }
        return result
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return authService.resetPassword(email)
    }

    fun signOut() {
        authService.signOut()
        _currentUser.value = UserProfile(
            id = "guest_${System.currentTimeMillis()}",
            name = "Guest Traveler",
            email = "",
            phone = "",
            passportNumber = "",
            nationality = "Ethiopian",
            isAdmin = false,
            isLoggedIn = false,
            firebaseUid = null
        )
    }

    fun setQuickUser(user: UserProfile) {
        _currentUser.value = user
        scope.launch {
            firestoreService.saveUserProfile(user)
        }
    }

    val allBookings: Flow<List<Booking>> = db.bookingDao().getAllBookings().map { list ->
        list.map { it.toDomain() }
    }

    val chatMessages: Flow<List<ChatMessage>> = db.chatDao().getAllMessages().map { list ->
        list.map { it.toDomain() }
    }

    val notifications: Flow<List<NotificationItem>> = db.notificationDao().getAllNotifications().map { list ->
        list.map { it.toDomain() }
    }

    // Chat operations
    fun sendMessage(text: String, bookingRef: String? = null) {
        if (text.isBlank()) return
        val user = _currentUser.value
        val msgId = UUID.randomUUID().toString()
        val userMsg = ChatMessage(
            id = msgId,
            senderId = user.id,
            senderName = user.name,
            senderRole = if (user.isAdmin) "SUPPORT_AGENT" else "USER",
            message = text.trim(),
            timestamp = System.currentTimeMillis(),
            bookingReference = bookingRef,
            isAgent = user.isAdmin
        )

        scope.launch {
            db.chatDao().insertMessage(ChatMessageEntity.fromDomain(userMsg))

            // If user is a customer, simulate live support agent reply after a realistic delay
            if (!user.isAdmin) {
                _isAgentTyping.value = true
                delay(1400)
                _isAgentTyping.value = false

                val replyText = generateSupportReply(text, bookingRef)
                val agentMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    senderId = "agent_alawla_desk",
                    senderName = "Al-Awla Concierge (Fatima)",
                    senderRole = "SUPPORT_AGENT",
                    message = replyText,
                    timestamp = System.currentTimeMillis(),
                    bookingReference = bookingRef,
                    isAgent = true
                )
                db.chatDao().insertMessage(ChatMessageEntity.fromDomain(agentMsg))
            }
        }
    }

    private fun generateSupportReply(userMsg: String, bookingRef: String?): String {
        val lower = userMsg.lowercase(Locale.ROOT)
        return when {
            bookingRef != null -> {
                "Hello! Thank you for reaching out regarding booking #$bookingRef. Our logistics desk in Addis Ababa & Jeddah has pulled up your file. Our representative is currently ensuring your visa status, Ethiopian Airlines flight reservation, and hotel check-in are synchronized. Do you need any assistance with document uploads or date adjustments?"
            }
            lower.contains("visa") -> {
                "For Umrah Visas, we require a clear scanned copy of your Ethiopian passport (valid for at least 6 months) and a recent white-background passport photo. Processing typically takes 24–48 hours once submitted through our portal."
            }
            lower.contains("price") || lower.contains("cost") || lower.contains("package") -> {
                "Our 10-night Umrah packages start from 150,000 ETB (Standard 4 in room), 177,500 ETB (VIP 2 in room), 220,000 ETB (VVIP Jabal Al Kaaba Sheraton), and 285,000 ETB (VVIP Royal Suite Kaaba Front). All include flights, visa, hotels, and guided Ziyarat!"
            }
            lower.contains("diaspora") || lower.contains("sponsor") || lower.contains("guarantee") -> {
                "Yes! Al-Awla provides complete Diaspora Family Sponsorship. Family members in the USA, Europe, or Middle East can book and sponsor relatives residing in Ethiopia. We handle the application guarantee and document authentication seamlessly."
            }
            lower.contains("dubai") || lower.contains("turkey") || lower.contains("malaysia") || lower.contains("tour") -> {
                "Our international group and private leisure tours include round-trip flights, 4/5-star accommodation, guided excursions, and visa assistance. Check out our Dubai, Turkey, Malaysia, and Egypt packages in the Tours section!"
            }
            else -> {
                "As-salamu alaykum! Thank you for contacting Al-Awla Tour & Travel Services. An official travel consultant is reviewing your request. For urgent direct bookings, you can also reach our hotline at +251 986 111 333 or on WhatsApp."
            }
        }
    }

    // Booking submission
    suspend fun createBooking(booking: Booking): Booking {
        val entity = BookingEntity.fromDomain(booking)
        db.bookingDao().insertBooking(entity)

        // Sync with Cloud Firestore
        scope.launch {
            firestoreService.saveBooking(booking)
        }

        // Generate instant notification
        val notif = NotificationItem(
            id = UUID.randomUUID().toString(),
            title = "Booking Submitted: ${booking.bookingReference}",
            message = "We received your booking request for ${booking.packageName}. Our desk is reviewing your submission.",
            type = "BOOKING_UPDATE"
        )
        db.notificationDao().insertNotification(NotificationEntity.fromDomain(notif))

        return booking
    }

    suspend fun updateBookingStatus(id: String, newStatus: BookingStatus, adminNotes: String = "") {
        db.bookingDao().updateBookingStatus(id, newStatus.name, adminNotes)

        // Sync with Cloud Firestore
        scope.launch {
            firestoreService.updateBookingStatus(id, newStatus, adminNotes)
        }

        val notif = NotificationItem(
            id = UUID.randomUUID().toString(),
            title = "Booking Status Updated",
            message = "Your booking status has been updated to ${newStatus.label}. $adminNotes",
            type = "BOOKING_UPDATE"
        )
        db.notificationDao().insertNotification(NotificationEntity.fromDomain(notif))
    }

    suspend fun markNotificationRead(id: String) {
        db.notificationDao().markAsRead(id)
    }

    fun submitReview(packageId: String, packageName: String, rating: Int, comment: String) {
        val user = _currentUser.value
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val review = Review(
            id = UUID.randomUUID().toString(),
            packageId = packageId,
            packageName = packageName,
            userName = user.name,
            userCountry = user.nationality,
            rating = rating,
            comment = comment,
            date = dateFormat.format(Date())
        )
        _reviews.value = listOf(review) + _reviews.value
    }

    fun broadcastAnnouncement(title: String, message: String, type: String = "AGENCY_ANNOUNCEMENT") {
        scope.launch {
            val notif = NotificationItem(
                id = UUID.randomUUID().toString(),
                title = title,
                message = message,
                type = type
            )
            db.notificationDao().insertNotification(NotificationEntity.fromDomain(notif))
        }
    }

    private fun seedInitialPackages() {
        _packages.value = listOf(
            // Official 6th Round Umrah from alawlatourtravel.com
            TravelPackage(
                id = "pkg_umrah_round6_ustaz",
                title = "6th Round Umrah with Ustaz Abdul Fattah",
                titleAm = "6ኛ ዙር የዑምራ ፓኬጅ ከኡስታዝ አብዱልፈታህ ጋር (ጥቅምት 18 - 28)",
                titleAr = "رحلة العمرة الفوج السادس مع الشيخ عبد الفتاح",
                category = PackageCategory.UMRAH,
                tier = "Featured 6th Round",
                priceEtb = 150000L,
                priceUsd = 1200,
                durationDays = 11,
                durationNights = 10,
                hotelMakkah = "Markaziyah Luxury Hotel (Makkah)",
                hotelMadinah = "Markaziyah Central Hotel (Madinah)",
                occupancy = "Quad / Triple / Double Room",
                inclusions = listOf(
                    "Spiritual guidance and daily lectures with beloved Ustaz Abdul Fattah",
                    "Direct Flight Tickets (Addis Ababa - Jeddah/Madinah)",
                    "Official Saudi Umrah Visa Processing & Insurance",
                    "Hotel Accommodations near Holy Haram",
                    "VIP Luxury Group Transportation",
                    "Guided Historical Ziyarah in Makkah & Madinah",
                    "Registration at Bethel Future Mall Office 305"
                ),
                exclusions = listOf("Personal laundry & shopping", "Private taxi rides outside schedule"),
                description = "Official 6th Round Umrah organized by Al-Awla Tour & Travel from Tikimt 18 to Tikimt 28 with beloved Ustaz Abdul Fattah. Register at our Bethel Future Mall office (3rd Floor, Office 305) or call 0943989999 / 09119558887.",
                descriptionAm = "ድርጅታችን አል-አውላ ቱር & ትራቭል ከጥቅምት 18 እስከ ጥቅምት 28 ከተወዳጁ ኡስታዝ አብዱልፈታህ ጋር በመሆን ሁለቱን የተቀደሱትን ምድሮች ሊያዘይራችሁ እነሆ ዝግጅቱን አጠናቆ ጨርሷል፤ በቤተል ፊዩቸር ሞል 3ኛ ፎቅ ቢሮ ቁጥር 305 ወይም በስልክ 0943989999 / 09119558887 መመዝገብ ይችላሉ።",
                descriptionAr = "يسر شركة الأولى للسياحة والسفر الإعلان عن رحلة العمرة الفوج السادس برفقة الشيخ عبد الفتاح، يرجى التسجيل عبر مكاتبنا في بيتيل فيوتشر مول أو الاتصال بنا.",
                destination = "Makkah & Madinah, Saudi Arabia",
                imageUrl = "website_ustaz",
                isFeatured = true,
                rating = 5.0f,
                reviewCount = 480
            ),
            // Umrah Packages inspired by alawlatourtravel.com & zadtravelagency.com
            TravelPackage(
                id = "pkg_umrah_std",
                title = "Standard Umrah Package (10 Nights)",
                titleAm = "መደበኛ የዑምራ ፓኬጅ (10 ሌሊቶች)",
                titleAr = "باقة العمرة الاقتصادية (10 ليالٍ)",
                category = PackageCategory.UMRAH,
                tier = "Standard",
                priceEtb = 150000L,
                priceUsd = 1200,
                durationDays = 11,
                durationNights = 10,
                hotelMakkah = "Abraj Tayseer / Safeer Al-Misk",
                hotelMadinah = "Diyar Taiba / Markaziyah Area",
                occupancy = "4 Persons / Room",
                inclusions = listOf(
                    "Round-trip Ethiopian Airlines flight (Addis Ababa - Jeddah/Madinah)",
                    "Official Saudi Umrah Visa & Health Insurance",
                    "7 Nights accommodation in Makkah (Comfortable Shuttle)",
                    "3 Nights accommodation in Madinah (Markaziyah Area)",
                    "Full ground transport in luxury air-conditioned coaches",
                    "Guided Ziyarat to historical holy sites (Ghar Hira, Thawr, Mount Uhud, Quba)",
                    "Pre-departure religious orientation in Addis Ababa",
                    "Dedicated Ethiopian Mutawwif scholars & 24/7 guides"
                ),
                exclusions = listOf("Personal laundry", "Room service orders", "Additional private shopping"),
                description = "Our most beloved budget-conscious pilgrimage package delivering impeccable logistics, trusted scholars, and comfortable air-conditioned stays in holy Makkah and Madinah.",
                descriptionAm = "ተመጣጣኝ ዋጋ ያለው፣ በአስተማማኝ መስተንግዶ እና በታዋቂ ዑለማዎች የሚመራ መንፈሳዊ የዑምራ ጉዞ።",
                descriptionAr = "الباقة المفضلة لضيوف الرحمن مع كافة الترتيبات الموثوقة بإشراف مرشدين ذوي خبرة.",
                destination = "Makkah & Madinah, Saudi Arabia",
                imageUrl = "website_mekka3",
                isFeatured = true,
                rating = 4.9f,
                reviewCount = 312
            ),
            TravelPackage(
                id = "pkg_umrah_vip",
                title = "VIP Umrah Package (Double Room)",
                titleAm = "ቪአይፒ የዑምራ ፓኬጅ (ባለ ሁለት አልጋ ክፍል)",
                titleAr = "باقة العمرة المميزة (غرفة ثنائية)",
                category = PackageCategory.UMRAH,
                tier = "VIP",
                priceEtb = 177500L,
                priceUsd = 1420,
                durationDays = 11,
                durationNights = 10,
                hotelMakkah = "Safeer Al-Misk / Al Kiswah Towers",
                hotelMadinah = "Diyar Al Habib (Markaziyah Central)",
                occupancy = "2 Persons / Room (Private Double)",
                inclusions = listOf(
                    "Direct Ethiopian Airlines return flight tickets",
                    "Fast-track Umrah Visa & electronic entry clearance",
                    "7 Nights in Makkah + 3 Nights in Madinah",
                    "Guaranteed private room for 2 guests (Spouse / Pair)",
                    "Complimentary daily breakfast buffet",
                    "Exclusive small-group Ziyarat tour with historian scholar",
                    "Free Saudi 5G eSIM with 10GB Data & Calls",
                    "Luggage escort & porterage at airports"
                ),
                exclusions = listOf("Lunch & Dinner outside hotel", "Personal medical extras"),
                description = "Enjoy elevated privacy and peace of mind with private double-occupancy hotel rooms, gourmet breakfast, and priority assistance throughout your journey.",
                descriptionAm = "የተሻለ ግላዊነት እና ምቾት፣ ለሁለት ሰዎች ብቻ የተመደበ ክፍል እና የቁርስ አገልግሎት ያካተተ ምርጥ ፓኬጅ።",
                descriptionAr = "خصوصية تامة وإقامة ثنائية فاخرة مع بوفيه إفطار يومي وجولات خاصة.",
                destination = "Makkah & Madinah, Saudi Arabia",
                imageUrl = "umrah_banner",
                isFeatured = true,
                rating = 4.95f,
                reviewCount = 184
            ),
            TravelPackage(
                id = "pkg_umrah_vvip",
                title = "VVIP Haram-Front Luxury Package",
                titleAm = "ቪቪአይፒ የሀረም ፊት ለፊት የቅንጦት ፓኬጅ",
                titleAr = "باقة العمرة الفاخرة المطلة على الحرم",
                category = PackageCategory.UMRAH,
                tier = "VVIP",
                priceEtb = 220000L,
                priceUsd = 1750,
                durationDays = 11,
                durationNights = 10,
                hotelMakkah = "Sheraton Makkah Jabal Al Kaaba",
                hotelMadinah = "Frontel Al Harithia (Steps to Haram)",
                occupancy = "4 Persons / Suite",
                inclusions = listOf(
                    "5-Star Sheraton Makkah Jabal Al Kaaba accommodation",
                    "Walking distance to Holy Haram courtyards",
                    "Daily international 5-star breakfast buffet",
                    "VIP Express airport lounge arrival and departure",
                    "Haramain High-Speed Train VIP Class between Makkah and Madinah",
                    "Private comfortable air-conditioned transport",
                    "Comprehensive Ziyarat with private vehicle",
                    "Complimentary Zamzam 5L water container upon departure"
                ),
                exclusions = listOf("Personal shopping"),
                description = "Step directly from your luxury 5-star hotel lobby into the sacred sanctuary of the Haram. Indulge in world-class amenities and five-star hospitality.",
                descriptionAm = "ከቅንጦት 5 ኮከብ ሆቴል በቀጥታ ወደ ሀረም ግቢ የሚያደርስ፣ እጅግ የላቀ ምቾት እና መስተንግዶ ያለው።",
                descriptionAr = "إقامة 5 نجوم بجوار الحرم الشريف مباشرة مع قطار الحرمين السريع وخدمات VIP.",
                destination = "Makkah & Madinah, Saudi Arabia",
                imageUrl = "umrah_banner",
                isFeatured = true,
                rating = 5.0f,
                reviewCount = 96
            ),
            TravelPackage(
                id = "pkg_umrah_royal",
                title = "VVIP Royal Premium Suite Kaaba View",
                titleAm = "ሮያል ፕሪሚየም ካዕባ እይታ ስዊት ፓኬጅ",
                titleAr = "الباقة الملكية الفاخرة بإطلالة مباشرة على الكعبة",
                category = PackageCategory.UMRAH,
                tier = "VVIP Premium",
                priceEtb = 285000L,
                priceUsd = 2280,
                durationDays = 11,
                durationNights = 10,
                hotelMakkah = "Pullman Zamzam / Clock Royal Suite",
                hotelMadinah = "The Oberoi Madinah / Dar Al Taqwa",
                occupancy = "2 Persons / Royal Suite",
                inclusions = listOf(
                    "Direct Panoramic Kaaba & Holy Haram courtyard view suite",
                    "Private GMC Yukon Luxury VIP Chauffeur for all transfers",
                    "Dedicated personal Mutawwif scholar for Tawaf & Sai",
                    "Full-board gourmet dining (Breakfast, Lunch & Dinner)",
                    "Private historical Ziyarat with historical documentary guide",
                    "24/7 Personal concierge service",
                    "Premium welcome gift box (Ihram, Miswak, Perfume, Quran)"
                ),
                exclusions = listOf(),
                description = "The ultimate royal pilgrimage experience. Revel in uninterrupted panoramic Kaaba views, private VIP GMC transportation, and round-the-clock bespoke care.",
                descriptionAm = "የተቀደሰውን ካዕባ በቀጥታ ከመኝታ ክፍልዎ የሚመለከቱበት፣ በግል ጂኤምሲ ዩኮን የሚጓጓዙበት የንጉሳዊ ደረጃ ፓኬጅ።",
                descriptionAr = "التجربة الملكية الأرقى بإطلالة بانورامية ساحرة على الكعبة المشرفة وسيارة خاصة 24 ساعة.",
                destination = "Makkah Clock Tower & Madinah Sanctuary",
                imageUrl = "umrah_banner",
                isFeatured = false,
                rating = 5.0f,
                reviewCount = 42
            ),
            TravelPackage(
                id = "pkg_umrah_ramadan",
                title = "Ramadan Spiritual Journey (Last 10 Days)",
                titleAm = "የረመዳን መንፈሳዊ ጉዞ (የመጨረሻዎቹ 10 ቀናት)",
                titleAr = "رحلة العشر الأواخر من شهر رمضان المبارك",
                category = PackageCategory.UMRAH,
                tier = "Ramadan Special",
                priceEtb = 340000L,
                priceUsd = 2700,
                durationDays = 16,
                durationNights = 15,
                hotelMakkah = "Swissôtel Al Maqam Makkah",
                hotelMadinah = "Anwar Al Madinah Mövenpick",
                occupancy = "Double / Quad Room options",
                inclusions = listOf(
                    "Full 15 nights covering the blessed 10 nights of Ramadan & Eid Al-Fitr",
                    "Complete daily Suhur & Grand Iftar buffets",
                    "Reserved seating arrangements for Taraweeh & Tahajjud prayers",
                    "Special Laylat al-Qadr Khatm al-Quran program in Haram",
                    "Eid prayer in Holy Haram and festive celebratory dinner",
                    "Luggage VIP management and direct airport shuttle"
                ),
                exclusions = listOf("Personal items"),
                description = "Experience the unparalleled spiritual blessing of worshipping in the Holy Kaaba during the last ten nights of Ramadan and welcoming Eid in Makkah.",
                descriptionAm = "የተቀደሱትን የመጨረሻዎቹ 10 የረመዳን ሌሊቶች እና የዒድ በዓልን በቅዱሱ ሀረም የማሳለፊያ ልዩ ፓኬጅ።",
                descriptionAr = "عش روحانية العشر الأواخر وصلاة التراويح والتهجد في رحاب البيت العتيق وختمة القرآن.",
                destination = "Makkah & Madinah",
                imageUrl = "umrah_banner",
                isFeatured = false,
                rating = 5.0f,
                reviewCount = 78
            ),

            // International Tours (Dubai, Turkey, Malaysia, Thailand, Singapore, Egypt, etc.)
            TravelPackage(
                id = "pkg_tour_dubai",
                title = "Dubai Luxury & Desert Safari Adventure",
                titleAm = "የዱባይ የቅንጦት እና የበረሃ ሳፋሪ ጉዞ",
                titleAr = "رحلة دبي الفاخرة وسفاري الصحراء",
                category = PackageCategory.INTERNATIONAL,
                tier = "International Tour",
                priceEtb = 185000L,
                priceUsd = 1480,
                durationDays = 5,
                durationNights = 4,
                hotelGeneral = "Grand Millennium Dubai 5-Star Hotel",
                inclusions = listOf(
                    "Return flight tickets Addis Ababa - Dubai on Ethiopian Airlines",
                    "UAE Tourist Visa and COVID Insurance",
                    "4 Nights in 5-Star Dubai luxury hotel with daily breakfast",
                    "Burj Khalifa At the Top 124th & 125th Floor tickets",
                    "Desert 4x4 Dune Bashing safari with BBQ dinner & Tanoura show",
                    "Dubai Marina Private Yacht Cruise & skyline photography",
                    "Dubai Miracle Garden & Dubai Frame entrance passes",
                    "Airport pick-up and drop-off in executive coach"
                ),
                exclusions = listOf("Tourism Dirham fee", "Personal shopping at Dubai Mall"),
                description = "Discover the sparkling metropolis of Dubai. From sky-piercing skyscrapers and tranquil marina yachts to thrilling golden desert dunes.",
                descriptionAm = "የዱባይን ድንቅ ህንፃዎች፣ የቡርጅ ከሊፋን እይታ፣ የበረሃ ሳፋሪን እና የቅንጦት ጀልባ ጉዞን በአንድ ላይ ያጣጥሙ።",
                descriptionAr = "اكتشف روعة دبي مع برج خليفة ورحلات اليخوت وسفاري الصحراء مع إقامة 5 نجوم.",
                destination = "Dubai, United Arab Emirates",
                imageUrl = "hero_banner",
                isComingSoon = false,
                isFeatured = true,
                rating = 4.9f,
                reviewCount = 145
            ),
            TravelPackage(
                id = "pkg_tour_turkey",
                title = "Turkey: Istanbul & Cappadocia Balloon Fantasy",
                titleAm = "ቱርክ: ኢስታንቡል እና የካፓዶቂያ የፊኛ ጉዞ",
                titleAr = "تركيا: سحر إسطنبول ومناطيد كابادوكيا",
                category = PackageCategory.INTERNATIONAL,
                tier = "International Tour",
                priceEtb = 240000L,
                priceUsd = 1920,
                durationDays = 7,
                durationNights = 6,
                hotelGeneral = "Radisson Blu Istanbul & Cave Suite Cappadocia",
                inclusions = listOf(
                    "International return flights from Addis Ababa",
                    "Domestic flights: Istanbul <-> Cappadocia",
                    "Turkish Visa processing assistance",
                    "Bosphorus dinner cruise with live folklore dance",
                    "Hagia Sophia, Blue Mosque & Topkapi Palace guided tour",
                    "Cappadocia Sunrise Hot Air Balloon flight",
                    "Grand Bazaar & Spice Market shopping escort"
                ),
                exclusions = listOf("Personal souvenirs"),
                description = "Immerse yourself in centuries of Ottoman elegance and surreal volcanic rock fairy chimneys with our signature Turkey itinerary.",
                descriptionAm = "የኢስታንቡልን ጥንታዊ ታሪክ፣ የቦስፈረስን ወንዝ እና በካፓዶቂያ አስደናቂውን የፊኛ ጉዞ ያካተተ ድንቅ ጉዞ።",
                descriptionAr = "رحلة العمر بين مضيق البوسفور ومساجد إسطنبول التاريخية ومناطيد كابادوكيا الساحرة.",
                destination = "Istanbul & Cappadocia, Turkey",
                imageUrl = "hero_banner",
                isComingSoon = false,
                isFeatured = true,
                rating = 4.95f,
                reviewCount = 112
            ),
            TravelPackage(
                id = "pkg_tour_malaysia",
                title = "Malaysia: Kuala Lumpur & Langkawi Island",
                titleAm = "ማሌዥያ: ኩዋላ ላምፑር እና ላንካዊ ደሴት",
                titleAr = "ماليزيا: كوالالمبور وجزيرة لنكاوي",
                category = PackageCategory.INTERNATIONAL,
                tier = "International Tour",
                priceEtb = 210000L,
                priceUsd = 1680,
                durationDays = 6,
                durationNights = 5,
                hotelGeneral = "Shangri-La Kuala Lumpur & Pelangi Beach Resort",
                inclusions = listOf(
                    "Addis Ababa - Kuala Lumpur return flights",
                    "Petronas Twin Towers Skybridge access",
                    "Batu Caves & Murugan Temple excursion",
                    "Langkawi Island cable car & SkyBridge pass",
                    "Mangrove forest boat safari & eagle feeding",
                    "Halal dining gourmet meals included"
                ),
                exclusions = listOf("Excess baggage charges"),
                description = "Modern tropical luxury in Kuala Lumpur paired with the emerald rainforests and pristine white sand beaches of Langkawi.",
                descriptionAm = "የኩዋላ ላምፑር ዘመናዊ ውበት እና የላንካዊ ውብ የባህር ዳርቻዎች ለእረፍትዎ ተስማሚ ምርጫ።",
                descriptionAr = "أروع عطلة استوائية بين برجي بتروناس وشواطئ لنكاوي الساحرة وطبيعتها الخلابة.",
                destination = "Kuala Lumpur & Langkawi, Malaysia",
                imageUrl = "hero_banner",
                isComingSoon = false,
                isFeatured = false,
                rating = 4.88f,
                reviewCount = 89
            ),
            TravelPackage(
                id = "pkg_tour_thailand",
                title = "Thailand: Bangkok & Phuket Tropical Wonders",
                titleAm = "ታይላንድ: ባንኮክ እና ፉኬት የሐሩር ደሴቶች",
                titleAr = "تايلاند: بانكوك وجزر بوكيت الساحرة",
                category = PackageCategory.INTERNATIONAL,
                tier = "International Tour",
                priceEtb = 215000L,
                priceUsd = 1720,
                durationDays = 6,
                durationNights = 5,
                hotelGeneral = "Anantara Riverside Bangkok & Phuket Beachfront Resort",
                inclusions = listOf(
                    "All flights including Bangkok to Phuket connection",
                    "Phi Phi Islands full-day luxury speedboat tour",
                    "Floating Market & Grand Palace excursion in Bangkok",
                    "Halal friendly dining and city shopping transfers",
                    "Complimentary Thai wellness massage session"
                ),
                exclusions = listOf("Water sports rentals"),
                description = "A vibrant blend of bustling river cities, gilded temples, and turquoise Andaman sea lagoons in the world's favorite tropical sanctuary.",
                descriptionAm = "የባንኮክ ታሪካዊ ቤተ-መንግስት እና የፉኬት ውብ የባህር ደሴቶች አስደሳች ጉዞ።",
                descriptionAr = "جزر بي بي الخلابة وقوارب السرعة وأسواق بانكوك العائمة بأعلى معايير الراحة.",
                destination = "Bangkok & Phuket, Thailand",
                imageUrl = "hero_banner",
                isComingSoon = false,
                isFeatured = false,
                rating = 4.85f,
                reviewCount = 76
            ),
            TravelPackage(
                id = "pkg_tour_egypt",
                title = "Egypt: Pyramids of Giza & 5-Star Nile Cruise",
                titleAm = "ግብፅ: የጊዛ ፒራሚዶች እና የናይል ወንዝ የቅንጦት መርከብ ጉዞ",
                titleAr = "مصر: أهرامات الجيزة وكروز نهر النيل 5 نجوم",
                category = PackageCategory.INTERNATIONAL,
                tier = "International Tour",
                priceEtb = 195000L,
                priceUsd = 1560,
                durationDays = 6,
                durationNights = 5,
                hotelGeneral = "Marriott Mena House Cairo & 5-Star Nile Cruiser",
                inclusions = listOf(
                    "Direct flights Addis Ababa - Cairo with Ethiopian Airlines",
                    "Great Pyramids of Giza & Great Sphinx with private Egyptologist",
                    "Grand Egyptian Museum guided tour",
                    "Luxor to Aswan luxury 5-star Nile cruise full-board",
                    "Valley of the Kings and Karnak Temple entry"
                ),
                exclusions = listOf("Entry inside Great Pyramid chamber"),
                description = "Uncover 5,000 years of majesty along the eternal Nile, crowned by the Pyramids of Giza and ancient pharaonic monuments.",
                descriptionAm = "የጥንታዊቷ ግብፅ ታላላቅ ፒራሚዶች፣ ሙዚየሞች እና በናይል ወንዝ ላይ የሚደረግ የ5 ኮከብ የመርከብ ጉዞ።",
                descriptionAr = "سحر الفراعنة وأهرامات الجيزة مع إبحار فاخر في نهر النيل بين الأقصر وأسوان.",
                destination = "Cairo, Luxor & Aswan, Egypt",
                imageUrl = "hero_banner",
                isComingSoon = false,
                isFeatured = false,
                rating = 4.92f,
                reviewCount = 94
            ),
            TravelPackage(
                id = "pkg_tour_qatar",
                title = "Qatar: Doha Cultural Splendor & Inland Sea",
                titleAm = "ኳታር: የዶሃ ባህላዊ ውበት እና የበረሃ ባህር",
                titleAr = "قطر: روائع الدوحة وخور العديد",
                category = PackageCategory.INTERNATIONAL,
                tier = "International Tour",
                priceEtb = 170000L,
                priceUsd = 1360,
                durationDays = 4,
                durationNights = 3,
                hotelGeneral = "St. Regis Doha 5-Star",
                inclusions = listOf(
                    "Return flights on Qatar Airways / Ethiopian Airlines",
                    "Souq Waqif heritage walking tour & falcon souq",
                    "Museum of Islamic Art and Katara Cultural Village",
                    "Khor Al Adaid Inland Sea desert safari and dune bashing"
                ),
                exclusions = listOf("Personal items"),
                description = "Sophistication, modern Arabian art, and dramatic desert landscapes where golden dunes meet the turquoise Arabian Gulf.",
                descriptionAm = "የዶሃ ውብ ባህል፣ ታላቁ የእስልምና ሙዚየም እና በበረሃ ሳፋሪ አስደሳች የእረፍት ጊዜ።",
                descriptionAr = "عاصمة الأناقة الخليجية: سوق واقف، متحف الفن الإسلامي ومغامرات خور العديد.",
                destination = "Doha, Qatar",
                imageUrl = "hero_banner",
                isComingSoon = false,
                isFeatured = false,
                rating = 4.88f,
                reviewCount = 61
            ),
            TravelPackage(
                id = "pkg_tour_saudi",
                title = "Saudi Arabia: Riyadh, AlUla & Red Sea Odyssey",
                titleAm = "ሳውዲ አረቢያ: ሪያድ፣ አልዑላ እና ቀይ ባህር",
                titleAr = "السعودية: الرياض، العلا والبحر الأحمر",
                category = PackageCategory.INTERNATIONAL,
                tier = "International Tour",
                priceEtb = 225000L,
                priceUsd = 1800,
                durationDays = 5,
                durationNights = 4,
                hotelGeneral = "Habitas AlUla & Ritz-Carlton Riyadh",
                inclusions = listOf(
                    "Flights from Addis Ababa to Riyadh and AlUla",
                    "Saudi Tourist E-Visa",
                    "Hegra UNESCO World Heritage Nabataean tombs",
                    "Elephant Rock sunset experience",
                    "Edge of the World cliff excursion in Riyadh",
                    "Boulevard World and Diriyah heritage site"
                ),
                exclusions = listOf("Special events concerts"),
                description = "Witness the wonders of the Kingdom from Riyadh's futuristic entertainment hubs to the awe-inspiring sandstone canyons of AlUla.",
                descriptionAm = "የሳውዲ አረቢያ ታላቅ የቱሪዝም ድንቅ: የአልዑላ ጥንታዊ ቅርሶች እና የሪያድ ዘመናዊ ውበት።",
                descriptionAr = "اكتشف مدائن صالح في العلا وصخورها الخلابة ونبض الرياض العصري.",
                destination = "Riyadh & AlUla, Saudi Arabia",
                imageUrl = "hero_banner",
                isComingSoon = false,
                isFeatured = false,
                rating = 4.96f,
                reviewCount = 52
            ),
            TravelPackage(
                id = "pkg_tour_singapore",
                title = "Singapore: Lion City & Gardens by the Bay",
                titleAm = "ሲንጋፖር: ዘመናዊቷ ከተማ እና ጋርደንስ ባይ ዘ ቤይ",
                titleAr = "سنغافورة: مدينة المستقبل وحدائق الخليج",
                category = PackageCategory.INTERNATIONAL,
                tier = "International Tour",
                priceEtb = 230000L,
                priceUsd = 1840,
                durationDays = 4,
                durationNights = 3,
                hotelGeneral = "Marina Bay Sands / Pan Pacific Singapore",
                inclusions = listOf(
                    "Return flights from Addis Ababa",
                    "Marina Bay Sands SkyPark Observation Deck",
                    "Gardens by the Bay Flower Dome & Cloud Forest",
                    "Sentosa Island cable car and cable ride",
                    "Night Safari tram expedition"
                ),
                exclusions = listOf("Universal Studios VIP fast pass"),
                description = "Immerse yourself in clean, green architectural genius, glowing supertrees, and high-tech urban wonders.",
                descriptionAm = "የሲንጋፖር ዘመናዊ ህንፃዎች፣ የተፈጥሮ ፓርኮች እና የሴንቶሳ ደሴት ጉብኝት።",
                descriptionAr = "عاصمة الابتكار: حدائق الخليج ومارينا باي ساندز ورحلات السفاري الليلية.",
                destination = "Singapore",
                imageUrl = "hero_banner",
                isComingSoon = false,
                isFeatured = false,
                rating = 4.91f,
                reviewCount = 45
            ),

            // Coming Soon destinations highlighting Al-Awla's international expansion
            TravelPackage(
                id = "pkg_tour_indonesia",
                title = "Indonesia: Bali Islands & Sacred Ubud",
                titleAm = "ኢንዶኔዥያ: ባሊ ደሴት እና ኡቡድ",
                titleAr = "إندونيسيا: سحر بالي وأوبود الخضراء",
                category = PackageCategory.INTERNATIONAL,
                tier = "Coming Soon",
                priceEtb = 210000L,
                priceUsd = 1680,
                durationDays = 7,
                durationNights = 6,
                hotelGeneral = "Luxury Ubud Rainforest Villas",
                inclusions = listOf("Flights", "Villas", "Nusa Penida tour", "Rice terraces"),
                exclusions = listOf(),
                description = "Lush green emerald rice terraces, cliffside temples overlooking crashing ocean waves, and tropical tranquility. Launching next season!",
                descriptionAm = "የባሊ አስደናቂ ተፈጥሮ እና ደሴቶች። በቅርብ ቀን ይጀምራል።",
                descriptionAr = "شواطئ بالي الساحرة وغابات أوبود الاستوائية. قريباً لعملائنا الكرام.",
                destination = "Bali, Indonesia",
                imageUrl = "hero_banner",
                isComingSoon = true,
                isFeatured = false
            ),
            TravelPackage(
                id = "pkg_tour_uk",
                title = "United Kingdom: London Icons & Scottish Castles",
                titleAm = "ዩናይትድ ኪንግደም: ለንደን እና ስኮትላንድ",
                titleAr = "بريطانيا: معالم لندن وقلاع اسكتلندا",
                category = PackageCategory.INTERNATIONAL,
                tier = "Coming Soon",
                priceEtb = 290000L,
                priceUsd = 2320,
                durationDays = 8,
                durationNights = 7,
                hotelGeneral = "Central London & Edinburgh Heritage Hotels",
                inclusions = listOf("London Eye", "Tower of London", "Highlands tour"),
                exclusions = listOf(),
                description = "Big Ben, Westminster Abbey, luxury shopping at Harrods, and the mystic glens of the Scottish Highlands. Launching soon!",
                descriptionAm = "ለንደን እና የስኮትላንድ ጥንታዊ ግንቦች። በቅርብ ቀን ይጀምራል።",
                descriptionAr = "جولة بريطانيا الكبرى بين معالم لندن التاريخية وقلاع اسكتلندا المهيبة. قريباً.",
                destination = "London & Edinburgh, UK",
                imageUrl = "hero_banner",
                isComingSoon = true,
                isFeatured = false
            ),
            TravelPackage(
                id = "pkg_tour_france",
                title = "France: Paris Romance & French Riviera",
                titleAm = "ፈረንሳይ: ፓሪስ እና የፈረንሳይ ሪቪዬራ",
                titleAr = "فرنسا: أضواء باريس والريفييرا الساحرة",
                category = PackageCategory.INTERNATIONAL,
                tier = "Coming Soon",
                priceEtb = 280000L,
                priceUsd = 2240,
                durationDays = 7,
                durationNights = 6,
                hotelGeneral = "Boutique Parisian & Cannes Coast Hotels",
                inclusions = listOf("Eiffel Tower Summit", "Louvre Museum", "Cannes promenade"),
                exclusions = listOf(),
                description = "The City of Light, world-renowned museums, haute couture, and the Mediterranean glamour of Nice and Monaco. Coming soon!",
                descriptionAm = "የፓሪስ ውበት፣ የኤፍል ታወር እና የሜድትራኒያን የባህር ዳርቻ። በቅርብ ቀን።",
                descriptionAr = "أجواء باريس الراقية وبرج إيفل وسحر شواطئ كان ونيس. قريباً.",
                destination = "Paris & Nice, France",
                imageUrl = "hero_banner",
                isComingSoon = true,
                isFeatured = false
            ),
            TravelPackage(
                id = "pkg_tour_italy",
                title = "Italy: Rome, Florence & Venice Gondolas",
                titleAm = "ጣሊያን: ሮም፣ ፍሎረንስ እና የቬኒስ ጎንዶላ",
                titleAr = "إيطاليا: روما التاريخية وقنوات فينيسيا",
                category = PackageCategory.INTERNATIONAL,
                tier = "Coming Soon",
                priceEtb = 285000L,
                priceUsd = 2280,
                durationDays = 8,
                durationNights = 7,
                hotelGeneral = "Historic Central Italian Hotels",
                inclusions = listOf("Colosseum & Vatican", "Florence Uffizi", "Venice Gondola"),
                exclusions = listOf(),
                description = "The ancient Colosseum, Renaissance masterworks in Florence, and romantic gondola glides through the canals of Venice. Coming soon!",
                descriptionAm = "የሮም ታሪክ፣ የፍሎረንስ ጥበብ እና የቬኒስ ድንቅ የቦይ ጉዞ። በቅርብ ቀን።",
                descriptionAr = "رحلة في قلب التاريخ الإيطالي وقنوات البندقية الساحرة. قريباً.",
                destination = "Rome, Florence & Venice, Italy",
                imageUrl = "hero_banner",
                isComingSoon = true,
                isFeatured = false
            )
        )
    }

    private fun seedInitialReviews() {
        _reviews.value = listOf(
            Review(
                id = "rev_1",
                packageId = "pkg_umrah_std",
                packageName = "Standard Umrah Package",
                userName = "Ustaz Abdurrahman K.",
                userCountry = "Addis Ababa, Ethiopia",
                rating = 5,
                comment = "Al-Awla made our pilgrimage so peaceful and well-coordinated. The hotel bus service in Makkah was prompt, and the Mutawwif scholar guided us step-by-step through our Umrah rites. Masha'Allah!",
                date = "Sep 14, 2026"
            ),
            Review(
                id = "rev_2",
                packageId = "pkg_umrah_vip",
                packageName = "VIP Umrah Package",
                userName = "Selamawit & Fuad M.",
                userCountry = "Silver Spring, MD (Diaspora)",
                rating = 5,
                comment = "I sponsored my parents from Ethiopia through the Diaspora Sponsorship feature. Al-Awla handled every guarantee, flight booking, and provided a private room. My parents couldn't stop praising the team!",
                date = "Aug 29, 2026"
            ),
            Review(
                id = "rev_3",
                packageId = "pkg_tour_dubai",
                packageName = "Dubai Luxury Tour",
                userName = "Dawit Tadesse",
                userCountry = "Addis Ababa, Ethiopia",
                rating = 5,
                comment = "The private yacht trip in Dubai Marina and the desert 4x4 safari were unforgettable. Professional guides, 5-star hotel, and zero visa delays.",
                date = "Aug 12, 2026"
            ),
            Review(
                id = "rev_4",
                packageId = "pkg_umrah_vvip",
                packageName = "VVIP Haram-Front Luxury",
                userName = "Hajj Jemal Beshir",
                userCountry = "Dire Dawa, Ethiopia",
                rating = 5,
                comment = "Staying at Sheraton Jabal Al Kaaba and taking the high-speed Haramain bullet train to Madinah was an incredible experience. Al-Awla delivers genuine 5-star luxury.",
                date = "Jul 22, 2026"
            )
        )
    }

    private fun seedInitialDataIfEmpty() {
        scope.launch {
            // Seed a representative initial booking if database is fresh
            val user = _currentUser.value
            val existing = db.bookingDao().getBookingById("book_seed_01")
            if (existing == null) {
                val seedBooking = BookingEntity(
                    id = "book_seed_01",
                    bookingReference = "ALW-2026-7891",
                    packageId = "pkg_umrah_std",
                    packageName = "Standard Umrah Package (10 Nights)",
                    category = "UMRAH",
                    travelerName = user.name,
                    phone = user.phone,
                    email = user.email,
                    gender = "Male",
                    dateOfBirth = "1990-05-14",
                    nationality = user.nationality,
                    passportNumber = user.passportNumber,
                    emergencyContactName = "Ahmed Abdusomed",
                    emergencyContactPhone = "+251 911 345 678",
                    travelDate = "2026-11-15",
                    numberOfTravelers = 2,
                    status = BookingStatus.APPROVED.name,
                    totalAmountEtb = 300000L,
                    notes = "Diaspora payment confirmed. Ground transfer requested.",
                    uploadedPassportUrl = "passport_scan_verified.jpg",
                    uploadedPhotoUrl = "photo_white_bg.jpg",
                    uploadedVisaDocUrl = "saudi_visa_valid.pdf",
                    adminNotes = "Visa issued successfully. Flight seats confirmed with Ethiopian Airlines.",
                    createdAt = System.currentTimeMillis() - 86400000L * 3
                )
                db.bookingDao().insertBooking(seedBooking)

                val welcomeChat1 = ChatMessageEntity(
                    id = "chat_welcome_01",
                    senderId = "agent_system",
                    senderName = "Al-Awla Concierge Desk",
                    senderRole = "SUPPORT_AGENT",
                    message = "As-salamu alaykum and welcome to Al-Awla Tour & Travel Services! Our team in Addis Ababa & Jeddah is at your service 24/7. Feel free to ask about Umrah packages, flight schedules, visa processing, or international vacations.",
                    timestamp = System.currentTimeMillis() - 3600000L * 4,
                    bookingReference = null,
                    isAgent = true
                )
                val welcomeChat2 = ChatMessageEntity(
                    id = "chat_welcome_02",
                    senderId = "agent_system",
                    senderName = "Al-Awla Concierge Desk",
                    senderRole = "SUPPORT_AGENT",
                    message = "Your booking #ALW-2026-7891 has been verified and Approved. All documents are in order.",
                    timestamp = System.currentTimeMillis() - 3600000L * 2,
                    bookingReference = "ALW-2026-7891",
                    isAgent = true
                )
                db.chatDao().insertMessage(welcomeChat1)
                db.chatDao().insertMessage(welcomeChat2)

                val notif = NotificationEntity(
                    id = "notif_welcome",
                    title = "Welcome to Al-Awla Tour & Travel",
                    message = "Explore sacred journeys to Makkah & Madinah and premium international tours to Dubai, Turkey, Malaysia, and beyond.",
                    type = "AGENCY_ANNOUNCEMENT",
                    timestamp = System.currentTimeMillis(),
                    isRead = false
                )
                db.notificationDao().insertNotification(notif)
            }
        }
    }
}
