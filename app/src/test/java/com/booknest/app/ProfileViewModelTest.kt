package com.booknest.app

import com.booknest.app.domain.model.ShippingAddress
import com.booknest.app.domain.usecase.GetUserProfileUseCase
import com.booknest.app.domain.usecase.UpdateUserProfileUseCase
import com.booknest.app.fakes.FakeUserRepository
import com.booknest.app.presentation.screens.profile.ProfileViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        viewModel = ProfileViewModel(
            getUserProfileUseCase = GetUserProfileUseCase(fakeUserRepository),
            updateUserProfileUseCase = UpdateUserProfileUseCase(fakeUserRepository)
        )
    }

    @Test
    fun `initial profile state loads user profile and defaults`() = runTest {
        val state = viewModel.uiState.value
        assertNotNull(state.userProfile)
        assertEquals("Alex Mercer", state.userProfile?.name)
        assertFalse(state.isDarkMode)
        assertTrue(state.notificationsEnabled)
    }

    @Test
    fun `updateProfile modifies user details properly`() = runTest {
        viewModel.updateProfile("Jane Doe", "jane@example.com", "+1 800-555-0199")

        val state = viewModel.uiState.value
        assertEquals("Jane Doe", state.userProfile?.name)
        assertEquals("jane@example.com", state.userProfile?.email)
        assertEquals("+1 800-555-0199", state.userProfile?.phoneNumber)
    }

    @Test
    fun `toggleDarkMode updates theme preference in profile`() = runTest {
        viewModel.toggleDarkMode(true)

        val state = viewModel.uiState.value
        assertTrue(state.isDarkMode)
    }

    @Test
    fun `dialog state toggles work correctly`() = runTest {
        viewModel.showEditProfileDialog(true)
        assertTrue(viewModel.uiState.value.isEditProfileDialogVisible)

        viewModel.showEditProfileDialog(false)
        assertFalse(viewModel.uiState.value.isEditProfileDialogVisible)
    }
}
