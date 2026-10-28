package com.booknest.app.presentation.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.booknest.app.domain.model.CartItem
import com.booknest.app.domain.usecase.ClearCartUseCase
import com.booknest.app.domain.usecase.GetCartUseCase
import com.booknest.app.domain.usecase.RemoveFromCartUseCase
import com.booknest.app.domain.usecase.UpdateCartItemQuantityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartUiState(
    val appliedCoupon: String? = null,
    val discountAmount: Double = 0.0,
    val couponError: String? = null
)

class CartViewModel(
    private val getCartUseCase: GetCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    val cartItems: StateFlow<List<CartItem>> = getCartUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItemCount: StateFlow<Int> = cartItems.map { items ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val subtotal: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.book.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity <= 0) {
                removeFromCartUseCase(cartItem.id)
            } else {
                updateCartItemQuantityUseCase(cartItem.id, newQuantity)
            }
        }
    }

    fun removeItem(cartItem: CartItem) {
        viewModelScope.launch {
            removeFromCartUseCase(cartItem.id)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            clearCartUseCase()
            _uiState.update { it.copy(appliedCoupon = null, discountAmount = 0.0, couponError = null) }
        }
    }

    fun applyCoupon(code: String) {
        val trimmed = code.trim().uppercase()
        when (trimmed) {
            "BOOKWORM" -> {
                val discount = subtotal.value * 0.15
                _uiState.update { it.copy(appliedCoupon = trimmed, discountAmount = discount, couponError = null) }
            }
            "SAVE10" -> {
                val discount = 10.0.coerceAtMost(subtotal.value)
                _uiState.update { it.copy(appliedCoupon = trimmed, discountAmount = discount, couponError = null) }
            }
            "STUDENT20" -> {
                val discount = subtotal.value * 0.20
                _uiState.update { it.copy(appliedCoupon = trimmed, discountAmount = discount, couponError = null) }
            }
            else -> {
                _uiState.update { it.copy(couponError = "Invalid promo code. Try BOOKWORM or SAVE10") }
            }
        }
    }

    fun removeCoupon() {
        _uiState.update { it.copy(appliedCoupon = null, discountAmount = 0.0, couponError = null) }
    }
}
