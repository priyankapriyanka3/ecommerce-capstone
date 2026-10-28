package com.booknest.app.data.repository

import com.booknest.app.data.local.SampleDataGenerator
import com.booknest.app.data.local.dao.BookDao
import com.booknest.app.data.local.dao.CategoryDao
import com.booknest.app.data.local.dao.ReviewDao
import com.booknest.app.data.local.entity.BookEntity
import com.booknest.app.data.local.entity.CategoryEntity
import com.booknest.app.data.local.entity.ReviewEntity
import com.booknest.app.domain.model.Book
import com.booknest.app.domain.model.BookSortOption
import com.booknest.app.domain.model.Category
import com.booknest.app.domain.model.Review
import com.booknest.app.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookRepositoryImpl(
    private val bookDao: BookDao,
    private val categoryDao: CategoryDao,
    private val reviewDao: ReviewDao
) : BookRepository {

    override fun getBooks(): Flow<List<Book>> {
        return bookDao.getAllBooks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBookById(id: String): Flow<Book?> {
        return bookDao.getBookById(id).map { it?.toDomain() }
    }

    override fun getFeaturedBooks(): Flow<List<Book>> {
        return bookDao.getFeaturedBooks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBestSellers(): Flow<List<Book>> {
        return bookDao.getBestSellers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNewReleases(): Flow<List<Book>> {
        return bookDao.getNewReleases().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBooksByCategory(category: String): Flow<List<Book>> {
        return bookDao.getBooksByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchBooks(
        query: String,
        category: String?,
        minPrice: Double?,
        maxPrice: Double?,
        minRating: Double?,
        sortBy: BookSortOption
    ): Flow<List<Book>> {
        return bookDao.getAllBooks().map { entities ->
            var list = entities.map { it.toDomain() }

            if (query.isNotBlank()) {
                val q = query.trim().lowercase()
                list = list.filter { book ->
                    book.title.lowercase().contains(q) ||
                            book.author.lowercase().contains(q) ||
                            book.category.lowercase().contains(q) ||
                            book.tags.any { it.lowercase().contains(q) } ||
                            book.isbn.contains(q)
                }
            }

            if (!category.isNullOrBlank() && category != "All") {
                list = list.filter { it.category.equals(category, ignoreCase = true) }
            }

            if (minPrice != null) {
                list = list.filter { it.price >= minPrice }
            }

            if (maxPrice != null) {
                list = list.filter { it.price <= maxPrice }
            }

            if (minRating != null) {
                list = list.filter { it.rating >= minRating }
            }

            when (sortBy) {
                BookSortOption.POPULARITY -> list.sortedByDescending { it.reviewCount }
                BookSortOption.RATING -> list.sortedByDescending { it.rating }
                BookSortOption.PRICE_LOW_HIGH -> list.sortedBy { it.price }
                BookSortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.price }
                BookSortOption.NEWEST -> list.sortedByDescending { it.isNewRelease }
                BookSortOption.TITLE_A_Z -> list.sortedBy { it.title }
            }
        }
    }

    override fun getReviewsForBook(bookId: String): Flow<List<Review>> {
        return reviewDao.getReviewsForBook(bookId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addReview(review: Review) {
        reviewDao.insertReview(ReviewEntity.fromDomain(review))
    }

    override suspend fun preloadSampleDataIfEmpty() {
        if (bookDao.getBookCount() == 0) {
            bookDao.insertBooks(SampleDataGenerator.books.map { BookEntity.fromDomain(it) })
        }
        if (categoryDao.getCategoryCount() == 0) {
            categoryDao.insertCategories(SampleDataGenerator.categories.map { CategoryEntity.fromDomain(it) })
        }
        if (reviewDao.getReviewCount() == 0) {
            reviewDao.insertReviews(SampleDataGenerator.sampleReviews.map { ReviewEntity.fromDomain(it) })
        }
    }
}
