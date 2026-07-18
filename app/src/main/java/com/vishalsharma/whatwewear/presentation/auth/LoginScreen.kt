package com.vishalsharma.whatwewear.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vishalsharma.whatwewear.R
import com.vishalsharma.whatwewear.presentation.components.buttons.OAuthButton
import com.vishalsharma.whatwewear.presentation.components.buttons.PrimaryButton
import com.vishalsharma.whatwewear.presentation.components.common.AuthDivider
import com.vishalsharma.whatwewear.presentation.components.textfields.PasswordTextField
import com.vishalsharma.whatwewear.presentation.components.textfields.PrimaryTextField
import com.vishalsharma.whatwewear.ui.theme.Dimens
import com.vishalsharma.whatwewear.ui.theme.Primary
import com.vishalsharma.whatwewear.ui.theme.TextSecondary
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import android.app.Activity
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()


) {


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val activity = context as Activity

    val scope = rememberCoroutineScope()

    val credentialManager = CredentialManager.create(activity)

    val signInWithGoogleOption =
        GetSignInWithGoogleOption.Builder(
            context.getString(R.string.default_web_client_id)
        ).build()

    val request =
        GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess){
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.PaddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.applogo3),
            contentDescription = "App Logo",
            modifier = Modifier
                .width(110.dp)
                .padding(top = 60.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(40.dp))

        PrimaryTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChanged,
            label = "Email",
            placeholder = "Enter your email",
            leadingIcon = Icons.Outlined.Email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChanged
        )

        if (uiState.error != null) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = uiState.error!!,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Forgot Password?",
            modifier = Modifier
                .align(Alignment.End)
                .clickable {
                    onForgotPasswordClick()
                },
            color = Primary,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = if (uiState.isLoading) "Signing In..." else "Continue",
            onClick = {
                if (!uiState.isLoading) {
                    viewModel.login()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthDivider()

        Spacer(modifier = Modifier.height(24.dp))

        OAuthButton(
            text = "Continue with Google",
            icon = R.drawable.google_logo,
            onClick = {

                scope.launch {

                    try {

                        val result = credentialManager.getCredential(
                            context = activity,
                            request = request
                        )

                        val credential = result.credential

                        if (credential is CustomCredential &&
                            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                        ) {

                            val googleCredential =
                                GoogleIdTokenCredential.createFrom(
                                    credential.data
                                )

                            viewModel.signInWithGoogle(
                                googleCredential.idToken
                            )
                        }

                    } catch (e: GoogleIdTokenParsingException) {

                        e.printStackTrace()

                    } catch (e: Exception) {

                        e.printStackTrace()

                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Don't have an account?",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            TextButton(
                onClick = onSignupClick
            ) {
                Text(
                    text = "Create Account",
                    color = Primary
                )
            }
        }
    }
}
