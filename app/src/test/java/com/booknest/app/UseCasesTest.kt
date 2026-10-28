package com.booknest.app

import com.booknest.app.domain.model.Book
import com.booknest.app.domain.model.Order
import com.booknest.app.domain.model.OrderItem
import com.booknest.app.domain.model.OrderStatus
import com.booknest.app.domain.model.ShippingAddress
import com.booknest.app.domain.usecase.AddToCartUseCase
import com.booknest.app.domain.usecase.GetBookByIdUseCase
import com.booknest.app.domain.usecase.GetBooksUseCase
import com.booknest.app.domain.usecase.GetCartUseCase
import com.booknest.app.domain.usecase.PlaceOrderUseCase
import com.booknest.app.domain.usecase.SearchBooksUseCase
import com.booknest.app.fakes.FakeBookRepository
import com.booknest.app.fakes.FakeCartRepository
import com.booknest.app.fakes.FakeOrderRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UseCasesTest {

    private lateinit var fakeBookRepository: FakeBookRepository
    private lateinit var fakeCartRepository: FakeCartRepository
    private lateinit var fakeOrderRepository: FakeOrderRepository

    @Before
    fun setUp() {
        fakeBookRepository = FakeBookRepository()
        fakeCartRepository = FakeCartRepository()
        fakeOrderRepository = FakeOrderRepository()
    }

    @Test
    fun `GetBooksUseCase returns all books and featured books correctly`() = runTest {
        val useCase = GetBooksUseCase(fakeBookRepository)
        val books = useCase().first()
        assertEquals(3, books.size)

        val featured = useCase.getFeatured().first()
        assertEquals(2, featured.size)
    }

    @Test
    fun `GetBookByIdUseCase returns expected book`() = runTest {
        val useCase = GetBookByIdUseCase(fakeBookRepository)
        val book = useCase("b1")
        assertNotNull(book)
        assertEquals("Atomic Habits", book?.title)
    }

    @Test
    fun `SearchBooksUseCase filters by query and rating`() = runTest {
        val useCase = SearchBooksUseCase(fakeBookRepository)
        val searchResult = useCase("Atomic").first()
        assertEquals(1, searchResult.size)
        assertEquals("Atomic Habits", searchResult.first().title)

        val highRated = useCase("", minRating = 4.8).first()
        assertTrue(highRated.all { it.rating >= 4.8 })
    }

    @Test
    fun `AddToCartUseCase and GetCartUseCase maintain item state`() = runTest {
        val addToCart = AddToCartUseCase(fakeCartRepository)
        val getCart = GetCartUseCase(fakeCartRepository)

        val book = Book(
            id = "b1",
            title = "Atomic Habits",
            author = "James Clear",
            description = "Desc",
            price = 19.99,
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

        addToCart(book, 2)
        val cartItems = getCart().first()
        assertEquals(1, cartItems.size)
        assertEquals(2, cartItems.first().quantity)
    }

    @Test
    fun `PlaceOrderUseCase stores order in repository`() = runTest {
        val placeOrder = PlaceOrderUseCase(fakeOrderRepository)
        val address = ShippingAddress("1", "Alex", "Street", "City", "ST", "12345", "USA", "555-1234", true)
        val order = Order(
            id = "ORD_001",
            userId = "user_default_01",
            items = listOf(OrderItem("b1", "Atomic Habits", "James Clear", "", 19.99, 1)),
            totalAmount = 19.99,
            shippingAddress = address,
            paymentMethod = "Card",
            orderDate = "Today",
            status = OrderStatus.CONFIRMED,
            trackingNumber = "TRK123"
        )

        placeOrder(order)
        val storedOrder = fakeOrderRepository.getOrderById("ORD_001").first()
        assertNotNull(storedOrder)
        assertEquals("ORD_001", storedOrder?.id)
    }
}
