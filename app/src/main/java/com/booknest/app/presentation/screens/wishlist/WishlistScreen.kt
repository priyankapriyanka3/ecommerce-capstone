package com.booknest.app.presentation.screens.wishlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.booknest.app.presentation.components.BookListCard
import com.booknest.app.presentation.components.BookNestTopAppBar
import com.booknest.app.presentation.components.EmptyStateView

@Composable
fun WishlistScreen(
    viewModel: WishlistViewModel,
    onBookClick: (String) -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val wishlistBooks by viewModel.wishlistBooks.collectAsState()

    Scaffold(
        topBar = {
            BookNestTopAppBar(
                title = "My Wishlist",
                actions = {
                    if (wishlistBooks.isNotEmpty()) {
                        OutlinedButton(
                            onClick = { viewModel.moveAllToCart() },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text("Move All to Cart", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        if (wishlistBooks.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.FavoriteBorder,
                title = "Your Wishlist is Empty",
                message = "Save stories and titles you love by tapping the heart icon anywhere in BookNest.",
                buttonText = "Explore Books",
                onButtonClick = onExploreClick,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${wishlistBooks.size} Saved Books",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(wishlistBooks, key = { it.id }) { book ->
                        BookListCard(
                            book = book,
                            onClick = { onBookClick(book.id) },
                            onWishlistToggle = { viewModel.removeFromWishlist(book) },
                            isInWishlist = true,
                            onAddToCart = { viewModel.moveToCart(book) }
                        )
                    }
                }
            }
        }
    }
}
