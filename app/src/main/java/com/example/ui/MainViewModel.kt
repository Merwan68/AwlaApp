package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppLanguage
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.ChatMessage
import com.example.data.model.NotificationItem
import com.example.data.model.PackageCategory
import com.example.data.model.Review
import com.example.data.model.TravelPackage
import com.example.data.model.UserProfile
import com.example.data.repository.TravelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    HOME,
    UMRAH,
    TOURS,
    PACKAGE_DETAIL,
    BOOKING_FORM,
    BOOKING_SUCCESS,
    MY_BOOKINGS,
    BOOKING_DETAIL,
    CHAT_SUPPORT,
    CONTACT,
    PROFILE,
    ADMIN_DASHBOARD,
    AUTH,
    CUSTOMER_DASHBOARD
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = TravelRepository(application.applicationContext, db)

    // Auth State
    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    private val _authErrorMessage = MutableStateFlow<String?>(null)
    val authErrorMessage: StateFlow<String?> = _authErrorMessage.asStateFlow()

    private val _authSuccessMessage = MutableStateFlow<String?>(null)
    val authSuccessMessage: StateFlow<String?> = _authSuccessMessage.asStateFlow()

    // Language & Theme State
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Navigation State
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _screenHistory = mutableListOf<AppScreen>()

    // Detail & Selection States
    private val _selectedPackage = MutableStateFlow<TravelPackage?>(null)
    val selectedPackage: StateFlow<TravelPackage?> = _selectedPackage.asStateFlow()

    private val _selectedBooking = MutableStateFlow<Booking?>(null)
    val selectedBooking: StateFlow<Booking?> = _selectedBooking.asStateFlow()

    private val _recentCreatedBooking = MutableStateFlow<Booking?>(null)
    val recentCreatedBooking: StateFlow<Booking?> = _recentCreatedBooking.asStateFlow()

    private val _chatBookingRef = MutableStateFlow<String?>(null)
    val chatBookingRef: StateFlow<String?> = _chatBookingRef.asStateFlow()

    // Filter states
    val searchQuery = MutableStateFlow("")
    val umrahTierFilter = MutableStateFlow("All")
    val tourFilter = MutableStateFlow("All")

    // Repositories mapped flows
    val packages = repository.packages
    val reviews = repository.reviews
    val isAgentTyping = repository.isAgentTyping
    val currentUser = repository.currentUser

    val allBookings: StateFlow<List<Booking>> = repository.allBookings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allChatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val notifications: StateFlow<List<NotificationItem>> = repository.notifications.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            _screenHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (_screenHistory.isNotEmpty()) {
            val prev = _screenHistory.removeAt(_screenHistory.size - 1)
            _currentScreen.value = prev
            return true
        }
        if (_currentScreen.value != AppScreen.HOME) {
            _currentScreen.value = AppScreen.HOME
            return true
        }
        return false
    }

    fun viewPackageDetail(pkg: TravelPackage) {
        _selectedPackage.value = pkg
        navigateTo(AppScreen.PACKAGE_DETAIL)
    }

    fun startBooking(pkg: TravelPackage) {
        _selectedPackage.value = pkg
        navigateTo(AppScreen.BOOKING_FORM)
    }

    fun viewBookingDetail(booking: Booking) {
        _selectedBooking.value = booking
        navigateTo(AppScreen.BOOKING_DETAIL)
    }

    fun selectBooking(booking: Booking) {
        _selectedBooking.value = booking
    }

    fun openChatWithBooking(bookingRef: String?) {
        _chatBookingRef.value = bookingRef
        navigateTo(AppScreen.CHAT_SUPPORT)
    }

    fun clearChatBookingRef() {
        _chatBookingRef.value = null
    }

    fun submitBooking(
        pkg: TravelPackage,
        travelerName: String,
        phone: String,
        email: String,
        gender: String,
        dob: String,
        nationality: String,
        passportNumber: String,
        emergencyName: String,
        emergencyPhone: String,
        travelDate: String,
        travelersCount: Int,
        notes: String,
        hasPassportDoc: Boolean,
        hasPhotoDoc: Boolean,
        hasVisaDoc: Boolean
    ) {
        viewModelScope.launch {
            val randomSuffix = (1000..9999).random()
            val ref = "ALW-2026-$randomSuffix"
            val total = pkg.priceEtb * travelersCount

            val booking = Booking(
                id = UUID.randomUUID().toString(),
                bookingReference = ref,
                packageId = pkg.id,
                packageName = pkg.title,
                category = pkg.category,
                travelerName = travelerName.ifBlank { currentUser.value.name },
                phone = phone.ifBlank { currentUser.value.phone },
                email = email.ifBlank { currentUser.value.email },
                gender = gender,
                dateOfBirth = dob,
                nationality = nationality.ifBlank { currentUser.value.nationality },
                passportNumber = passportNumber.ifBlank { currentUser.value.passportNumber },
                emergencyContactName = emergencyName,
                emergencyContactPhone = emergencyPhone,
                travelDate = travelDate,
                numberOfTravelers = travelersCount,
                status = BookingStatus.PENDING,
                totalAmountEtb = total,
                notes = notes,
                uploadedPassportUrl = if (hasPassportDoc) "passport_verified.jpg" else "",
                uploadedPhotoUrl = if (hasPhotoDoc) "photo_white_bg.jpg" else "",
                uploadedVisaDocUrl = if (hasVisaDoc) "visa_doc.pdf" else "",
                createdAt = System.currentTimeMillis()
            )

            val saved = repository.createBooking(booking)
            _recentCreatedBooking.value = saved
            _selectedBooking.value = saved
            navigateTo(AppScreen.BOOKING_SUCCESS)
        }
    }

    fun sendChatMessage(text: String, bookingRef: String? = null) {
        repository.sendMessage(text, bookingRef ?: _chatBookingRef.value)
    }

    fun updateBookingStatus(id: String, status: BookingStatus, adminNotes: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(id, status, adminNotes)
            // Refresh selected booking if open
            if (_selectedBooking.value?.id == id) {
                _selectedBooking.value = _selectedBooking.value?.copy(status = status, adminNotes = adminNotes)
            }
        }
    }

    fun submitReview(packageId: String, packageName: String, rating: Int, comment: String) {
        repository.submitReview(packageId, packageName, rating, comment)
    }

    fun broadcastAnnouncement(title: String, message: String) {
        repository.broadcastAnnouncement(title, message)
    }

    fun toggleAdminMode(enabled: Boolean) {
        repository.toggleAdminMode(enabled)
    }

    fun updateProfile(name: String, email: String, phone: String, passport: String, nationality: String) {
        repository.updateUserProfile(name, email, phone, passport, nationality)
    }

    fun updatePackagePrice(packageId: String, newPriceEtb: Long) {
        repository.updatePackagePrice(packageId, newPriceEtb)
    }

    fun updatePackage(pkg: TravelPackage) {
        repository.updatePackage(pkg)
    }

    fun createPackage(newPkg: TravelPackage) {
        repository.addPackage(newPkg)
    }

    fun deletePackage(packageId: String) {
        repository.deletePackage(packageId)
    }

    // Firebase Auth & Cloud Firestore actions
    fun isFirebaseConnected(): Boolean {
        return repository.firestoreService.isFirestoreAvailable()
    }

    fun login(email: String, pass: String, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _authLoading.value = true
            _authErrorMessage.value = null
            _authSuccessMessage.value = null

            val result = repository.signIn(email, pass)
            _authLoading.value = false

            result.onSuccess { profile ->
                _authSuccessMessage.value = "Welcome back, ${profile.name}!"
                onComplete(true)
                if (profile.isAdmin) {
                    navigateTo(AppScreen.ADMIN_DASHBOARD)
                } else {
                    navigateTo(AppScreen.CUSTOMER_DASHBOARD)
                }
            }.onFailure { err ->
                _authErrorMessage.value = err.message ?: "Authentication failed. Please check credentials."
                onComplete(false)
            }
        }
    }

    fun register(email: String, pass: String, name: String, phone: String, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _authLoading.value = true
            _authErrorMessage.value = null
            _authSuccessMessage.value = null

            val result = repository.signUp(email, pass, name, phone)
            _authLoading.value = false

            result.onSuccess { profile ->
                _authSuccessMessage.value = "Account created successfully for ${profile.name}!"
                onComplete(true)
                navigateTo(AppScreen.CUSTOMER_DASHBOARD)
            }.onFailure { err ->
                _authErrorMessage.value = err.message ?: "Registration failed."
                onComplete(false)
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authLoading.value = true
            val result = repository.resetPassword(email)
            _authLoading.value = false
            result.onSuccess {
                _authSuccessMessage.value = "Password reset instructions sent to $email."
            }.onFailure { err ->
                _authErrorMessage.value = err.message ?: "Could not send reset email."
            }
        }
    }

    fun logout() {
        repository.signOut()
        _authSuccessMessage.value = null
        _authErrorMessage.value = null
        navigateTo(AppScreen.HOME)
    }

    fun loginWithDemoProfile(isAdmin: Boolean) {
        if (isAdmin) {
            val adminUser = UserProfile(
                id = "admin_01",
                name = "Al-Awla Admin (Bethel Office)",
                email = "admin@alawlatourtravel.com",
                phone = "+251 911 955 8887",
                passportNumber = "ADM001",
                nationality = "Ethiopian",
                isAdmin = true,
                isLoggedIn = true
            )
            repository.setQuickUser(adminUser)
            navigateTo(AppScreen.ADMIN_DASHBOARD)
        } else {
            val customerUser = UserProfile(
                id = "usr_alawla_01",
                name = "Merwan Abdusomed",
                email = "merwanabdusomed@gmail.com",
                phone = "+251 911 955 8887",
                passportNumber = "EP892304",
                nationality = "Ethiopian",
                isAdmin = false,
                isLoggedIn = true
            )
            repository.setQuickUser(customerUser)
            navigateTo(AppScreen.CUSTOMER_DASHBOARD)
        }
    }

    fun clearAuthMessages() {
        _authErrorMessage.value = null
        _authSuccessMessage.value = null
    }
}
