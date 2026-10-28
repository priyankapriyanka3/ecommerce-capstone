package com.booknest.app

import com.booknest.app.domain.usecase.AddToCartUseCase
import com.booknest.app.domain.usecase.GetWishlistUseCase
import com.booknest.app.domain.usecase.ToggleWishlistUseCase
import com.booknest.app.fakes.FakeBookRepository
import com.booknest.app.fakes.FakeCartRepository
import com.booknest.app.presentation.screens.wishlist.WishlistViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WishlistViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeBookRepository: FakeBookRepository
    private lateinit var fakeCartRepository: FakeCartRepository
    private lateinit var viewModel: WishlistViewModel

    @Before
    fun setUp() {
        fakeBookRepository = FakeBookRepository()
        fakeCartRepository = FakeCartRepository()

        viewModel = WishlistViewModel(
            getWishlistUseCase = GetWishlistUseCase(fakeBookRepository),
            toggleWishlistUseCase = ToggleWishlistUseCase(fakeBookRepository),
            addToCartUseCase = AddToCartUseCase(fakeCartRepository)
        )
    }

    @Test
    fun `wishlist items load and removeFromWishlist works`() = runTest {
        val book = fakeBookRepository.getAllBooks().first().first()
        fakeBookRepository.addToWishlist(book)

        assertEquals(1, viewModel.wishlistBooks.value.size)

        viewModel.removeFromWishlist(book)
        assertEquals(0, viewModel.wishlistBooks.value.size)
    }

    @Test
    fun `moveAllToCart adds all items to cart and empties wishlist`() = runTest {
        val book = fakeBookRepository.getAllBooks().first().first()
        fakeBookRepository.addToWishlist(book)

        viewModel.moveAllToCart()

        val cartItems = fakeCartRepository.getCartItems().first()
        assertEquals(1, cartItems.size)
        assertEquals(0, viewModel.wishlistBooks.value.size)
    }
}
