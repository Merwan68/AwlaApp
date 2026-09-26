package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.PackageCategory
import com.example.data.model.TravelPackage
import com.example.data.model.UserProfile
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirestoreService(private val context: Context) {

    private val tag = "FirestoreService"

    fun isFirestoreAvailable(): Boolean {
        return try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Throwable) {
            false
        }
    }

    private fun getFirestoreInstance(): FirebaseFirestore? {
        return try {
            if (isFirestoreAvailable()) {
                FirebaseFirestore.getInstance()
            } else {
                null
            }
        } catch (e: Throwable) {
            Log.w(tag, "Firestore not available: ${e.message}")
            null
        }
    }

    suspend fun saveBooking(booking: Booking): Boolean {
        val firestore = getFirestoreInstance() ?: return false
        return try {
            val bookingMap = hashMapOf(
                "id" to booking.id,
                "bookingReference" to booking.bookingReference,
                "packageId" to booking.packageId,
                "packageName" to booking.packageName,
                "category" to booking.category.name,
                "travelerName" to booking.travelerName,
                "phone" to booking.phone,
                "email" to booking.email,
                "gender" to booking.gender,
                "dateOfBirth" to booking.dateOfBirth,
                "nationality" to booking.nationality,
                "passportNumber" to booking.passportNumber,
                "emergencyContactName" to booking.emergencyContactName,
                "emergencyContactPhone" to booking.emergencyContactPhone,
                "travelDate" to booking.travelDate,
                "numberOfTravelers" to booking.numberOfTravelers,
                "status" to booking.status.name,
                "totalAmountEtb" to booking.totalAmountEtb,
                "notes" to booking.notes,
                "uploadedPassportUrl" to booking.uploadedPassportUrl,
                "uploadedPhotoUrl" to booking.uploadedPhotoUrl,
                "uploadedVisaDocUrl" to booking.uploadedVisaDocUrl,
                "adminNotes" to booking.adminNotes,
                "createdAt" to booking.createdAt,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("bookings")
                .document(booking.id)
                .set(bookingMap, SetOptions.merge())
                .await()
            Log.d(tag, "Booking ${booking.bookingReference} saved to Firestore successfully")
            true
        } catch (e: Exception) {
            Log.e(tag, "Failed to save booking to Firestore: ${e.message}", e)
            false
        }
    }

    suspend fun updateBookingStatus(bookingId: String, newStatus: BookingStatus, adminNotes: String): Boolean {
        val firestore = getFirestoreInstance() ?: return false
        return try {
            val updates = hashMapOf<String, Any>(
                "status" to newStatus.name,
                "adminNotes" to adminNotes,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("bookings")
                .document(bookingId)
                .update(updates)
                .await()
            Log.d(tag, "Booking $bookingId status updated in Firestore to $newStatus")
            true
        } catch (e: Exception) {
            Log.e(tag, "Failed to update booking status in Firestore: ${e.message}", e)
            false
        }
    }

    suspend fun savePackage(pkg: TravelPackage): Boolean {
        val firestore = getFirestoreInstance() ?: return false
        return try {
            val pkgMap = hashMapOf(
                "id" to pkg.id,
                "title" to pkg.title,
                "titleAm" to pkg.titleAm,
                "titleAr" to pkg.titleAr,
                "category" to pkg.category.name,
                "tier" to pkg.tier,
                "priceEtb" to pkg.priceEtb,
                "priceUsd" to pkg.priceUsd,
                "durationDays" to pkg.durationDays,
                "durationNights" to pkg.durationNights,
                "hotelMakkah" to pkg.hotelMakkah,
                "hotelMadinah" to pkg.hotelMadinah,
                "occupancy" to pkg.occupancy,
                "description" to pkg.description,
                "destination" to pkg.destination,
                "imageUrl" to pkg.imageUrl,
                "isFeatured" to pkg.isFeatured,
                "rating" to pkg.rating,
                "reviewCount" to pkg.reviewCount,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("packages")
                .document(pkg.id)
                .set(pkgMap, SetOptions.merge())
                .await()
            Log.d(tag, "Package ${pkg.id} saved to Firestore")
            true
        } catch (e: Exception) {
            Log.e(tag, "Failed to save package to Firestore: ${e.message}", e)
            false
        }
    }

    suspend fun updatePackagePrice(packageId: String, newPriceEtb: Long): Boolean {
        val firestore = getFirestoreInstance() ?: return false
        return try {
            firestore.collection("packages")
                .document(packageId)
                .update("priceEtb", newPriceEtb, "updatedAt", System.currentTimeMillis())
                .await()
            Log.d(tag, "Package $packageId price updated in Firestore to ETB $newPriceEtb")
            true
        } catch (e: Exception) {
            Log.e(tag, "Failed to update package price in Firestore: ${e.message}", e)
            false
        }
    }

    suspend fun saveUserProfile(profile: UserProfile): Boolean {
        val firestore = getFirestoreInstance() ?: return false
        return try {
            val userMap = hashMapOf(
                "id" to profile.id,
                "name" to profile.name,
                "email" to profile.email,
                "phone" to profile.phone,
                "passportNumber" to profile.passportNumber,
                "nationality" to profile.nationality,
                "isAdmin" to profile.isAdmin,
                "firebaseUid" to profile.firebaseUid,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("users")
                .document(profile.email.replace(".", "_"))
                .set(userMap, SetOptions.merge())
                .await()
            Log.d(tag, "User ${profile.email} profile saved to Firestore")
            true
        } catch (e: Exception) {
            Log.e(tag, "Failed to save user profile to Firestore: ${e.message}", e)
            false
        }
    }
}
