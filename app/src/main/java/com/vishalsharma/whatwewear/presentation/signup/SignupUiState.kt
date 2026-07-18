package com.vishalsharma.whatwewear.presentation.signup

data class SignupUiState(

    val fullName: String = "",

    val email: String = "",

    val password: String = "",

    val confirmPassword: String = "",

    val isLoading: Boolean = false,

    val error: String? = null,

    val signupSuccess: Boolean = false

)