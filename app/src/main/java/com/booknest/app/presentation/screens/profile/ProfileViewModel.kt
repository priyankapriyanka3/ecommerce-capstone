package com.booknest.app.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.booknest.app.domain.model.ShippingAddress
import com.booknest.app.domain.model.UserProfile
import com.booknest.app.domain.usecase.GetUserProfileUseCase
import com.booknest.app.domain.usecase.UpdateUserProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val isEditProfileDialogVisible: Boolean = false,
    val isAddAddressDialogVisible: Boolean = false,
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true
)

class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getUserProfileUseCase().collect { profile ->
                _uiState.update {
                    it.copy(
                        userProfile = profile,
                        isDarkMode = profile.isDarkMode,
                        notificationsEnabled = profile.notificationsEnabled,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateProfile(name: String, email: String, phone: String) {
        val current = _uiState.value.userProfile ?: return
        val updated = current.copy(name = name, email = email, phoneNumber = phone)
        viewModelScope.launch {
            updateUserProfileUseCase(updated)
            _uiState.update { it.copy(userProfile = updated, isEditProfileDialogVisible = false) }
        }
    }

    fun addAddress(address: ShippingAddress) {
        val current = _uiState.value.userProfile ?: return
        val updatedList = current.shippingAddresses + address
        val updated = current.copy(shippingAddresses = updatedList)
        viewModelScope.launch {
            updateUserProfileUseCase(updated)
            _uiState.update { it.copy(userProfile = updated, isAddAddressDialogVisible = false) }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled) }
        val current = _uiState.value.userProfile ?: return
        viewModelScope.launch {
            updateUserProfileUseCase(current.copy(isDarkMode = enabled))
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
        val current = _uiState.value.userProfile ?: return
        viewModelScope.launch {
            updateUserProfileUseCase(current.copy(notificationsEnabled = enabled))
        }
    }

    fun showEditProfileDialog(show: Boolean) {
        _uiState.update { it.copy(isEditProfileDialogVisible = show) }
    }

    fun showAddAddressDialog(show: Boolean) {
        _uiState.update { it.copy(isAddAddressDialogVisible = show) }
    }
}
