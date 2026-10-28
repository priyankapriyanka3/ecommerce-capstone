package com.booknest.app.di

import android.content.Context
import com.booknest.app.data.local.BookNestDatabase
import com.booknest.app.data.repository.BookRepositoryImpl
import com.booknest.app.data.repository.CartRepositoryImpl
import com.booknest.app.data.repository.OrderRepositoryImpl
import com.booknest.app.data.repository.UserRepositoryImpl
import com.booknest.app.data.repository.WishlistRepositoryImpl
import com.booknest.app.domain.repository.BookRepository
import com.booknest.app.domain.repository.CartRepository
import com.booknest.app.domain.repository.OrderRepository
import com.booknest.app.domain.repository.UserRepository
import com.booknest.app.domain.repository.WishlistRepository
import com.booknest.app.domain.usecase.AddReviewUseCase
import com.booknest.app.domain.usecase.AddToCartUseCase
import com.booknest.app.domain.usecase.ClearCartUseCase
import com.booknest.app.domain.usecase.GetBookByIdUseCase
import com.booknest.app.domain.usecase.GetBooksUseCase
import com.booknest.app.domain.usecase.GetCartUseCase
import com.booknest.app.domain.usecase.GetCategoriesUseCase
import com.booknest.app.domain.usecase.GetOrderByIdUseCase
import com.booknest.app.domain.usecase.GetOrdersUseCase
import com.booknest.app.domain.usecase.GetReviewsUseCase
import com.booknest.app.domain.usecase.GetUserProfileUseCase
import com.booknest.app.domain.usecase.GetWishlistUseCase
import com.booknest.app.domain.usecase.IsInWishlistUseCase
import com.booknest.app.domain.usecase.PlaceOrderUseCase
import com.booknest.app.domain.usecase.RemoveFromCartUseCase
import com.booknest.app.domain.usecase.SaveAddressUseCase
import com.booknest.app.domain.usecase.SearchBooksUseCase
import com.booknest.app.domain.usecase.ToggleWishlistUseCase
import com.booknest.app.domain.usecase.UpdateCartQuantityUseCase
import com.booknest.app.domain.usecase.UpdateUserProfileUseCase

interface AppContainer {
    val bookRepository: BookRepository
    val cartRepository: CartRepository
    val wishlistRepository: WishlistRepository
    val orderRepository: OrderRepository
    val userRepository: UserRepository

    val getBooksUseCase: GetBooksUseCase
    val getBookByIdUseCase: GetBookByIdUseCase
    val searchBooksUseCase: SearchBooksUseCase
    val getCategoriesUseCase: GetCategoriesUseCase
    val getReviewsUseCase: GetReviewsUseCase
    val addReviewUseCase: AddReviewUseCase

    val getCartUseCase: GetCartUseCase
    val addToCartUseCase: AddToCartUseCase
    val updateCartQuantityUseCase: UpdateCartQuantityUseCase
    val removeFromCartUseCase: RemoveFromCartUseCase
    val clearCartUseCase: ClearCartUseCase

    val getWishlistUseCase: GetWishlistUseCase
    val toggleWishlistUseCase: ToggleWishlistUseCase
    val isInWishlistUseCase: IsInWishlistUseCase

    val getOrdersUseCase: GetOrdersUseCase
    val getOrderByIdUseCase: GetOrderByIdUseCase
    val placeOrderUseCase: PlaceOrderUseCase

    val getUserProfileUseCase: GetUserProfileUseCase
    val updateUserProfileUseCase: UpdateUserProfileUseCase
    val saveAddressUseCase: SaveAddressUseCase
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database: BookNestDatabase by lazy {
        BookNestDatabase.getDatabase(context)
    }

    override val bookRepository: BookRepository by lazy {
        BookRepositoryImpl(database.bookDao(), database.categoryDao(), database.reviewDao())
    }

    override val cartRepository: CartRepository by lazy {
        CartRepositoryImpl(database.cartDao(), database.bookDao())
    }

    override val wishlistRepository: WishlistRepository by lazy {
        WishlistRepositoryImpl(database.wishlistDao(), database.bookDao())
    }

    override val orderRepository: OrderRepository by lazy {
        OrderRepositoryImpl(database.orderDao())
    }

    override val userRepository: UserRepository by lazy {
        UserRepositoryImpl(database.userDao())
    }

    override val getBooksUseCase: GetBooksUseCase by lazy { GetBooksUseCase(bookRepository) }
    override val getBookByIdUseCase: GetBookByIdUseCase by lazy { GetBookByIdUseCase(bookRepository) }
    override val searchBooksUseCase: SearchBooksUseCase by lazy { SearchBooksUseCase(bookRepository) }
    override val getCategoriesUseCase: GetCategoriesUseCase by lazy { GetCategoriesUseCase(bookRepository) }
    override val getReviewsUseCase: GetReviewsUseCase by lazy { GetReviewsUseCase(bookRepository) }
    override val addReviewUseCase: AddReviewUseCase by lazy { AddReviewUseCase(bookRepository) }

    override val getCartUseCase: GetCartUseCase by lazy { GetCartUseCase(cartRepository) }
    override val addToCartUseCase: AddToCartUseCase by lazy { AddToCartUseCase(cartRepository) }
    override val updateCartQuantityUseCase: UpdateCartQuantityUseCase by lazy { UpdateCartQuantityUseCase(cartRepository) }
    override val removeFromCartUseCase: RemoveFromCartUseCase by lazy { RemoveFromCartUseCase(cartRepository) }
    override val clearCartUseCase: ClearCartUseCase by lazy { ClearCartUseCase(cartRepository) }

    override val getWishlistUseCase: GetWishlistUseCase by lazy { GetWishlistUseCase(wishlistRepository) }
    override val toggleWishlistUseCase: ToggleWishlistUseCase by lazy { ToggleWishlistUseCase(wishlistRepository) }
    override val isInWishlistUseCase: IsInWishlistUseCase by lazy { IsInWishlistUseCase(wishlistRepository) }

    override val getOrdersUseCase: GetOrdersUseCase by lazy { GetOrdersUseCase(orderRepository) }
    override val getOrderByIdUseCase: GetOrderByIdUseCase by lazy { GetOrderByIdUseCase(orderRepository) }
    override val placeOrderUseCase: PlaceOrderUseCase by lazy { PlaceOrderUseCase(orderRepository) }

    override val getUserProfileUseCase: GetUserProfileUseCase by lazy { GetUserProfileUseCase(userRepository) }
    override val updateUserProfileUseCase: UpdateUserProfileUseCase by lazy { UpdateUserProfileUseCase(userRepository) }
    override val saveAddressUseCase: SaveAddressUseCase by lazy { SaveAddressUseCase(userRepository) }
}
