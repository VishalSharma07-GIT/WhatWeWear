package com.vishalsharma.whatwewear.presentation.signup

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.vishalsharma.whatwewear.R
import com.vishalsharma.whatwewear.presentation.auth.LoginViewModel
import com.vishalsharma.whatwewear.presentation.components.buttons.OAuthButton
import com.vishalsharma.whatwewear.presentation.components.buttons.PrimaryButton
import com.vishalsharma.whatwewear.presentation.components.common.AuthDivider
import com.vishalsharma.whatwewear.presentation.components.textfields.PasswordTextField
import com.vishalsharma.whatwewear.presentation.components.textfields.PrimaryTextField
import com.vishalsharma.whatwewear.ui.theme.Dimens
import com.vishalsharma.whatwewear.ui.theme.Primary
import com.vishalsharma.whatwewear.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun SignupScreen(
    onLoginClick: () -> Unit,
    onSignupSuccess: () -> Unit
) {
    val viewModel: SignupViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Google sign-in reuses the login screen's logic. Firebase creates the account on first use.
    val googleViewModel: LoginViewModel = hiltViewModel()
    val googleState by googleViewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(activity) }
    val googleRequest = remember {
        GetCredentialRequest.Builder()
            .addCredentialOption(
                GetSignInWithGoogleOption.Builder(
                    context.getString(R.string.default_web_client_id)
                ).build()
            )
            .build()
    }

    LaunchedEffect(uiState.signupSuccess) {
        if (uiState.signupSuccess) onSignupSuccess()
    }

    LaunchedEffect(googleState.loginSuccess) {
        if (googleState.loginSuccess) onSignupSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.PaddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.applogo3),
            contentDescription = "App Logo",
            modifier = Modifier
                .width(110.dp)
                .padding(top = 40.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Create your wardrobe account",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryTextField(
            value = uiState.fullName,
            onValueChange = viewModel::onFullNameChanged,
            label = "Full Name",
            placeholder = "Enter your full name",
            leadingIcon = Icons.Outlined.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            onValueChange = viewModel::onPasswordChanged,
            label = "Password",
            placeholder = "Enter your password"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            value = uiState.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChanged,
            label = "Confirm Password",
            placeholder = "Confirm your password"
        )

        val errorMessage = uiState.error ?: googleState.error
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = if (uiState.isLoading) "Creating Account..." else "Create Account",
            onClick = {
                if (!uiState.isLoading) viewModel.signup()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthDivider()

        Spacer(modifier = Modifier.height(16.dp))

        OAuthButton(
            text = "Continue with Google",
            icon = R.drawable.google_logo,
            onClick = {
                scope.launch {
                    try {
                        val result = credentialManager.getCredential(
                            context = activity,
                            request = googleRequest
                        )
                        val credential = result.credential
                        if (credential is CustomCredential &&
                            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                        ) {
                            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                            googleViewModel.signInWithGoogle(googleCredential.idToken)
                        }
                    } catch (e: GetCredentialCancellationException) {
                        // The user closed the Google sheet. That's not an error.
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            context,
                            "Google sign-in didn't work. Please try again.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Already have an account?",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(onClick = onLoginClick) {
                Text(
                    text = "Sign In",
                    color = Primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}