package com.booknest.app

import com.booknest.app.domain.model.Book
import com.booknest.app.domain.usecase.ClearCartUseCase
import com.booknest.app.domain.usecase.GetCartUseCase
import com.booknest.app.domain.usecase.GetUserProfileUseCase
import com.booknest.app.domain.usecase.PlaceOrderUseCase
import com.booknest.app.fakes.FakeCartRepository
import com.booknest.app.fakes.FakeOrderRepository
import com.booknest.app.fakes.FakeUserRepository
import com.booknest.app.presentation.screens.checkout.CheckoutStep
import com.booknest.app.presentation.screens.checkout.CheckoutViewModel
import com.booknest.app.presentation.screens.checkout.ShippingMethod
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CheckoutViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeCartRepository: FakeCartRepository
    private lateinit var fakeOrderRepository: FakeOrderRepository
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var viewModel: CheckoutViewModel

    private val sampleBook = Book(
        id = "b1",
        title = "Atomic Habits",
        author = "James Clear",
        description = "Desc",
        price = 20.0,
        originalPrice = null,
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
        fakeOrderRepository = FakeOrderRepository()
        fakeUserRepository = FakeUserRepository()

        viewModel = CheckoutViewModel(
            getCartUseCase = GetCartUseCase(fakeCartRepository),
            placeOrderUseCase = PlaceOrderUseCase(fakeOrderRepository),
            clearCartUseCase = ClearCartUseCase(fakeCartRepository),
            getUserProfileUseCase = GetUserProfileUseCase(fakeUserRepository)
        )
    }

    @Test
    fun `initial checkout loads default address from user profile`() = runTest {
        val state = viewModel.uiState.value
        assertEquals("Alex Mercer", state.fullName)
        assertEquals("742 Evergreen Terrace", state.streetAddress)
        assertEquals("Springfield", state.city)
    }

    @Test
    fun `selecting express shipping updates fee and total`() = runTest {
        viewModel.selectShippingMethod(ShippingMethod.EXPRESS)
        val state = viewModel.uiState.value
        assertEquals(9.99, state.shippingCost, 0.001)
    }

    @Test
    fun `placeOrder creates order and clears cart`() = runTest {
        fakeCartRepository.addToCart(sampleBook, 1)

        var placedId: String? = null
        viewModel.placeOrder { orderId ->
            placedId = orderId
        }

        assertNotNull(placedId)
        assertEquals(CheckoutStep.ORDER_SUCCESS, viewModel.uiState.value.currentStep)
        val cartAfter = fakeCartRepository.getCartItems().first()
        assertEquals(0, cartAfter.size)
    }
}
