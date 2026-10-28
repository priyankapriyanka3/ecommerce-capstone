package com.booknest.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.booknest.app.domain.model.Address
import com.booknest.app.domain.model.Book
import com.booknest.app.domain.model.CartItem
import com.booknest.app.domain.model.Category
import com.booknest.app.domain.model.Order
import com.booknest.app.domain.model.OrderItem
import com.booknest.app.domain.model.OrderStatus
import com.booknest.app.domain.model.PaymentMethod
import com.booknest.app.domain.model.Review
import com.booknest.app.domain.model.UserProfile

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val category: String,
    val price: Double,
    val originalPrice: Double?,
    val rating: Double,
    val reviewCount: Int,
    val description: String,
    val coverUrl: String,
    val pages: Int,
    val language: String,
    val publisher: String,
    val publishDate: String,
    val isbn: String,
    val isBestSeller: Boolean,
    val isFeatured: Boolean,
    val isNewRelease: Boolean,
    val inStock: Boolean,
    val stockQuantity: Int,
    val tags: List<String>
) {
    fun toDomain(): Book = Book(
        id = id,
        title = title,
        author = author,
        category = category,
        price = price,
        originalPrice = originalPrice,
        rating = rating,
        reviewCount = reviewCount,
        description = description,
        coverUrl = coverUrl,
        pages = pages,
        language = language,
        publisher = publisher,
        publishDate = publishDate,
        isbn = isbn,
        isBestSeller = isBestSeller,
        isFeatured = isFeatured,
        isNewRelease = isNewRelease,
        inStock = inStock,
        stockQuantity = stockQuantity,
        tags = tags
    )

    companion object {
        fun fromDomain(book: Book): BookEntity = BookEntity(
            id = book.id,
            title = book.title,
            author = book.author,
            category = book.category,
            price = book.price,
            originalPrice = book.originalPrice,
            rating = book.rating,
            reviewCount = book.reviewCount,
            description = book.description,
            coverUrl = book.coverUrl,
            pages = book.pages,
            language = book.language,
            publisher = book.publisher,
            publishDate = book.publishDate,
            isbn = book.isbn,
            isBestSeller = book.isBestSeller,
            isFeatured = book.isFeatured,
            isNewRelease = book.isNewRelease,
            inStock = book.inStock,
            stockQuantity = book.stockQuantity,
            tags = book.tags
        )
    }
}

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val bookCount: Int,
    val description: String
) {
    fun toDomain(): Category = Category(
        id = id,
        name = name,
        iconName = iconName,
        bookCount = bookCount,
        description = description
    )

    companion object {
        fun fromDomain(category: Category): CategoryEntity = CategoryEntity(
            id = category.id,
            name = category.name,
            iconName = category.iconName,
            bookCount = category.bookCount,
            description = category.description
        )
    }
}

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val bookId: String,
    val quantity: Int,
    val addedAt: Long
)

@Entity(tableName = "wishlist_items")
data class WishlistEntity(
    @PrimaryKey val bookId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val bookId: String,
    val userName: String,
    val userAvatar: String,
    val rating: Double,
    val comment: String,
    val date: String
) {
    fun toDomain(): Review = Review(
        id = id,
        bookId = bookId,
        userName = userName,
        userAvatar = userAvatar,
        rating = rating,
        comment = comment,
        date = date
    )

    companion object {
        fun fromDomain(review: Review): ReviewEntity = ReviewEntity(
            id = review.id,
            bookId = review.bookId,
            userName = review.userName,
            userAvatar = review.userAvatar,
            rating = review.rating,
            comment = review.comment,
            date = review.date
        )
    }
}

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val items: List<OrderItem>,
    val subtotal: Double,
    val discount: Double,
    val shippingFee: Double,
    val tax: Double,
    val totalAmount: Double,
    val shippingAddress: Address,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus,
    val createdAt: Long,
    val estimatedDeliveryDate: String,
    val trackingNumber: String
) {
    fun toDomain(): Order = Order(
        id = id,
        items = items,
        subtotal = subtotal,
        discount = discount,
        shippingFee = shippingFee,
        tax = tax,
        totalAmount = totalAmount,
        shippingAddress = shippingAddress,
        paymentMethod = paymentMethod,
        status = status,
        createdAt = createdAt,
        estimatedDeliveryDate = estimatedDeliveryDate,
        trackingNumber = trackingNumber
    )

    companion object {
        fun fromDomain(order: Order): OrderEntity = OrderEntity(
            id = order.id,
            items = order.items,
            subtotal = order.subtotal,
            discount = order.discount,
            shippingFee = order.shippingFee,
            tax = order.tax,
            totalAmount = order.totalAmount,
            shippingAddress = order.shippingAddress,
            paymentMethod = order.paymentMethod,
            status = order.status,
            createdAt = order.createdAt,
            estimatedDeliveryDate = order.estimatedDeliveryDate,
            trackingNumber = order.trackingNumber
        )
    }
}

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val avatarUrl: String,
    val addresses: List<Address>,
    val booksRead: Int,
    val memberSince: String,
    val preferredTheme: String
) {
    fun toDomain(): UserProfile = UserProfile(
        id = id,
        name = name,
        email = email,
        phone = phone,
        avatarUrl = avatarUrl,
        addresses = addresses,
        booksRead = booksRead,
        memberSince = memberSince,
        preferredTheme = preferredTheme
    )

    companion object {
        fun fromDomain(user: UserProfile): UserProfileEntity = UserProfileEntity(
            id = user.id,
            name = user.name,
            email = user.email,
            phone = user.phone,
            avatarUrl = user.avatarUrl,
            addresses = user.addresses,
            booksRead = user.booksRead,
            memberSince = user.memberSince,
            preferredTheme = user.preferredTheme
        )
    }
}
