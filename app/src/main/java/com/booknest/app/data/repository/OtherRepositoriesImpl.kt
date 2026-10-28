package com.booknest.app.data.repository

import com.booknest.app.data.local.SampleDataGenerator
import com.booknest.app.data.local.dao.BookDao
import com.booknest.app.data.local.dao.CartDao
import com.booknest.app.data.local.dao.OrderDao
import com.booknest.app.data.local.dao.UserDao
import com.booknest.app.data.local.dao.WishlistDao
import com.booknest.app.data.local.entity.CartItemEntity
import com.booknest.app.data.local.entity.OrderEntity
import com.booknest.app.data.local.entity.UserProfileEntity
import com.booknest.app.data.local.entity.WishlistEntity
import com.booknest.app.domain.model.Address
import com.booknest.app.domain.model.Book
import com.booknest.app.domain.model.CartItem
import com.booknest.app.domain.model.Order
import com.booknest.app.domain.model.UserProfile
import com.booknest.app.domain.repository.CartRepository
import com.booknest.app.domain.repository.OrderRepository
import com.booknest.app.domain.repository.UserRepository
import com.booknest.app.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.UUID

class CartRepositoryImpl(
    private val cartDao: CartDao,
    private val bookDao: BookDao
) : CartRepository {

    override fun getCartItems(): Flow<List<CartItem>> {
        return combine(cartDao.getCartItems(), bookDao.getAllBooks()) { cartEntities, bookEntities ->
            val bookMap = bookEntities.associateBy { it.id }
            cartEntities.mapNotNull { cartEntity ->
                val book = bookMap[cartEntity.bookId]?.toDomain()
                if (book != null) {
                    CartItem(
                        id = cartEntity.id,
                        book = book,
                        quantity = cartEntity.quantity,
                        addedAt = cartEntity.addedAt
                    )
                } else null
            }
        }
    }

    override suspend fun addToCart(book: Book, quantity: Int) {
        val existing = cartDao.getCartItemByBookId(book.id)
        if (existing != null) {
            cartDao.updateQuantity(existing.id, existing.quantity + quantity)
        } else {
            val newItem = CartItemEntity(
                id = UUID.randomUUID().toString(),
                bookId = book.id,
                quantity = quantity,
                addedAt = System.currentTimeMillis()
            )
            cartDao.insertCartItem(newItem)
        }
    }

    override suspend fun updateQuantity(cartItemId: String, quantity: Int) {
        if (quantity <= 0) {
            cartDao.deleteCartItem(cartItemId)
        } else {
            cartDao.updateQuantity(cartItemId, quantity)
        }
    }

    override suspend fun removeFromCart(cartItemId: String) {
        cartDao.deleteCartItem(cartItemId)
    }

    override suspend fun clearCart() {
        cartDao.clearCart()
    }

    override fun getCartItemCount(): Flow<Int> {
        return cartDao.getCartItems().map { list -> list.sumOf { it.quantity } }
    }
}

class WishlistRepositoryImpl(
    private val wishlistDao: WishlistDao,
    private val bookDao: BookDao
) : WishlistRepository {

    override fun getWishlistItems(): Flow<List<Book>> {
        return combine(wishlistDao.getAllWishlist(), bookDao.getAllBooks()) { wishlistEntities, bookEntities ->
            val wishlistIds = wishlistEntities.map { it.bookId }.toSet()
            bookEntities.filter { it.id in wishlistIds }.map { it.toDomain() }
        }
    }

    override suspend fun toggleWishlist(book: Book) {
        val currentItems = wishlistDao.getAllWishlist().firstOrNull() ?: emptyList()
        val exists = currentItems.any { it.bookId == book.id }
        if (exists) {
            wishlistDao.deleteWishlist(book.id)
        } else {
            wishlistDao.insertWishlist(WishlistEntity(bookId = book.id))
        }
    }

    override fun isInWishlist(bookId: String): Flow<Boolean> {
        return wishlistDao.isBookInWishlist(bookId).map { count -> count > 0 }
    }

    override suspend fun removeFromWishlist(bookId: String) {
        wishlistDao.deleteWishlist(bookId)
    }

    override suspend fun clearWishlist() {
        wishlistDao.clearWishlist()
    }

    override fun getWishlistCount(): Flow<Int> {
        return wishlistDao.getAllWishlist().map { it.size }
    }
}

class OrderRepositoryImpl(
    private val orderDao: OrderDao
) : OrderRepository {

    override fun getOrders(): Flow<List<Order>> {
        return orderDao.getAllOrders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getOrderById(orderId: String): Flow<Order?> {
        return orderDao.getOrderById(orderId).map { it?.toDomain() }
    }

    override suspend fun placeOrder(order: Order): Result<Order> {
        return try {
            orderDao.insertOrder(OrderEntity.fromDomain(order))
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override fun getUserProfile(): Flow<UserProfile> {
        return userDao.getUserProfile().map { entity ->
            entity?.toDomain() ?: SampleDataGenerator.defaultUser
        }
    }

    override suspend fun updateUserProfile(profile: UserProfile) {
        userDao.insertOrUpdateUser(UserProfileEntity.fromDomain(profile))
    }

    override suspend fun saveAddress(address: Address) {
        val current = userDao.getUserProfile().firstOrNull()?.toDomain() ?: SampleDataGenerator.defaultUser
        val updatedAddresses = current.addresses.toMutableList()
        val index = updatedAddresses.indexOfFirst { it.id == address.id }
        if (index >= 0) {
            updatedAddresses[index] = address
        } else {
            val newAddr = if (address.id.isBlank()) address.copy(id = UUID.randomUUID().toString()) else address
            updatedAddresses.add(newAddr)
        }
        val updated = current.copy(addresses = updatedAddresses)
        userDao.insertOrUpdateUser(UserProfileEntity.fromDomain(updated))
    }

    override suspend fun setDefaultAddress(addressId: String) {
        val current = userDao.getUserProfile().firstOrNull()?.toDomain() ?: SampleDataGenerator.defaultUser
        val updatedAddresses = current.addresses.map {
            it.copy(isDefault = it.id == addressId)
        }
        userDao.insertOrUpdateUser(UserProfileEntity.fromDomain(current.copy(addresses = updatedAddresses)))
    }

    override suspend fun setPreferredTheme(theme: String) {
        val current = userDao.getUserProfile().firstOrNull()?.toDomain() ?: SampleDataGenerator.defaultUser
        userDao.insertOrUpdateUser(UserProfileEntity.fromDomain(current.copy(preferredTheme = theme)))
    }
}
