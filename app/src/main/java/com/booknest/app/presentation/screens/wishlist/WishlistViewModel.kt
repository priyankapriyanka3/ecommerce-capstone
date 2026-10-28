package com.booknest.app.presentation.screens.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.booknest.app.domain.model.Book
import com.booknest.app.domain.usecase.AddToCartUseCase
import com.booknest.app.domain.usecase.GetWishlistUseCase
import com.booknest.app.domain.usecase.ToggleWishlistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WishlistUiState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false
)

class WishlistViewModel(
    private val getWishlistUseCase: GetWishlistUseCase,
    private val toggleWishlistUseCase: ToggleWishlistUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    val wishlistBooks: StateFlow<List<Book>> = getWishlistUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun removeFromWishlist(book: Book) {
        viewModelScope.launch {
            toggleWishlistUseCase(book)
        }
    }

    fun moveToCart(book: Book) {
        viewModelScope.launch {
            addToCartUseCase(book, 1)
            toggleWishlistUseCase(book)
        }
    }

    fun moveAllToCart() {
        viewModelScope.launch {
            wishlistBooks.value.forEach { book ->
                addToCartUseCase(book, 1)
                toggleWishlistUseCase(book)
            }
        }
    }
}
