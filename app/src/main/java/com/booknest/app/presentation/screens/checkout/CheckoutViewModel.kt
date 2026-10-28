package com.booknest.app.presentation.screens.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.booknest.app.domain.model.CartItem
import com.booknest.app.domain.model.Order
import com.booknest.app.domain.model.OrderItem
import com.booknest.app.domain.model.OrderStatus
import com.booknest.app.domain.model.ShippingAddress
import com.booknest.app.domain.usecase.ClearCartUseCase
import com.booknest.app.domain.usecase.GetCartUseCase
import com.booknest.app.domain.usecase.GetUserProfileUseCase
import com.booknest.app.domain.usecase.PlaceOrderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class CheckoutStep {
    SHIPPING_ADDRESS,
    SHIPPING_METHOD,
    PAYMENT_METHOD,
    REVIEW_ORDER,
    ORDER_SUCCESS
}

enum class ShippingMethod(val title: String, val duration: String, val price: Double) {
    STANDARD("Standard Delivery", "3-5 Business Days", 0.0),
    EXPRESS("Express Delivery", "1-2 Business Days", 9.99),
    NEXT_DAY("Overnight Priority", "Next Business Day", 19.99)
}

enum class PaymentType(val title: String) {
    CREDIT_CARD("Credit / Debit Card"),
    UPI("UPI / Instant Transfer"),
    NET_BANKING("Net Banking"),
    CASH_ON_DELIVERY("Cash on Delivery (COD)")
}

data class CheckoutUiState(
    val currentStep: CheckoutStep = CheckoutStep.SHIPPING_ADDRESS,
    val cartItems: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val shippingMethod: ShippingMethod = ShippingMethod.STANDARD,
    val paymentType: PaymentType = PaymentType.CREDIT_CARD,
    // Address fields
    val fullName: String = "",
    val streetAddress: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = "United States",
    val phone: String = "",
    // Card fields
    val cardNumber: String = "",
    val cardHolder: String = "",
    val expiryDate: String = "",
    val cvv: String = "",
    // UPI
    val upiId: String = "",
    // Order Result
    val placedOrder: Order? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val shippingCost: Double
        get() = shippingMethod.price

    val tax: Double
        get() = subtotal * 0.08

    val total: Double
        get() = (subtotal - discount + shippingCost + tax).coerceAtLeast(0.0)
}

class CheckoutViewModel(
    private val getCartUseCase: GetCartUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val user = getUserProfileUseCase().first()
            val cart = getCartUseCase().first()
            val sub = cart.sumOf { it.book.price * it.quantity }

            val defaultAddress = user.shippingAddresses.firstOrNull { it.isDefault }
                ?: user.shippingAddresses.firstOrNull()

            _uiState.update {
                it.copy(
                    cartItems = cart,
                    subtotal = sub,
                    fullName = defaultAddress?.fullName ?: user.name,
                    streetAddress = defaultAddress?.streetAddress ?: "",
                    city = defaultAddress?.city ?: "",
                    state = defaultAddress?.state ?: "",
                    zipCode = defaultAddress?.zipCode ?: "",
                    country = defaultAddress?.country ?: "United States",
                    phone = defaultAddress?.phone ?: user.phoneNumber
                )
            }
        }
    }

    fun updateAddress(
        fullName: String,
        streetAddress: String,
        city: String,
        state: String,
        zipCode: String,
        country: String,
        phone: String
    ) {
        _uiState.update {
            it.copy(
                fullName = fullName,
                streetAddress = streetAddress,
                city = city,
                state = state,
                zipCode = zipCode,
                country = country,
                phone = phone
            )
        }
    }

    fun selectShippingMethod(method: ShippingMethod) {
        _uiState.update { it.copy(shippingMethod = method) }
    }

    fun selectPaymentType(type: PaymentType) {
        _uiState.update { it.copy(paymentType = type) }
    }

    fun updateCardInfo(cardNumber: String, cardHolder: String, expiryDate: String, cvv: String) {
        _uiState.update {
            it.copy(
                cardNumber = cardNumber,
                cardHolder = cardHolder,
                expiryDate = expiryDate,
                cvv = cvv
            )
        }
    }

    fun updateUpiId(upiId: String) {
        _uiState.update { it.copy(upiId = upiId) }
    }

    fun goToNextStep() {
        val next = when (_uiState.value.currentStep) {
            CheckoutStep.SHIPPING_ADDRESS -> CheckoutStep.SHIPPING_METHOD
            CheckoutStep.SHIPPING_METHOD -> CheckoutStep.PAYMENT_METHOD
            CheckoutStep.PAYMENT_METHOD -> CheckoutStep.REVIEW_ORDER
            CheckoutStep.REVIEW_ORDER -> CheckoutStep.ORDER_SUCCESS
            CheckoutStep.ORDER_SUCCESS -> CheckoutStep.ORDER_SUCCESS
        }
        _uiState.update { it.copy(currentStep = next) }
    }

    fun goToPreviousStep() {
        val prev = when (_uiState.value.currentStep) {
            CheckoutStep.SHIPPING_ADDRESS -> CheckoutStep.SHIPPING_ADDRESS
            CheckoutStep.SHIPPING_METHOD -> CheckoutStep.SHIPPING_ADDRESS
            CheckoutStep.PAYMENT_METHOD -> CheckoutStep.SHIPPING_METHOD
            CheckoutStep.REVIEW_ORDER -> CheckoutStep.PAYMENT_METHOD
            CheckoutStep.ORDER_SUCCESS -> CheckoutStep.ORDER_SUCCESS
        }
        _uiState.update { it.copy(currentStep = prev) }
    }

    fun placeOrder(onSuccess: (String) -> Unit) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val state = _uiState.value
                val orderId = "BN-${SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())}-${UUID.randomUUID().toString().take(6).uppercase()}"

                val address = ShippingAddress(
                    id = UUID.randomUUID().toString(),
                    fullName = state.fullName,
                    streetAddress = state.streetAddress,
                    city = state.city,
                    state = state.state,
                    zipCode = state.zipCode,
                    country = state.country,
                    phone = state.phone,
                    isDefault = false
                )

                val orderItems = state.cartItems.map { cartItem ->
                    OrderItem(
                        bookId = cartItem.book.id,
                        title = cartItem.book.title,
                        author = cartItem.book.author,
                        coverUrl = cartItem.book.coverUrl,
                        price = cartItem.book.price,
                        quantity = cartItem.quantity
                    )
                }

                val order = Order(
                    id = orderId,
                    userId = "user_default_01",
                    items = orderItems,
                    totalAmount = state.total,
                    shippingAddress = address,
                    paymentMethod = state.paymentType.title,
                    orderDate = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.US).format(Date()),
                    status = OrderStatus.CONFIRMED,
                    trackingNumber = "TRK-${UUID.randomUUID().toString().take(8).uppercase()}"
                )

                placeOrderUseCase(order)
                clearCartUseCase()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        placedOrder = order,
                        currentStep = CheckoutStep.ORDER_SUCCESS
                    )
                }
                onSuccess(order.id)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to place order. Please try again."
                    )
                }
            }
        }
    }
}
