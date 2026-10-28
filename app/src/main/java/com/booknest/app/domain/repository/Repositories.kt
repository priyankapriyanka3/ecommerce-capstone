package com.booknest.app.domain.repository

import com.booknest.app.domain.model.Address
import com.booknest.app.domain.model.Book
import com.booknest.app.domain.model.CartItem
import com.booknest.app.domain.model.Order
import com.booknest.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun addToCart(book: Book, quantity: Int = 1)
    suspend fun updateQuantity(cartItemId: String, quantity: Int)
    suspend fun removeFromCart(cartItemId: String)
    suspend fun clearCart()
    fun getCartItemCount(): Flow<Int>
}

interface WishlistRepository {
    fun getWishlistItems(): Flow<List<Book>>
    suspend fun toggleWishlist(book: Book)
    fun isInWishlist(bookId: String): Flow<Boolean>
    suspend fun removeFromWishlist(bookId: String)
    suspend fun clearWishlist()
    fun getWishlistCount(): Flow<Int>
}

interface OrderRepository {
    fun getOrders(): Flow<List<Order>>
    fun getOrderById(orderId: String): Flow<Order?>
    suspend fun placeOrder(order: Order): Result<Order>
}

interface UserRepository {
    fun getUserProfile(): Flow<UserProfile>
    suspend fun updateUserProfile(profile: UserProfile)
    suspend fun saveAddress(address: Address)
    suspend fun setDefaultAddress(addressId: String)
    suspend fun setPreferredTheme(theme: String)
}
