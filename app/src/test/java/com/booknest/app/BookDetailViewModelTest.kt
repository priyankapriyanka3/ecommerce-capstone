package com.booknest.app

import com.booknest.app.domain.usecase.AddToCartUseCase
import com.booknest.app.domain.usecase.GetBookByIdUseCase
import com.booknest.app.domain.usecase.GetWishlistUseCase
import com.booknest.app.domain.usecase.ToggleWishlistUseCase
import com.booknest.app.fakes.FakeBookRepository
import com.booknest.app.fakes.FakeCartRepository
import com.booknest.app.presentation.screens.detail.BookDetailViewModel
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
class BookDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeBookRepository: FakeBookRepository
    private lateinit var fakeCartRepository: FakeCartRepository
    private lateinit var viewModel: BookDetailViewModel

    @Before
    fun setUp() {
        fakeBookRepository = FakeBookRepository()
        fakeCartRepository = FakeCartRepository()

        viewModel = BookDetailViewModel(
            getBookByIdUseCase = GetBookByIdUseCase(fakeBookRepository),
            getWishlistUseCase = GetWishlistUseCase(fakeBookRepository),
            toggleWishlistUseCase = ToggleWishlistUseCase(fakeBookRepository),
            addToCartUseCase = AddToCartUseCase(fakeCartRepository)
        )
    }

    @Test
    fun `loadBook populates book details and reviews`() = runTest {
        viewModel.loadBook("b1")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.book)
        assertEquals("Atomic Habits", state.book?.title)
        assertTrue(state.reviews.isNotEmpty())
    }

    @Test
    fun `setQuantity updates quantity within allowed range`() = runTest {
        viewModel.setQuantity(5)
        assertEquals(5, viewModel.uiState.value.selectedQuantity)

        // Invalid quantity should be ignored
        viewModel.setQuantity(0)
        assertEquals(5, viewModel.uiState.value.selectedQuantity)
    }

    @Test
    fun `submitReview adds review to book`() = runTest {
        viewModel.loadBook("b1")
        val initialCount = viewModel.uiState.value.reviews.size

        viewModel.submitReview("John Doe", 5.0, "Outstanding read!")

        val updated = viewModel.uiState.value
        assertEquals(initialCount + 1, updated.reviews.size)
        assertEquals("John Doe", updated.reviews.first().reviewerName)
    }
}
