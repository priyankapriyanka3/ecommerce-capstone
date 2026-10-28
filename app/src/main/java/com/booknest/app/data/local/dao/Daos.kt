package com.booknest.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.booknest.app.data.local.entity.BookEntity
import com.booknest.app.data.local.entity.CartItemEntity
import com.booknest.app.data.local.entity.CategoryEntity
import com.booknest.app.data.local.entity.OrderEntity
import com.booknest.app.data.local.entity.ReviewEntity
import com.booknest.app.data.local.entity.UserProfileEntity
import com.booknest.app.data.local.entity.WishlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookById(id: String): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE isFeatured = 1")
    fun getFeaturedBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isBestSeller = 1")
    fun getBestSellers(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isNewRelease = 1")
    fun getNewReleases(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE category = :category")
    fun getBooksByCategory(category: String): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBookCount(): Int
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE bookId = :bookId LIMIT 1")
    suspend fun getCartItemByBookId(bookId: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: String, quantity: Int)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItem(id: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist_items ORDER BY addedAt DESC")
    fun getAllWishlist(): Flow<List<WishlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlist(item: WishlistEntity)

    @Query("DELETE FROM wishlist_items WHERE bookId = :bookId")
    suspend fun deleteWishlist(bookId: String)

    @Query("SELECT COUNT(*) FROM wishlist_items WHERE bookId = :bookId")
    fun isBookInWishlist(bookId: String): Flow<Int>

    @Query("DELETE FROM wishlist_items")
    suspend fun clearWishlist()
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE bookId = :bookId ORDER BY id DESC")
    fun getReviewsForBook(bookId: String): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Query("SELECT COUNT(*) FROM reviews")
    suspend fun getReviewCount(): Int
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id")
    fun getOrderById(id: String): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserProfileEntity)
}
