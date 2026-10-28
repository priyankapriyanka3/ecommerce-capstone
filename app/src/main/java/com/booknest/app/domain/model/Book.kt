package com.booknest.app.domain.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val category: String,
    val price: Double,
    val originalPrice: Double? = null,
    val rating: Double,
    val reviewCount: Int,
    val description: String,
    val coverUrl: String,
    val pages: Int,
    val language: String = "English",
    val publisher: String,
    val publishDate: String,
    val isbn: String,
    val isBestSeller: Boolean = false,
    val isFeatured: Boolean = false,
    val isNewRelease: Boolean = false,
    val inStock: Boolean = true,
    val stockQuantity: Int = 10,
    val tags: List<String> = emptyList()
) {
    val discountPercent: Int?
        get() = originalPrice?.let {
            if (it > price) (((it - price) / it) * 100).toInt() else null
        }
}
