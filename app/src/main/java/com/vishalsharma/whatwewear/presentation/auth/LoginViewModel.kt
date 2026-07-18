package com.vishalsharma.whatwewear.presentation.auth

import android.util.Log
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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email
        )
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password
        )
    }

    fun login() {

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = authRepository.login(
                email = uiState.value.email,
                password = uiState.value.password
            )

            when (result) {

                is AuthResult.Success -> {

                    Log.d("LOGIN", "Login Successful")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginSuccess = true
                    )

                }

                is AuthResult.Error -> {

                    Log.e("LOGIN", result.message)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )

                }
            }
        }
    }
    fun signInWithGoogle(
        idToken: String
    ) {

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            when (
                val result = authRepository.signInWithGoogle(idToken)
            ) {

                is AuthResult.Success -> {

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginSuccess = true
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
