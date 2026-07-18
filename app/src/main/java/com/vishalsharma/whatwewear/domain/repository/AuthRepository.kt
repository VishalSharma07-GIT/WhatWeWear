package com.vishalsharma.whatwewear.domain.repository

import com.vishalsharma.whatwewear.domain.model.AuthResult

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): AuthResult

    suspend fun signup(
        fullName: String,
        email: String,
        password: String
    ): AuthResult

    suspend fun resetPassword(
        email: String
    ): AuthResult

    suspend fun signInWithGoogle(
        idToken: String
    ): AuthResult
}