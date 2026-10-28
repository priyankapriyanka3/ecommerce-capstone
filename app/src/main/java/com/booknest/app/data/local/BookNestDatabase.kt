package com.booknest.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.booknest.app.data.local.converter.RoomConverters
import com.booknest.app.data.local.dao.BookDao
import com.booknest.app.data.local.dao.CartDao
import com.booknest.app.data.local.dao.CategoryDao
import com.booknest.app.data.local.dao.OrderDao
import com.booknest.app.data.local.dao.ReviewDao
import com.booknest.app.data.local.dao.UserDao
import com.booknest.app.data.local.dao.WishlistDao
import com.booknest.app.data.local.entity.BookEntity
import com.booknest.app.data.local.entity.CartItemEntity
import com.booknest.app.data.local.entity.CategoryEntity
import com.booknest.app.data.local.entity.OrderEntity
import com.booknest.app.data.local.entity.ReviewEntity
import com.booknest.app.data.local.entity.UserProfileEntity
import com.booknest.app.data.local.entity.WishlistEntity

@Database(
    entities = [
        BookEntity::class,
        CategoryEntity::class,
        CartItemEntity::class,
        WishlistEntity::class,
        OrderEntity::class,
        ReviewEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class BookNestDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun categoryDao(): CategoryDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun reviewDao(): ReviewDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: BookNestDatabase? = null

        fun getDatabase(context: Context): BookNestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookNestDatabase::class.java,
                    "booknest_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
