/**
 * @author Saad Khan
 * @date June 2025
 */
package com.taskmaster.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmaster.core.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = tokenManager.isLoggedIn()
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _sessionReady = MutableStateFlow(false)
    val sessionReady: StateFlow<Boolean> = _sessionReady.asStateFlow()

    init {
        viewModelScope.launch {
            tokenManager.restoreSession()
            _sessionReady.value = true
        }
    }
}
