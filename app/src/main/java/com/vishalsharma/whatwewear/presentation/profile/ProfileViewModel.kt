package com.vishalsharma.whatwewear.presentation.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    val email: String get() = auth.currentUser?.email.orEmpty()

    val name: String
        get() {
            val user = auth.currentUser
            val fromName = user?.displayName?.trim().orEmpty()
            val fromEmail = user?.email?.substringBefore("@").orEmpty()
                .replaceFirstChar { it.uppercase() }
            return fromName.ifBlank { fromEmail }.ifBlank { "Your profile" }
        }

    fun signOut() = auth.signOut()
}