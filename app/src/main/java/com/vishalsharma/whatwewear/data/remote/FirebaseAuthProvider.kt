package com.vishalsharma.whatwewear.data.remote

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthProvider @Inject constructor(
    val auth: FirebaseAuth
) {
}