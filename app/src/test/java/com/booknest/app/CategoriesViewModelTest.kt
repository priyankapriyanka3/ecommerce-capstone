package com.booknest.app

import com.booknest.app.domain.usecase.AddToCartUseCase
import com.booknest.app.domain.usecase.GetBooksByCategoryUseCase
import com.booknest.app.domain.usecase.GetBooksUseCase
import com.booknest.app.domain.usecase.GetCategoriesUseCase
import com.booknest.app.domain.usecase.GetWishlistUseCase
import com.booknest.app.domain.usecase.ToggleWishlistUseCase
import com.booknest.app.fakes.FakeBookRepository
import com.booknest.app.fakes.FakeCartRepository
import com.booknest.app.presentation.screens.categories.CategoriesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeBookRepository: FakeBookRepository
    private lateinit var fakeCartRepository: FakeCartRepository
    private lateinit var viewModel: CategoriesViewModel

    @Before
    fun setUp() {
        fakeBookRepository = FakeBookRepository()
        fakeCartRepository = FakeCartRepository()

        viewModel = CategoriesViewModel(
            getCategoriesUseCase = GetCategoriesUseCase(fakeBookRepository),
            getBooksByCategoryUseCase = GetBooksByCategoryUseCase(fakeBookRepository),
            getBooksUseCase = GetBooksUseCase(fakeBookRepository),
            getWishlistUseCase = GetWishlistUseCase(fakeBookRepository),
            toggleWishlistUseCase = ToggleWishlistUseCase(fakeBookRepository),
            addToCartUseCase = AddToCartUseCase(fakeCartRepository)
        )
    }

    @Test
    fun `initial categories are loaded`() = runTest {
        val state = viewModel.uiState.value
        assertEquals(3, state.categories.size)
        assertNotNull(state.selectedCategory)
    }

    @Test
    fun `loadBooksForCategory populates books for selected category`() = runTest {
        viewModel.loadBooksForCategory("Self-Help")
        val state = viewModel.uiState.value
        assertEquals(1, state.books.size)
        assertEquals("Atomic Habits", state.books.first().title)
    }

    @Test
    fun `toggleWishlist updates wishlistIds in state`() = runTest {
        val book = fakeBookRepository.getAllBooks().first().first()
        viewModel.toggleWishlist(book)

        val state = viewModel.uiState.value
        assertTrue(state.wishlistBookIds.contains(book.id))
    }
}
