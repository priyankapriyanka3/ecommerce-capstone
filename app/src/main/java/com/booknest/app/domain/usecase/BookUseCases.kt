package com.booknest.app.domain.usecase

import com.booknest.app.domain.model.Book
import com.booknest.app.domain.model.BookSortOption
import com.booknest.app.domain.model.Category
import com.booknest.app.domain.model.Review
import com.booknest.app.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow

class GetBooksUseCase(private val bookRepository: BookRepository) {
    operator fun invoke(): Flow<List<Book>> = bookRepository.getBooks()
    fun getFeatured(): Flow<List<Book>> = bookRepository.getFeaturedBooks()
    fun getBestSellers(): Flow<List<Book>> = bookRepository.getBestSellers()
    fun getNewReleases(): Flow<List<Book>> = bookRepository.getNewReleases()
    fun getByCategory(category: String): Flow<List<Book>> = bookRepository.getBooksByCategory(category)
}

class GetBookByIdUseCase(private val bookRepository: BookRepository) {
    operator fun invoke(id: String): Flow<Book?> = bookRepository.getBookById(id)
}

class SearchBooksUseCase(private val bookRepository: BookRepository) {
    operator fun invoke(
        query: String = "",
        category: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minRating: Double? = null,
        sortBy: BookSortOption = BookSortOption.POPULARITY
    ): Flow<List<Book>> = bookRepository.searchBooks(
        query = query,
        category = category,
        minPrice = minPrice,
        maxPrice = maxPrice,
        minRating = minRating,
        sortBy = sortBy
    )
}

class GetCategoriesUseCase(private val bookRepository: BookRepository) {
    operator fun invoke(): Flow<List<Category>> = bookRepository.getCategories()
}

class GetReviewsUseCase(private val bookRepository: BookRepository) {
    operator fun invoke(bookId: String): Flow<List<Review>> = bookRepository.getReviewsForBook(bookId)
}

class AddReviewUseCase(private val bookRepository: BookRepository) {
    suspend operator fun invoke(review: Review) = bookRepository.addReview(review)
}
