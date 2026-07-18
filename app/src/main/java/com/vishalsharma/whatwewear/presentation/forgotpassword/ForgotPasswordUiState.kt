package com.vishalsharma.whatwewear.presentation.forgotpassword

data class ForgotPasswordUiState(
    val email: String ="",
    val isLoading: Boolean= false,
    val error: String? = null,
    val emailSent: Boolean=false
)