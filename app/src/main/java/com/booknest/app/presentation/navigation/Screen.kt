package com.booknest.app.presentation.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Categories : Screen("categories")
    data object Wishlist : Screen("wishlist")
    data object Cart : Screen("cart")
    data object Checkout : Screen("checkout")
    data object OrderHistory : Screen("order_history")
    data object Profile : Screen("profile")
    data object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: String): String = "book_detail/$bookId"
    }
    data object OrderConfirmation : Screen("order_confirmation/{orderId}") {
        fun createRoute(orderId: String): String = "order_confirmation/$orderId"
    }
}
