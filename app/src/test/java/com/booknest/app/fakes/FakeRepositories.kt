package com.booknest.app.fakes

import com.booknest.app.domain.model.Book
import com.booknest.app.domain.model.CartItem
import com.booknest.app.domain.model.Category
import com.booknest.app.domain.model.Order
import com.booknest.app.domain.model.OrderStatus
import com.booknest.app.domain.model.ShippingAddress
import com.booknest.app.domain.model.UserProfile
import com.booknest.app.domain.repository.BookRepository
import com.booknest.app.domain.repository.CartRepository
import com.booknest.app.domain.repository.OrderRepository
import com.booknest.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeBookRepository : BookRepository {
    private val books = mutableListOf(
        Book(
            id = "b1",
            title = "Atomic Habits",
            author = "James Clear",
            description = "Tiny Changes, Remarkable Results",
            price = 19.99,
            originalPrice = 27.00,
            rating = 4.8,
            reviewCount = 120,
            coverUrl = "",
            category = "Self-Help",
            isbn = "978-0735211292",
            pageCount = 320,
            publisher = "Avery",
            publicationDate = "2018",
            language = "English",
            format = "Hardcover",
            stockQuantity = 25,
            isFeatured = true,
            isBestSeller = true,
            discountPercentage = 25
        ),
        Book(
            id = "b2",
            title = "Clean Code",
            author = "Robert C. Martin",
            description = "A Handbook of Agile Software Craftsmanship",
            price = 34.99,
            originalPrice = 45.00,
            rating = 4.7,
            reviewCount = 85,
            coverUrl = "",
            category = "Technology",
            isbn = "978-0132350884",
            pageCount = 464,
            publisher = "Prentice Hall",
            publicationDate = "2008",
            language = "English",
            format = "Paperback",
            stockQuantity = 15,
            isFeatured = false,
            isBestSeller = true,
            discountPercentage = 20
        ),
        Book(
            id = "b3",
            title = "The Psychology of Money",
            author = "Morgan Housel",
            description = "Timeless lessons on wealth, greed, and happiness",
            price = 16.99,
            originalPrice = 22.00,
            rating = 4.9,
            reviewCount = 200,
            coverUrl = "",
            category = "Business",
            isbn = "978-0857197689",
            pageCount = 256,
            publisher = "Harriman House",
            publicationDate = "2020",
            language = "English",
            format = "Paperback",
            stockQuantity = 40,
            isFeatured = true,
            isBestSeller = false,
            discountPercentage = 0
        )
    )

    private val categories = mutableListOf(
        Category(id = "cat_self_help", name = "Self-Help", description = "Personal growth", bookCount = 1),
        Category(id = "cat_tech", name = "Technology", description = "Coding & AI", bookCount = 1),
        Category(id = "cat_business", name = "Business", description = "Finance & Leadership", bookCount = 1)
    )

    private val wishlist = MutableStateFlow<List<Book>>(emptyList())

    override fun getAllBooks(): Flow<List<Book>> = MutableStateFlow(books)

    override fun getBookById(id: String): Flow<Book?> = MutableStateFlow(books.find { it.id == id })

    override fun getFeaturedBooks(): Flow<List<Book>> = MutableStateFlow(books.filter { it.isFeatured })

    override fun getBestSellers(): Flow<List<Book>> = MutableStateFlow(books.filter { it.isBestSeller })

    override fun getBooksByCategory(category: String): Flow<List<Book>> =
        MutableStateFlow(books.filter { it.category.equals(category, ignoreCase = true) })

    override fun searchBooks(query: String, category: String?, minRating: Double?, maxPrice: Double?): Flow<List<Book>> {
        var res = books.filter {
            it.title.contains(query, ignoreCase = true) || it.author.contains(query, ignoreCase = true)
        }
        if (category != null) {
            res = res.filter { it.category.equals(category, ignoreCase = true) }
        }
        if (minRating != null) {
            res = res.filter { it.rating >= minRating }
        }
        if (maxPrice != null) {
            res = res.filter { it.price <= maxPrice }
        }
        return MutableStateFlow(res)
    }

    override fun getCategories(): Flow<List<Category>> = MutableStateFlow(categories)

    override fun getWishlist(): Flow<List<Book>> = wishlist.asStateFlow()

    override suspend fun addToWishlist(book: Book) {
        if (!wishlist.value.any { it.id == book.id }) {
            wishlist.value = wishlist.value + book
        }
    }

    override suspend fun removeFromWishlist(bookId: String) {
        wishlist.value = wishlist.value.filter { it.id != bookId }
    }

    override suspend fun isInWishlist(bookId: String): Boolean {
        return wishlist.value.any { it.id == bookId }
    }
}

class FakeCartRepository : CartRepository {
    private val cart = MutableStateFlow<List<CartItem>>(emptyList())

    override fun getCartItems(): Flow<List<CartItem>> = cart.asStateFlow()

    override fun getCartItemCount(): Flow<Int> = cart.map { items -> items.sumOf { it.quantity } }

    override suspend fun addToCart(book: Book, quantity: Int) {
        val current = cart.value.toMutableList()
        val index = current.indexOfFirst { it.book.id == book.id }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            current.add(CartItem(id = "cart_${book.id}", book = book, quantity = quantity))
        }
        cart.value = current
    }

    override suspend fun updateQuantity(cartItemId: String, quantity: Int) {
        cart.value = cart.value.map {
            if (it.id == cartItemId) it.copy(quantity = quantity) else it
        }
    }

    override suspend fun removeFromCart(cartItemId: String) {
        cart.value = cart.value.filter { it.id != cartItemId }
    }

    override suspend fun clearCart() {
        cart.value = emptyList()
    }
}

class FakeOrderRepository : OrderRepository {
    private val orders = MutableStateFlow<List<Order>>(emptyList())

    override fun getOrders(userId: String): Flow<List<Order>> = orders.asStateFlow()

    override fun getOrderById(orderId: String): Flow<Order?> = orders.map { list -> list.find { it.id == orderId } }

    override suspend fun placeOrder(order: Order) {
        orders.value = listOf(order) + orders.value
    }
}

class FakeUserRepository : UserRepository {
    private val profile = MutableStateFlow(
        UserProfile(
            id = "user_default_01",
            name = "Alex Mercer",
            email = "alex.mercer@booknest.com",
            phoneNumber = "+1 (555) 234-5678",
            avatarUrl = "",
            shippingAddresses = listOf(
                ShippingAddress(
                    id = "addr_1",
                    fullName = "Alex Mercer",
                    streetAddress = "742 Evergreen Terrace",
                    city = "Springfield",
                    state = "OR",
                    zipCode = "97477",
                    country = "United States",
                    phone = "+1 (555) 234-5678",
                    isDefault = true
                )
            ),
            isDarkMode = false,
            notificationsEnabled = true
        )
    )

    override fun getUserProfile(): Flow<UserProfile> = profile.asStateFlow()

    override suspend fun updateUserProfile(userProfile: UserProfile) {
        profile.value = userProfile
    }
}
