package com.booknest.app.domain.model

data class Category(
    val id: String,
    val name: String,
    val iconName: String,
    val bookCount: Int,
    val description: String
)

data class CartItem(
    val id: String,
    val book: Book,
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
) {
    val totalPrice: Double
        get() = book.price * quantity
}

data class WishlistItem(
    val bookId: String,
    val book: Book,
    val addedAt: Long = System.currentTimeMillis()
)

data class Review(
    val id: String,
    val bookId: String,
    val userName: String,
    val userAvatar: String = "",
    val rating: Double,
    val comment: String,
    val date: String
)

data class Address(
    val id: String = "",
    val fullName: String,
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String = "United States",
    val phone: String,
    val isDefault: Boolean = false
) {
    val formatted: String
        get() = "$street, $city, $state $zipCode, $country"
}

enum class PaymentType {
    CREDIT_CARD,
    DEBIT_CARD,
    UPI,
    PAYPAL,
    CASH_ON_DELIVERY
}

data class PaymentMethod(
    val type: PaymentType,
    val title: String,
    val subtitle: String,
    val iconName: String
)

enum class OrderStatus {
    PLACED,
    CONFIRMED,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}

data class OrderItem(
    val bookId: String,
    val bookTitle: String,
    val bookAuthor: String,
    val coverUrl: String,
    val price: Double,
    val quantity: Int
) {
    val totalPrice: Double
        get() = price * quantity
}

data class Order(
    val id: String,
    val items: List<OrderItem>,
    val subtotal: Double,
    val discount: Double = 0.0,
    val shippingFee: Double = 0.0,
    val tax: Double = 0.0,
    val totalAmount: Double,
    val shippingAddress: Address,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus = OrderStatus.PLACED,
    val createdAt: Long = System.currentTimeMillis(),
    val estimatedDeliveryDate: String = "3-5 Business Days",
    val trackingNumber: String = ""
)

data class UserProfile(
    val id: String = "usr_101",
    val name: String = "Alex Morgan",
    val email: String = "alex.morgan@booknest.com",
    val phone: String = "+1 (555) 234-5678",
    val avatarUrl: String = "",
    val addresses: List<Address> = emptyList(),
    val booksRead: Int = 24,
    val memberSince: String = "January 2024",
    val preferredTheme: String = "system"
)

enum class BookSortOption(val displayName: String) {
    POPULARITY("Most Popular"),
    RATING("Highest Rated"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    NEWEST("Newest Arrivals"),
    TITLE_A_Z("Title: A to Z")
}
