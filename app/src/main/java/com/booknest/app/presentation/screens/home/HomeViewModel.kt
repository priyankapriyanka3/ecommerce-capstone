package com.booknest.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.booknest.app.domain.model.Book
import com.booknest.app.domain.model.Category
import com.booknest.app.domain.usecase.AddToCartUseCase
import com.booknest.app.domain.usecase.GetBooksUseCase
import com.booknest.app.domain.usecase.GetCategoriesUseCase
import com.booknest.app.domain.usecase.GetWishlistUseCase
import com.booknest.app.domain.usecase.ToggleWishlistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val featuredBooks: List<Book> = emptyList(),
    val bestSellers: List<Book> = emptyList(),
    val newArrivals: List<Book> = emptyList(),
    val discountBooks: List<Book> = emptyList(),
    val categories: List<Category> = emptyList(),
    val wishlistBookIds: Set<String> = emptySet(),
    val userGreeting: String = "Hello, Reader!",
    val errorMessage: String? = null
)

class HomeViewModel(
    private val getBooksUseCase: GetBooksUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getWishlistUseCase: GetWishlistUseCase,
    private val toggleWishlistUseCase: ToggleWishlistUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
        observeWishlist()
    }

    fun loadHomeData() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                combine(
                    getBooksUseCase(),
                    getCategoriesUseCase()
                ) { books, categories ->
                    val featured = books.filter { it.isFeatured }
                    val bestSellers = books.filter { it.isBestSeller }
                    val newArrivals = books.sortedByDescending { it.rating }.take(6)
                    val discount = books.filter { it.discountPercentage > 0 }

                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            featuredBooks = featured.ifEmpty { books.take(4) },
                            bestSellers = bestSellers.ifEmpty { books.take(6) },
                            newArrivals = newArrivals,
                            discountBooks = discount,
                            categories = categories
                        )
                    }
                }.catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load books") }
                }.collect {}
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "An error occurred") }
            }
        }
    }

    private fun observeWishlist() {
        viewModelScope.launch {
            getWishlistUseCase().collect { wishlistBooks ->
                val ids = wishlistBooks.map { it.id }.toSet()
                _uiState.update { it.copy(wishlistBookIds = ids) }
            }
        }
    }

    fun toggleWishlist(book: Book) {
        viewModelScope.launch {
            toggleWishlistUseCase(book)
        }
    }

    fun addToCart(book: Book) {
        viewModelScope.launch {
            addToCartUseCase(book, 1)
        }
    }
}
