package com.booknest.app.domain.usecase

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

class GetCartUseCase(private val cartRepository: CartRepository) {
    operator fun invoke(): Flow<List<CartItem>> = cartRepository.getCartItems()
    fun getItemCount(): Flow<Int> = cartRepository.getCartItemCount()
}

class AddToCartUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(book: Book, quantity: Int = 1) = cartRepository.addToCart(book, quantity)
}

class UpdateCartQuantityUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(cartItemId: String, quantity: Int) = cartRepository.updateQuantity(cartItemId, quantity)
}

class RemoveFromCartUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(cartItemId: String) = cartRepository.removeFromCart(cartItemId)
}

class ClearCartUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke() = cartRepository.clearCart()
}

class GetWishlistUseCase(private val wishlistRepository: WishlistRepository) {
    operator fun invoke(): Flow<List<Book>> = wishlistRepository.getWishlistItems()
    fun getCount(): Flow<Int> = wishlistRepository.getWishlistCount()
}

class ToggleWishlistUseCase(private val wishlistRepository: WishlistRepository) {
    suspend operator fun invoke(book: Book) = wishlistRepository.toggleWishlist(book)
}

class IsInWishlistUseCase(private val wishlistRepository: WishlistRepository) {
    operator fun invoke(bookId: String): Flow<Boolean> = wishlistRepository.isInWishlist(bookId)
}

class GetOrdersUseCase(private val orderRepository: OrderRepository) {
    operator fun invoke(): Flow<List<Order>> = orderRepository.getOrders()
}

class GetOrderByIdUseCase(private val orderRepository: OrderRepository) {
    operator fun invoke(orderId: String): Flow<Order?> = orderRepository.getOrderById(orderId)
}

class PlaceOrderUseCase(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(order: Order): Result<Order> = orderRepository.placeOrder(order)
}

class GetUserProfileUseCase(private val userRepository: UserRepository) {
    operator fun invoke(): Flow<UserProfile> = userRepository.getUserProfile()
}

class UpdateUserProfileUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(profile: UserProfile) = userRepository.updateUserProfile(profile)
}

class SaveAddressUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(address: Address) = userRepository.saveAddress(address)
}
