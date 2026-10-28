package com.booknest.app.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.booknest.app.domain.model.Book
import com.booknest.app.domain.model.Category
import com.booknest.app.domain.usecase.AddToCartUseCase
import com.booknest.app.domain.usecase.GetBooksUseCase
import com.booknest.app.domain.usecase.GetCategoriesUseCase
import com.booknest.app.domain.usecase.GetWishlistUseCase
import com.booknest.app.domain.usecase.SearchBooksUseCase
import com.booknest.app.domain.usecase.ToggleWishlistUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SortOption(val displayName: String) {
    RELEVANCE("Relevance"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    RATING("Highest Rated"),
    TITLE_AZ("Title: A to Z")
}

data class SearchUiState(
    val query: String = "",
    val books: List<Book> = emptyList(),
    val filteredBooks: List<Book> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: String? = null,
    val selectedSort: SortOption = SortOption.RELEVANCE,
    val minRating: Double? = null,
    val maxPrice: Double? = null,
    val wishlistBookIds: Set<String> = emptySet(),
    val isSearching: Boolean = false,
    val isFilterSheetVisible: Boolean = false,
    val isGridView: Boolean = false,
    val errorMessage: String? = null
)

class SearchViewModel(
    private val getBooksUseCase: GetBooksUseCase,
    private val searchBooksUseCase: SearchBooksUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getWishlistUseCase: GetWishlistUseCase,
    private val toggleWishlistUseCase: ToggleWishlistUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadInitialData()
        observeWishlist()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            getCategoriesUseCase().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
        viewModelScope.launch {
            getBooksUseCase().collect { books ->
                _uiState.update { state ->
                    val filtered = applyFilters(books, state.selectedCategory, state.selectedSort, state.minRating, state.maxPrice)
                    state.copy(books = books, filteredBooks = filtered)
                }
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

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            performSearch(newQuery)
        }
    }

    fun performSearch(query: String) {
        _uiState.update { it.copy(isSearching = true) }
        viewModelScope.launch {
            searchBooksUseCase(
                query = query,
                category = _uiState.value.selectedCategory,
                minRating = _uiState.value.minRating,
                maxPrice = _uiState.value.maxPrice
            ).catch { e ->
                _uiState.update { it.copy(isSearching = false, errorMessage = e.message) }
            }.collect { results ->
                val sorted = sortBooks(results, _uiState.value.selectedSort)
                _uiState.update { it.copy(filteredBooks = sorted, isSearching = false) }
            }
        }
    }

    fun selectCategory(category: String?) {
        val newCategory = if (_uiState.value.selectedCategory == category) null else category
        _uiState.update { it.copy(selectedCategory = newCategory) }
        performSearch(_uiState.value.query)
    }

    fun selectSortOption(sortOption: SortOption) {
        _uiState.update { state ->
            val sorted = sortBooks(state.filteredBooks, sortOption)
            state.copy(selectedSort = sortOption, filteredBooks = sorted)
        }
    }

    fun setRatingFilter(rating: Double?) {
        _uiState.update { it.copy(minRating = rating) }
        performSearch(_uiState.value.query)
    }

    fun setPriceFilter(maxPrice: Double?) {
        _uiState.update { it.copy(maxPrice = maxPrice) }
        performSearch(_uiState.value.query)
    }

    fun toggleViewMode() {
        _uiState.update { it.copy(isGridView = !it.isGridView) }
    }

    fun toggleFilterSheet(show: Boolean) {
        _uiState.update { it.copy(isFilterSheetVisible = show) }
    }

    fun resetFilters() {
        _uiState.update {
            it.copy(
                selectedCategory = null,
                selectedSort = SortOption.RELEVANCE,
                minRating = null,
                maxPrice = null
            )
        }
        performSearch(_uiState.value.query)
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

    private fun applyFilters(
        books: List<Book>,
        category: String?,
        sortOption: SortOption,
        minRating: Double?,
        maxPrice: Double?
    ): List<Book> {
        var filtered = books
        if (!category.isNullOrBlank()) {
            filtered = filtered.filter { it.category.equals(category, ignoreCase = true) }
        }
        if (minRating != null) {
            filtered = filtered.filter { it.rating >= minRating }
        }
        if (maxPrice != null) {
            filtered = filtered.filter { it.price <= maxPrice }
        }
        return sortBooks(filtered, sortOption)
    }

    private fun sortBooks(books: List<Book>, sortOption: SortOption): List<Book> {
        return when (sortOption) {
            SortOption.RELEVANCE -> books
            SortOption.PRICE_LOW_HIGH -> books.sortedBy { it.price }
            SortOption.PRICE_HIGH_LOW -> books.sortedByDescending { it.price }
            SortOption.RATING -> books.sortedByDescending { it.rating }
            SortOption.TITLE_AZ -> books.sortedBy { it.title }
        }
    }
}
