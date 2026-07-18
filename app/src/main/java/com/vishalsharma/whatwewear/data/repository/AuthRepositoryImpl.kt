package com.vishalsharma.whatwewear.data.repository

import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.vishalsharma.whatwewear.data.remote.FirebaseAuthProvider
import com.vishalsharma.whatwewear.domain.model.AuthResult
import com.vishalsharma.whatwewear.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): AuthResult {

        return try {

            firebaseAuthProvider.auth
                .signInWithEmailAndPassword(
                    email,
                    password
                )
                .await()

            AuthResult.Success

        } catch (e: Exception) {

            AuthResult.Error(
                message = e.localizedMessage
                    ?: "Unable to sign in. Please try again."
            )

        }

    }
    override suspend fun signup(
        fullName: String,
        email: String,
        password: String
    ): AuthResult {

        return try {

            firebaseAuthProvider.auth
                .createUserWithEmailAndPassword(
                    email,
                    password
                )
                .await()

            firebaseAuthProvider.auth.currentUser
                ?.updateProfile(
                    userProfileChangeRequest {
                        displayName = fullName
                    }
                )
                ?.await()

            AuthResult.Success

        } catch (e: Exception) {

            AuthResult.Error(
                e.message ?: "Something went wrong"
            )

        }

    }

    override suspend fun resetPassword(
        email: String
    ): AuthResult{
        return try {

            firebaseAuthProvider.auth
                .sendPasswordResetEmail(email)
                .await()
            AuthResult.Success
        }catch (e: Exception){

            AuthResult.Error(
                e.message ?: "Something went wrong"
            )
        }
    }
    override suspend fun signInWithGoogle(
        idToken: String
    ): AuthResult {

        return try {

            val credential = GoogleAuthProvider.getCredential(
                idToken,
                null
            )

            firebaseAuthProvider.auth
                .signInWithCredential(credential)
                .await()

            AuthResult.Success

        } catch (e: Exception) {

            AuthResult.Error(
                e.message ?: "Google Sign-In failed"
            )

        }

    }
}