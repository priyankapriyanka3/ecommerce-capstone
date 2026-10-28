package com.booknest.app

import com.booknest.app.domain.usecase.AddToCartUseCase
import com.booknest.app.domain.usecase.GetBooksUseCase
import com.booknest.app.domain.usecase.GetCategoriesUseCase
import com.booknest.app.domain.usecase.GetWishlistUseCase
import com.booknest.app.domain.usecase.SearchBooksUseCase
import com.booknest.app.domain.usecase.ToggleWishlistUseCase
import com.booknest.app.fakes.FakeBookRepository
import com.booknest.app.fakes.FakeCartRepository
import com.booknest.app.presentation.screens.search.SearchViewModel
import com.booknest.app.presentation.screens.search.SortOption
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeBookRepository: FakeBookRepository
    private lateinit var fakeCartRepository: FakeCartRepository
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        fakeBookRepository = FakeBookRepository()
        fakeCartRepository = FakeCartRepository()

        viewModel = SearchViewModel(
            getBooksUseCase = GetBooksUseCase(fakeBookRepository),
            searchBooksUseCase = SearchBooksUseCase(fakeBookRepository),
            getCategoriesUseCase = GetCategoriesUseCase(fakeBookRepository),
            getWishlistUseCase = GetWishlistUseCase(fakeBookRepository),
            toggleWishlistUseCase = ToggleWishlistUseCase(fakeBookRepository),
            addToCartUseCase = AddToCartUseCase(fakeCartRepository)
        )
    }

    @Test
    fun `search by title query filters matching books`() = runTest {
        viewModel.performSearch("Clean")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredBooks.size)
        assertEquals("Clean Code", state.filteredBooks.first().title)
    }

    @Test
    fun `filter by category filters properly`() = runTest {
        viewModel.selectCategory("Technology")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredBooks.size)
        assertEquals("Technology", state.filteredBooks.first().category)
    }

    @Test
    fun `sorting by price low to high orders list correctly`() = runTest {
        viewModel.selectSortOption(SortOption.PRICE_LOW_HIGH)

        val state = viewModel.uiState.value
        assertTrue(state.filteredBooks[0].price <= state.filteredBooks[1].price)
    }

    @Test
    fun `resetFilters restores default search state`() = runTest {
        viewModel.selectCategory("Technology")
        viewModel.setRatingFilter(4.8)
        viewModel.resetFilters()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(null, state.selectedCategory)
        assertEquals(null, state.minRating)
        assertEquals(3, state.filteredBooks.size)
    }
}
