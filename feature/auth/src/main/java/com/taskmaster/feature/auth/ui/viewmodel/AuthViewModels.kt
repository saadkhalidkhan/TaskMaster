package com.taskmaster.feature.auth.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmaster.core.common.Result
import com.taskmaster.core.domain.usecase.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError = _loginError.asStateFlow()

    private val _loginSuccess = MutableSharedFlow<Boolean>()
    val loginSuccess = _loginSuccess.asSharedFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _loginError.value = null
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _loginError.value = null
    }

    fun login() {
        _isLoading.value = true
        _loginError.value = null
        viewModelScope.launch {
            when (val result = authUseCases.loginUser(email.value, password.value)) {
                is Result.Success -> {
                    _loginSuccess.emit(true)
                }
                is Result.Error -> {
                    _loginError.value = result.message
                }
                is Result.Loading -> {
                    // Handle loading state if needed
                }
            }
            _isLoading.value = false
        }
    }
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError = _registerError.asStateFlow()

    private val _registerSuccess = MutableSharedFlow<Boolean>()
    val registerSuccess = _registerSuccess.asSharedFlow()

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
        _registerError.value = null
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _registerError.value = null
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _registerError.value = null
    }

    fun register() {
        _isLoading.value = true
        _registerError.value = null
        viewModelScope.launch {
            when (val result = authUseCases.registerUser(username.value, email.value, password.value)) {
                is Result.Success -> {
                    _registerSuccess.emit(true)
                }
                is Result.Error -> {
                    _registerError.value = result.message
                }
                is Result.Loading -> {
                    // Handle loading state if needed
                }
            }
            _isLoading.value = false
        }
    }
}