package com.example.data.model

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    AMHARIC("am", "Amharic", "አማርኛ"),
    ARABIC("ar", "Arabic", "العربية")
}

enum class PackageCategory {
    UMRAH,
    INTERNATIONAL
}

enum class BookingStatus(val label: String, val amharic: String, val arabic: String) {
    PENDING("Pending", "በመጠባበቅ ላይ", "قيد الانتظار"),
    UNDER_REVIEW("Under Review", "በግምገማ ላይ", "قيد المراجعة"),
    DOCUMENTS_REQUIRED("Documents Required", "ሰነዶች ያስፈልጋሉ", "مطلوب مستندات"),
    APPROVED("Approved", "ተቀባይነት አግኝቷል", "تمت الموافقة"),
    REJECTED("Rejected", "ተቀባይነት አላገኘም", "مرفوض"),
    COMPLETED("Completed", "ተጠናቋል", "مكتمل")
}

data class TravelPackage(
    val id: String,
    val title: String,
    val titleAm: String,
    val titleAr: String,
    val category: PackageCategory,
    val tier: String, // "Standard", "VIP", "VVIP", "VVIP Premium", "Ramadan Special"
    val priceEtb: Long,
    val priceUsd: Int,
    val durationDays: Int,
    val durationNights: Int,
    val hotelMakkah: String = "",
    val hotelMadinah: String = "",
    val hotelGeneral: String = "",
    val occupancy: String = "", // "4 Persons / Room", "2 Persons / Room", etc.
    val inclusions: List<String> = emptyList(),
    val inclusionsAm: List<String> = emptyList(),
    val inclusionsAr: List<String> = emptyList(),
    val exclusions: List<String> = emptyList(),
    val description: String,
    val descriptionAm: String,
    val descriptionAr: String,
    val destination: String,
    val imageUrl: String,
    val isComingSoon: Boolean = false,
    val isFeatured: Boolean = false,
    val rating: Float = 4.9f,
    val reviewCount: Int = 128
)

data class Booking(
    val id: String,
    val bookingReference: String,
    val packageId: String,
    val packageName: String,
    val category: PackageCategory,
    val travelerName: String,
    val phone: String,
    val email: String,
    val gender: String,
    val dateOfBirth: String,
    val nationality: String,
    val passportNumber: String,
    val emergencyContactName: String,
    val emergencyContactPhone: String,
    val travelDate: String,
    val numberOfTravelers: Int,
    val status: BookingStatus,
    val totalAmountEtb: Long,
    val notes: String = "",
    val uploadedPassportUrl: String = "",
    val uploadedPhotoUrl: String = "",
    val uploadedVisaDocUrl: String = "",
    val adminNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class ChatMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val senderRole: String, // "USER", "SUPPORT_AGENT", "SYSTEM"
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val bookingReference: String? = null,
    val isAgent: Boolean = false
)

data class Review(
    val id: String,
    val packageId: String,
    val packageName: String,
    val userName: String,
    val userCountry: String,
    val rating: Int,
    val comment: String,
    val date: String
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: String, // "BOOKING_UPDATE", "PROMOTION", "AGENCY_ANNOUNCEMENT"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class UserProfile(
    val id: String = "user_guest_01",
    val name: String = "Merwan Abdusomed",
    val email: String = "merwanabdusomed@gmail.com",
    val phone: String = "+251 911 955 8887",
    val passportNumber: String = "EP7849201",
    val nationality: String = "Ethiopian",
    val isAdmin: Boolean = false,
    val isLoggedIn: Boolean = true,
    val firebaseUid: String? = null
)
