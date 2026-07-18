package com.vishalsharma.whatwewear.presentation.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishalsharma.whatwewear.domain.model.AuthResult
import com.vishalsharma.whatwewear.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())

    val uiState: StateFlow<ForgotPasswordUiState> =
        _uiState.asStateFlow()

    fun onEmailChanged(email: String) {

        _uiState.value = _uiState.value.copy(
            email = email
        )

    }

    fun sendResetLink() {

        val state = uiState.value

        if (state.email.isBlank()) {

            _uiState.value = state.copy(
                error = "Please enter your email"
            )

            return
        }

        viewModelScope.launch {

            _uiState.value = state.copy(
                isLoading = true,
                error = null
            )

            when (
                val result = authRepository.resetPassword(state.email)
            ) {

                is AuthResult.Success -> {

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        emailSent = true
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