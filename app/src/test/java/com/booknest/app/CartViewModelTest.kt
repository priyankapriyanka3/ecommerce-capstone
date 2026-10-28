package com.booknest.app

import com.booknest.app.domain.model.Book
import com.booknest.app.domain.usecase.ClearCartUseCase
import com.booknest.app.domain.usecase.GetCartUseCase
import com.booknest.app.domain.usecase.RemoveFromCartUseCase
import com.booknest.app.domain.usecase.UpdateCartItemQuantityUseCase
import com.booknest.app.fakes.FakeCartRepository
import com.booknest.app.presentation.screens.cart.CartViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeCartRepository: FakeCartRepository
    private lateinit var viewModel: CartViewModel

    private val sampleBook = Book(
        id = "b1",
        title = "Atomic Habits",
        author = "James Clear",
        description = "Desc",
        price = 20.0,
        originalPrice = 25.0,
        rating = 4.8,
        reviewCount = 10,
        coverUrl = "",
        category = "Self-Help",
        isbn = "123",
        pageCount = 300,
        publisher = "P",
        publicationDate = "2020",
        language = "English",
        format = "Hardcover",
        stockQuantity = 10,
        isFeatured = false,
        isBestSeller = false,
        discountPercentage = 0
    )

    @Before
    fun setUp() {
        fakeCartRepository = FakeCartRepository()
        viewModel = CartViewModel(
            getCartUseCase = GetCartUseCase(fakeCartRepository),
            updateCartItemQuantityUseCase = UpdateCartItemQuantityUseCase(fakeCartRepository),
            removeFromCartUseCase = RemoveFromCartUseCase(fakeCartRepository),
            clearCartUseCase = ClearCartUseCase(fakeCartRepository)
        )
    }

    @Test
    fun `initial cart is empty and total is zero`() = runTest {
        assertEquals(0, viewModel.cartItems.value.size)
        assertEquals(0, viewModel.cartItemCount.value)
        assertEquals(0.0, viewModel.subtotal.value, 0.001)
    }

    @Test
    fun `adding items to cart updates subtotal and total calculations`() = runTest {
        fakeCartRepository.addToCart(sampleBook, 2)

        assertEquals(1, viewModel.cartItems.value.size)
        assertEquals(2, viewModel.cartItemCount.value)
        assertEquals(40.0, viewModel.subtotal.value, 0.001)
    }

    @Test
    fun `valid coupon code applies discount correctly`() = runTest {
        fakeCartRepository.addToCart(sampleBook, 2) // subtotal 40.0

        viewModel.applyCoupon("BOOKWORM") // 15% discount = 6.0

        assertEquals("BOOKWORM", viewModel.uiState.value.appliedCoupon)
        assertEquals(6.0, viewModel.uiState.value.discountAmount, 0.001)
    }

    @Test
    fun `invalid coupon code sets error message`() = runTest {
        viewModel.applyCoupon("INVALID_CODE")

        assertNotNull(viewModel.uiState.value.couponError)
        assertEquals(0.0, viewModel.uiState.value.discountAmount, 0.001)
    }

    @Test
    fun `clearCart removes all items`() = runTest {
        fakeCartRepository.addToCart(sampleBook, 1)
        viewModel.clearCart()

        assertEquals(0, viewModel.cartItems.value.size)
    }
}
