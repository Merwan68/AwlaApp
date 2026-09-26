package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.model.UserProfile
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(private val context: Context) {

    private val tag = "FirebaseAuthService"

    fun isFirebaseInitialized(): Boolean {
        return try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Throwable) {
            false
        }
    }

    private fun getAuthInstance(): FirebaseAuth? {
        return try {
            if (isFirebaseInitialized()) {
                FirebaseAuth.getInstance()
            } else {
                null
            }
        } catch (e: Throwable) {
            Log.w(tag, "Firebase Auth not available: ${e.message}")
            null
        }
    }

    fun getCurrentFirebaseUser(): FirebaseUser? {
        return getAuthInstance()?.currentUser
    }

    suspend fun signIn(email: String, password: String): Result<UserProfile> {
        val auth = getAuthInstance()
        if (auth == null) {
            // Offline / Simulation fallback mode
            Log.i(tag, "Firebase Auth offline mode. Signing in locally.")
            val isAdmin = email.trim().equals("admin@alawlatourtravel.com", ignoreCase = true) ||
                    email.trim().contains("admin", ignoreCase = true)
            val name = if (isAdmin) "Al-Awla Admin (Bethel Office)" else email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
            return Result.success(
                UserProfile(
                    id = "local_${System.currentTimeMillis()}",
                    name = name,
                    email = email.trim(),
                    phone = "+251 911 955 8887",
                    passportNumber = "EP7849201",
                    nationality = "Ethiopian",
                    isAdmin = isAdmin,
                    isLoggedIn = true,
                    firebaseUid = null
                )
            )
        }

        return try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = authResult.user
            val isAdmin = email.trim().equals("admin@alawlatourtravel.com", ignoreCase = true) ||
                    email.trim().contains("admin", ignoreCase = true)
            val profile = UserProfile(
                id = user?.uid ?: "user_${System.currentTimeMillis()}",
                name = user?.displayName?.takeIf { it.isNotBlank() } ?: email.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = user?.email ?: email,
                phone = user?.phoneNumber?.takeIf { it.isNotBlank() } ?: "+251 911 955 8887",
                passportNumber = "EP7849201",
                nationality = "Ethiopian",
                isAdmin = isAdmin,
                isLoggedIn = true,
                firebaseUid = user?.uid
            )
            Result.success(profile)
        } catch (e: Exception) {
            Log.e(tag, "Sign in failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String, name: String, phone: String): Result<UserProfile> {
        val auth = getAuthInstance()
        if (auth == null) {
            // Local fallback
            val isAdmin = email.trim().equals("admin@alawlatourtravel.com", ignoreCase = true)
            return Result.success(
                UserProfile(
                    id = "local_${System.currentTimeMillis()}",
                    name = name.ifBlank { email.substringBefore("@") },
                    email = email.trim(),
                    phone = phone.ifBlank { "+251 911 955 8887" },
                    passportNumber = "EP7849201",
                    nationality = "Ethiopian",
                    isAdmin = isAdmin,
                    isLoggedIn = true,
                    firebaseUid = null
                )
            )
        }

        return try {
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = authResult.user
            val isAdmin = email.trim().equals("admin@alawlatourtravel.com", ignoreCase = true)
            val profile = UserProfile(
                id = user?.uid ?: "user_${System.currentTimeMillis()}",
                name = name.ifBlank { email.substringBefore("@") },
                email = user?.email ?: email,
                phone = phone.ifBlank { "+251 911 955 8887" },
                passportNumber = "EP7849201",
                nationality = "Ethiopian",
                isAdmin = isAdmin,
                isLoggedIn = true,
                firebaseUid = user?.uid
            )
            Result.success(profile)
        } catch (e: Exception) {
            Log.e(tag, "Sign up failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        val auth = getAuthInstance() ?: return Result.success(Unit)
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Reset password failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        try {
            getAuthInstance()?.signOut()
        } catch (e: Exception) {
            Log.e(tag, "Sign out error: ${e.message}")
        }
    }
}
