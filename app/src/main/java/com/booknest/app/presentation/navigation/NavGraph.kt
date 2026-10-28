package com.booknest.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.booknest.app.presentation.components.AppAdaptiveScaffold
import com.booknest.app.presentation.screens.cart.CartScreen
import com.booknest.app.presentation.screens.cart.CartViewModel
import com.booknest.app.presentation.screens.categories.CategoriesScreen
import com.booknest.app.presentation.screens.categories.CategoriesViewModel
import com.booknest.app.presentation.screens.checkout.CheckoutScreen
import com.booknest.app.presentation.screens.checkout.CheckoutViewModel
import com.booknest.app.presentation.screens.detail.BookDetailScreen
import com.booknest.app.presentation.screens.detail.BookDetailViewModel
import com.booknest.app.presentation.screens.home.HomeScreen
import com.booknest.app.presentation.screens.home.HomeViewModel
import com.booknest.app.presentation.screens.orders.OrderHistoryScreen
import com.booknest.app.presentation.screens.orders.OrderHistoryViewModel
import com.booknest.app.presentation.screens.profile.ProfileScreen
import com.booknest.app.presentation.screens.profile.ProfileViewModel
import com.booknest.app.presentation.screens.search.SearchScreen
import com.booknest.app.presentation.screens.search.SearchViewModel
import com.booknest.app.presentation.screens.wishlist.WishlistScreen
import com.booknest.app.presentation.screens.wishlist.WishlistViewModel

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookNestNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel,
    categoriesViewModel: CategoriesViewModel,
    bookDetailViewModel: BookDetailViewModel,
    wishlistViewModel: WishlistViewModel,
    cartViewModel: CartViewModel,
    checkoutViewModel: CheckoutViewModel,
    orderHistoryViewModel: OrderHistoryViewModel,
    profileViewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val cartItemCount by cartViewModel.cartItemCount.collectAsState()
    val wishlistItems by wishlistViewModel.wishlistBooks.collectAsState()

    val navItems = listOf(
        BottomNavItem(Screen.Home.route, "Home", Icons.Default.Home),
        BottomNavItem(Screen.Search.route, "Search", Icons.Default.Search),
        BottomNavItem(Screen.Categories.route, "Categories", Icons.Default.Category),
        BottomNavItem(
            Screen.Wishlist.route,
            "Wishlist",
            Icons.Default.Favorite,
            badgeCount = wishlistItems.size
        ),
        BottomNavItem(
            Screen.Cart.route,
            "Cart",
            Icons.Default.ShoppingCart,
            badgeCount = cartItemCount
        ),
        BottomNavItem(Screen.Profile.route, "Profile", Icons.Default.Person)
    )

    val isTopLevelDestination = navItems.any { it.route == currentRoute }

    AppAdaptiveScaffold(
        modifier = modifier.fillMaxSize(),
        navigationRail = if (isTopLevelDestination) {
            {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    navItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationRailItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                if (item.badgeCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge {
                                                Text(item.badgeCount.toString())
                                            }
                                        }
                                    ) {
                                        Icon(item.icon, contentDescription = item.title)
                                    }
                                } else {
                                    Icon(item.icon, contentDescription = item.title)
                                }
                            },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        } else null,
        bottomBar = if (isTopLevelDestination) {
            {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    navItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                if (item.badgeCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge {
                                                Text(item.badgeCount.toString())
                                            }
                                        }
                                    ) {
                                        Icon(item.icon, contentDescription = item.title)
                                    }
                                } else {
                                    Icon(item.icon, contentDescription = item.title)
                                }
                            },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        } else null
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onBookClick = { bookId ->
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    },
                    onCategoryClick = { categoryId ->
                        categoriesViewModel.selectCategory(categoryId)
                        navController.navigate(Screen.Categories.route)
                    },
                    onCartClick = {
                        navController.navigate(Screen.Cart.route)
                    },
                    onSearchClick = {
                        navController.navigate(Screen.Search.route)
                    }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = searchViewModel,
                    onBookClick = { bookId ->
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    }
                )
            }

            composable(Screen.Categories.route) {
                CategoriesScreen(
                    viewModel = categoriesViewModel,
                    onBookClick = { bookId ->
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    }
                )
            }

            composable(Screen.Wishlist.route) {
                WishlistScreen(
                    viewModel = wishlistViewModel,
                    onBookClick = { bookId ->
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    },
                    onExploreClick = {
                        navController.navigate(Screen.Home.route)
                    }
                )
            }

            composable(Screen.Cart.route) {
                CartScreen(
                    viewModel = cartViewModel,
                    onBookClick = { bookId ->
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    },
                    onCheckoutClick = {
                        navController.navigate(Screen.Checkout.route)
                    },
                    onExploreClick = {
                        navController.navigate(Screen.Home.route)
                    }
                )
            }

            composable(Screen.Checkout.route) {
                CheckoutScreen(
                    viewModel = checkoutViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOrderPlaced = { orderId ->
                        navController.navigate(Screen.OrderHistory.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }

            composable(Screen.OrderHistory.route) {
                OrderHistoryScreen(
                    viewModel = orderHistoryViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onBookClick = { bookId ->
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onOrderHistoryClick = {
                        navController.navigate(Screen.OrderHistory.route)
                    },
                    onWishlistClick = {
                        navController.navigate(Screen.Wishlist.route)
                    }
                )
            }

            composable(
                route = Screen.BookDetail.route,
                arguments = listOf(navArgument("bookId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                BookDetailScreen(
                    bookId = bookId,
                    viewModel = bookDetailViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onCartClick = { navController.navigate(Screen.Cart.route) }
                )
            }
        }
    }
}
