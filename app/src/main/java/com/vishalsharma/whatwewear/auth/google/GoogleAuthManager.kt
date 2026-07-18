package com.vishalsharma.whatwewear.auth.google

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption

class GoogleAuthManager {

    suspend fun signIn(
        activity: Activity,
        serverClientId: String
    ): GetCredentialResponse {

        val credentialManager =
            CredentialManager.create(activity)

        val option =
            GetSignInWithGoogleOption.Builder(
                serverClientId
            ).build()

        val request =
            GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build()

        return credentialManager.getCredential(
            context = activity,
            request = request
        )
    }
}