package com.vishalsharma.whatwewear.presentation.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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

@Composable
fun SignupScreen(
    onLoginClick: () -> Unit,
    onSignupSuccess: () -> Unit
) {

    val viewModel: SignupViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.signupSuccess) {
        if (uiState.signupSuccess) {
            onSignupSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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

        if (uiState.error != null) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = uiState.error!!,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = if (uiState.isLoading) {
                "Creating Account..."
            } else {
                "Create Account"
            },
            onClick = {
                if (!uiState.isLoading) {
                    viewModel.signup()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthDivider()

        Spacer(modifier = Modifier.height(16.dp))

        OAuthButton(
            text = "Continue with Google",
            icon = R.drawable.google_logo,
            onClick = {
                // Google Sign-In (Next)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Already have an account?",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            TextButton(
                onClick = onLoginClick
            ) {

                Text(
                    text = "Sign In",
                    color = Primary
                )

            }

        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}