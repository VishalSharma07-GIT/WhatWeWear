package com.vishalsharma.whatwewear.presentation.signup

import androidx.lifecycle.ViewModel
import com.vishalsharma.whatwewear.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.vishalsharma.whatwewear.domain.model.AuthResult

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.compareTo

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())

    val uiState: StateFlow<SignupUiState> =
        _uiState.asStateFlow()

    fun onFullNameChanged(name: String) {

        _uiState.value =
            _uiState.value.copy(
                fullName = name
            )

    }

    fun onEmailChanged(email: String) {

        _uiState.value =
            _uiState.value.copy(
                email = email
            )

    }

    fun onPasswordChanged(password: String) {

        _uiState.value =
            _uiState.value.copy(
                password = password
            )

    }

    fun onConfirmPasswordChanged(confirmPassword: String) {

        _uiState.value =
            _uiState.value.copy(
                confirmPassword = confirmPassword
            )

    }
    fun signup() {

        val state = uiState.value

        if (state.fullName.isBlank()) {
            _uiState.value = state.copy(error = "Please enter your full name")
            return
        }

        if (state.email.isBlank()) {
            _uiState.value = state.copy(error = "Please enter your email")
            return
        }

        if (state.password.length < 6) {
            _uiState.value = state.copy(error = "Password must be at least 6 characters")
            return
        }

        if (state.password != state.confirmPassword) {
            _uiState.value = state.copy(error = "Passwords do not match")
            return
        }

        viewModelScope.launch {

            _uiState.value = state.copy(
                isLoading = true,
                error = null
            )

            when (
                val result = authRepository.signup(
                    fullName = state.fullName,
                    email = state.email,
                    password = state.password
                )
            ) {

                is AuthResult.Success -> {

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        signupSuccess = true
                    )

                }

                is AuthResult.Error -> {

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )

                }

            }

        }

    }

}
