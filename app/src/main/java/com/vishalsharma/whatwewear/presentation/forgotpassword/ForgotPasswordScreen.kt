package com.vishalsharma.whatwewear.presentation.forgotpassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vishalsharma.whatwewear.presentation.components.buttons.PrimaryButton
import com.vishalsharma.whatwewear.presentation.components.textfields.PrimaryTextField
import com.vishalsharma.whatwewear.ui.theme.Dimens

@Composable
fun ForgotPasswordScreen(
    onEmailSent: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.emailSent) {

        if (uiState.emailSent) {
            onEmailSent()
        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.PaddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Forgot Password",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Enter your email and we'll send you a password reset link.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = Dimens.PaddingSmall)
        )

        PrimaryTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChanged,
            label = "Email",
            placeholder = "Enter your email",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.PaddingLarge)
        )

        uiState.error?.let {

            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(top = Dimens.PaddingSmall)
                    .fillMaxWidth()
            )

        }

        PrimaryButton(
            text = if (uiState.isLoading)
                "Sending..."
            else
                "Send Reset Link",
            onClick = viewModel::sendResetLink,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.PaddingLarge)
        )

    }

}