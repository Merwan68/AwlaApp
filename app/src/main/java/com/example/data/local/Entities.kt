package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.ChatMessage
import com.example.data.model.NotificationItem
import com.example.data.model.PackageCategory

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val bookingReference: String,
    val packageId: String,
    val packageName: String,
    val category: String,
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
    val status: String,
    val totalAmountEtb: Long,
    val notes: String,
    val uploadedPassportUrl: String,
    val uploadedPhotoUrl: String,
    val uploadedVisaDocUrl: String,
    val adminNotes: String,
    val createdAt: Long
) {
    fun toDomain(): Booking = Booking(
        id = id,
        bookingReference = bookingReference,
        packageId = packageId,
        packageName = packageName,
        category = try { PackageCategory.valueOf(category) } catch (e: Exception) { PackageCategory.UMRAH },
        travelerName = travelerName,
        phone = phone,
        email = email,
        gender = gender,
        dateOfBirth = dateOfBirth,
        nationality = nationality,
        passportNumber = passportNumber,
        emergencyContactName = emergencyContactName,
        emergencyContactPhone = emergencyContactPhone,
        travelDate = travelDate,
        numberOfTravelers = numberOfTravelers,
        status = try { BookingStatus.valueOf(status) } catch (e: Exception) { BookingStatus.PENDING },
        totalAmountEtb = totalAmountEtb,
        notes = notes,
        uploadedPassportUrl = uploadedPassportUrl,
        uploadedPhotoUrl = uploadedPhotoUrl,
        uploadedVisaDocUrl = uploadedVisaDocUrl,
        adminNotes = adminNotes,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(b: Booking): BookingEntity = BookingEntity(
            id = b.id,
            bookingReference = b.bookingReference,
            packageId = b.packageId,
            packageName = b.packageName,
            category = b.category.name,
            travelerName = b.travelerName,
            phone = b.phone,
            email = b.email,
            gender = b.gender,
            dateOfBirth = b.dateOfBirth,
            nationality = b.nationality,
            passportNumber = b.passportNumber,
            emergencyContactName = b.emergencyContactName,
            emergencyContactPhone = b.emergencyContactPhone,
            travelDate = b.travelDate,
            numberOfTravelers = b.numberOfTravelers,
            status = b.status.name,
            totalAmountEtb = b.totalAmountEtb,
            notes = b.notes,
            uploadedPassportUrl = b.uploadedPassportUrl,
            uploadedPhotoUrl = b.uploadedPhotoUrl,
            uploadedVisaDocUrl = b.uploadedVisaDocUrl,
            adminNotes = b.adminNotes,
            createdAt = b.createdAt
        )
    }
}

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val senderId: String,
    val senderName: String,
    val senderRole: String,
    val message: String,
    val timestamp: Long,
    val bookingReference: String?,
    val isAgent: Boolean
) {
    fun toDomain(): ChatMessage = ChatMessage(
        id = id,
        senderId = senderId,
        senderName = senderName,
        senderRole = senderRole,
        message = message,
        timestamp = timestamp,
        bookingReference = bookingReference,
        isAgent = isAgent
    )

    companion object {
        fun fromDomain(m: ChatMessage): ChatMessageEntity = ChatMessageEntity(
            id = m.id,
            senderId = m.senderId,
            senderName = m.senderName,
            senderRole = m.senderRole,
            message = m.message,
            timestamp = m.timestamp,
            bookingReference = m.bookingReference,
            isAgent = m.isAgent
        )
    }
}

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String,
    val timestamp: Long,
    val isRead: Boolean
) {
    fun toDomain(): NotificationItem = NotificationItem(
        id = id,
        title = title,
        message = message,
        type = type,
        timestamp = timestamp,
        isRead = isRead
    )

    companion object {
        fun fromDomain(n: NotificationItem): NotificationEntity = NotificationEntity(
            id = n.id,
            title = n.title,
            message = n.message,
            type = n.type,
            timestamp = n.timestamp,
            isRead = n.isRead
        )
    }
}
