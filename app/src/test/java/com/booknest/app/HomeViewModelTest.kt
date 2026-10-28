package com.booknest.app

import com.booknest.app.domain.usecase.AddToCartUseCase
import com.booknest.app.domain.usecase.GetBooksUseCase
import com.booknest.app.domain.usecase.GetCategoriesUseCase
import com.booknest.app.domain.usecase.GetWishlistUseCase
import com.booknest.app.domain.usecase.ToggleWishlistUseCase
import com.booknest.app.fakes.FakeBookRepository
import com.booknest.app.fakes.FakeCartRepository
import com.booknest.app.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeBookRepository: FakeBookRepository
    private lateinit var fakeCartRepository: FakeCartRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        fakeBookRepository = FakeBookRepository()
        fakeCartRepository = FakeCartRepository()

        viewModel = HomeViewModel(
            getBooksUseCase = GetBooksUseCase(fakeBookRepository),
            getCategoriesUseCase = GetCategoriesUseCase(fakeBookRepository),
            getWishlistUseCase = GetWishlistUseCase(fakeBookRepository),
            toggleWishlistUseCase = ToggleWishlistUseCase(fakeBookRepository),
            addToCartUseCase = AddToCartUseCase(fakeCartRepository)
        )
    }

    @Test
    fun `loadHomeData loads featured books, bestsellers, and categories successfully`() = runTest {
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.featuredBooks.size)
        assertEquals(2, state.bestSellers.size)
        assertEquals(3, state.categories.size)
    }

    @Test
    fun `toggleWishlist updates wishlist state in HomeUiState`() = runTest {
        val book = fakeBookRepository.getAllBooks().first().first()
        viewModel.toggleWishlist(book)

        val state = viewModel.uiState.value
        assertTrue(state.wishlistBookIds.contains(book.id))

        viewModel.toggleWishlist(book)
        val updatedState = viewModel.uiState.value
        assertFalse(updatedState.wishlistBookIds.contains(book.id))
    }

    @Test
    fun `addToCart increases cart item count`() = runTest {
        val book = fakeBookRepository.getAllBooks().first().first()
        viewModel.addToCart(book)

        val cartItems = fakeCartRepository.getCartItems().first()
        assertEquals(1, cartItems.size)
        assertEquals(book.id, cartItems.first().book.id)
    }
}
