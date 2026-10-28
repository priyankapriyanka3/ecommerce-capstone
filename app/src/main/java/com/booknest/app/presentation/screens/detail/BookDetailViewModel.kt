package com.booknest.app.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.booknest.app.domain.model.Book
import com.booknest.app.domain.model.Review
import com.booknest.app.domain.usecase.AddToCartUseCase
import com.booknest.app.domain.usecase.GetBookByIdUseCase
import com.booknest.app.domain.usecase.GetWishlistUseCase
import com.booknest.app.domain.usecase.ToggleWishlistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookDetailUiState(
    val isLoading: Boolean = true,
    val book: Book? = null,
    val isInWishlist: Boolean = false,
    val selectedQuantity: Int = 1,
    val reviews: List<Review> = emptyList(),
    val isAddedToCartSnackbar: Boolean = false,
    val isReviewDialogVisible: Boolean = false,
    val errorMessage: String? = null
)

class BookDetailViewModel(
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val getWishlistUseCase: GetWishlistUseCase,
    private val toggleWishlistUseCase: ToggleWishlistUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    fun loadBook(bookId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val book = getBookByIdUseCase(bookId)
                if (book != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            book = book,
                            reviews = generateSampleReviews(book.id)
                        )
                    }
                    checkWishlist(bookId)
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Book not found") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load book") }
            }
        }
    }

    private fun checkWishlist(bookId: String) {
        viewModelScope.launch {
            getWishlistUseCase().collect { wishlistBooks ->
                val inWishlist = wishlistBooks.any { it.id == bookId }
                _uiState.update { it.copy(isInWishlist = inWishlist) }
            }
        }
    }

    fun setQuantity(quantity: Int) {
        if (quantity in 1..99) {
            _uiState.update { it.copy(selectedQuantity = quantity) }
        }
    }

    fun toggleWishlist() {
        val currentBook = _uiState.value.book ?: return
        viewModelScope.launch {
            toggleWishlistUseCase(currentBook)
        }
    }

    fun addToCart() {
        val currentBook = _uiState.value.book ?: return
        viewModelScope.launch {
            addToCartUseCase(currentBook, _uiState.value.selectedQuantity)
            _uiState.update { it.copy(isAddedToCartSnackbar = true) }
        }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(isAddedToCartSnackbar = false) }
    }

    fun toggleReviewDialog(show: Boolean) {
        _uiState.update { it.copy(isReviewDialogVisible = show) }
    }

    fun submitReview(reviewerName: String, rating: Double, comment: String) {
        val currentBook = _uiState.value.book ?: return
        val newReview = Review(
            id = "rev_${System.currentTimeMillis()}",
            bookId = currentBook.id,
            reviewerName = reviewerName.ifBlank { "Anonymous Reader" },
            rating = rating,
            comment = comment,
            date = "Just now"
        )
        _uiState.update {
            it.copy(
                reviews = listOf(newReview) + it.reviews,
                isReviewDialogVisible = false
            )
        }
    }

    private fun generateSampleReviews(bookId: String): List<Review> {
        return listOf(
            Review(
                id = "rev_1",
                bookId = bookId,
                reviewerName = "Sarah Jenkins",
                rating = 5.0,
                comment = "An extraordinary masterpiece! Couldn't put it down from page one to the end.",
                date = "2 days ago"
            ),
            Review(
                id = "rev_2",
                bookId = bookId,
                reviewerName = "David Kumar",
                rating = 4.5,
                comment = "Very insightful, meticulously structured, and thoroughly enjoyable.",
                date = "1 week ago"
            ),
            Review(
                id = "rev_3",
                bookId = bookId,
                reviewerName = "Emily Watson",
                rating = 4.0,
                comment = "High quality writing and top notch presentation. Highly recommended for book clubs.",
                date = "2 weeks ago"
            )
        )
    }
}
