package com.booknest.app.domain.repository

import com.booknest.app.domain.model.Book
import com.booknest.app.domain.model.BookSortOption
import com.booknest.app.domain.model.Category
import com.booknest.app.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun getBooks(): Flow<List<Book>>
    fun getBookById(id: String): Flow<Book?>
    fun getFeaturedBooks(): Flow<List<Book>>
    fun getBestSellers(): Flow<List<Book>>
    fun getNewReleases(): Flow<List<Book>>
    fun getCategories(): Flow<List<Category>>
    fun getBooksByCategory(category: String): Flow<List<Book>>
    fun searchBooks(
        query: String = "",
        category: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minRating: Double? = null,
        sortBy: BookSortOption = BookSortOption.POPULARITY
    ): Flow<List<Book>>
    fun getReviewsForBook(bookId: String): Flow<List<Review>>
    suspend fun addReview(review: Review)
    suspend fun preloadSampleDataIfEmpty()
}
