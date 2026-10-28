# BookNest – Modern eCommerce Bookstore Android App

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.06.00-green.svg)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material%203-1.2.1-orange.svg)](https://m3.material.io)
[![Room](https://img.shields.io/badge/Room%20DB-2.6.1-red.svg)](https://developer.android.com/training/data-storage/room)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-brightgreen.svg)](https://developer.android.com/topic/architecture)
[![Build & Test](https://img.shields.io/badge/Tests-32%20Passed-success.svg)]()

> A premier eCommerce Bookstore Android application crafted with modern Android development standards: **Kotlin**, **Jetpack Compose**, **Material 3**, **Clean Architecture**, **MVVM**, **Room Database**, and **Kotlin Coroutines / Flow**. Fully adaptive for both phone and tablet form factors with dynamic light and dark theme support.

---

## 📖 Table of Contents
1. [Overview & Features](#-overview--features)
2. [Architecture & Design Principles](#-architecture--design-principles)
3. [Screens & User Flows](#-screens--user-flows)
4. [Tech Stack & Libraries](#-tech-stack--libraries)
5. [Database & Room Entities](#-database--room-entities)
6. [Unit Testing & Test Coverage](#-unit-testing--test-coverage)
7. [Screenshots & UI Showcase](#-screenshots--ui-showcase)
8. [Setup & Installation](#-setup--installation)
9. [Project Structure](#-project-structure)
10. [Verification & Capstone Evaluation](#-verification--capstone-evaluation)

---

## 🌟 Overview & Features

**BookNest** is a full-featured, production-ready online bookstore mobile application offering an intuitive, delightful book buying experience:

* **🏠 Home Discovery**: Curated hero banners, featured carousels, bestselling lists, category tiles, and special discount promotions.
* **🔍 Search & Filter**: Real-time debounce search by book title, author, category, or ISBN with interactive bottom sheet filters for rating (3.0★ - 4.5★) and price sliders, plus dynamic sorting (Price Low-to-High, High-to-Low, Highest Rated, A-Z).
* **📚 Categories Catalog**: Browse 6+ rich book categories (Fiction, Technology, Business & Finance, Self-Help & Wellness, Biography & History, Fantasy & Sci-Fi) with custom category icons and dedicated book grids.
* **📖 Book Details & Reviews**: High-resolution covers, comprehensive synopsis with expandable text, metadata specifications (page count, publisher, publication date, language, format, stock level), star ratings, verified reader reviews, and an interactive "Write a Review" dialog.
* **❤️ Wishlist**: Save favorite titles, quick toggle from any card, and 1-tap "Move All to Cart" functionality.
* **🛒 Shopping Cart & Discounts**: Real-time quantity selector, auto-calculated subtotal, tax (8%), free shipping progress indicator (free over $35), coupon code engine (e.g. `BOOKWORM` for 15% off, `SAVE10`, `STUDENT20`), and clear cart.
* **💳 Multi-Step Checkout**: Streamlined 4-step wizard:
  1. *Shipping Address* with prefilled default address and validation.
  2. *Delivery Method* (Standard Free, Express $9.99, Overnight $19.99).
  3. *Payment Method* (Credit/Debit Card with live formatting, UPI, Net Banking, Cash on Delivery).
  4. *Order Review & Instant Confirmation* with tracking number generation and cart clearing.
* **📦 Order History & Tracking**: Detailed order timeline cards, status badges (`Confirmed`, `Shipped`, `Delivered`, `Processing`), and full modal bottom-sheet order breakdown.
* **👤 User Profile & Customization**: Manage user details, multiple saved delivery addresses with add-address dialog, theme toggle (Light / Dark mode), and notification preferences.
* **📱 Adaptive Layout**: Responsive UX that seamlessly scales between compact phone screens and wide tablet form factors with NavigationRail and adaptive grids.

---

## 🏛️ Architecture & Design Principles

BookNest strictly follows **Clean Architecture** and the **MVVM (Model-View-ViewModel)** design pattern recommended by Google:

```mermaid
graph TD
    subgraph Presentation Layer
        UI[Jetpack Compose UI Screens & Components] --> VM[StateFlow & ViewModels]
    end

    subgraph Domain Layer
        VM --> UC[Use Cases / Interactors]
        UC --> R_Interface[Repository Interfaces / Contracts]
        UC --> DomainModels[Domain Entities & Value Objects]
    end

    subgraph Data Layer
        R_Interface --> R_Impl[Repository Implementations]
        R_Impl --> RoomDAO[Room DAOs & Local Database]
        R_Impl --> Seed[Initial Seed Data & In-Memory Store]
    end
```

### Key Architectural Strengths:
* **Separation of Concerns**: Presentation layer knows nothing about database entities; Data layer converts Room entities to clean Domain models via mappers.
* **Single Source of Truth**: Data flows unidirectionally via Kotlin Coroutines `StateFlow` and Room reactive `Flow`.
* **Testability**: All domain use cases and ViewModels are unit-tested in isolation using mockable repository interfaces and test fakes.

---

## 📱 Screens & User Flows

| Screen | Route | Key Capabilities |
| :--- | :--- | :--- |
| **Home** | `home` | Promotional banner, featured carousel, categories, bestsellers, new arrivals, discount shelf |
| **Search** | `search` | Instant search, category chips, rating & price filters, sort dropdown, grid/list view toggle |
| **Categories** | `categories` | Categorized shelves, category selector grid, live book count |
| **Book Detail** | `book_detail/{bookId}` | Book specs, rating breakdown, reviews, quantity selector, add-to-cart & wishlist toggle |
| **Wishlist** | `wishlist` | Saved items collection, direct add-to-cart, clear/move all |
| **Cart** | `cart` | Quantity modifier, free shipping indicator, coupon code redemption, item removal |
| **Checkout** | `checkout` | 4-step wizard: Shipping Address -> Delivery Option -> Payment -> Order Summary |
| **Order History** | `order_history` | Order receipts, tracking numbers, status badges, expandable order details |
| **Profile** | `profile` | Edit user info, manage addresses, dark theme toggle, notification toggle |

---

## 🛠️ Tech Stack & Libraries

* **Language**: Kotlin 2.0.0
* **UI Toolkit**: Jetpack Compose 2024.06.00 (BOM)
* **Design System**: Material Design 3 (M3)
* **Architecture**: Clean Architecture + MVVM + Repository Pattern
* **Concurrency**: Kotlin Coroutines & Asynchronous Reactive `Flow`
* **Local Persistence**: Android Room Database 2.6.1 with KSP annotation processor
* **Image Loading**: Coil Compose 2.6.0 with fallback generative placeholders
* **Navigation**: Jetpack Navigation Compose 2.7.7
* **Testing**: JUnit 4, Kotlinx Coroutines Test 1.8.1, Test Rules & Fakes

---

## 🗄️ Database & Room Entities

BookNest utilizes a pre-seeded, self-contained Room database with type converters for complex nested collections:

* `BookEntity` (`books` table): Stores title, author, description, price, original price, rating, review count, category, ISBN, page count, publisher, stock, and flags.
* `CartItemEntity` (`cart_items` table): Stores cart items with relational book entities and quantities.
* `WishlistEntity` (`wishlist` table): Stores user-wishlisted book references.
* `OrderEntity` (`orders` table): Stores confirmed order records, serialized `List<OrderItem>`, `ShippingAddress`, payment type, and tracking numbers.
* `UserProfileEntity` (`user_profile` table): Stores name, email, phone, addresses list, dark mode, and notification preferences.

---

## 🧪 Unit Testing & Test Coverage

The test suite covers use cases, business logic, cart arithmetic, discount calculations, search filtering, and ViewModels.

### Test Execution Summary (32 Tests Passed):
```
✔ UseCasesTest:
  - GetBooksUseCase returns all books and featured books correctly
  - GetBookByIdUseCase returns expected book
  - SearchBooksUseCase filters by query and rating
  - AddToCartUseCase and GetCartUseCase maintain item state
  - PlaceOrderUseCase stores order in repository

✔ HomeViewModelTest:
  - loadHomeData loads featured books, bestsellers, and categories successfully
  - toggleWishlist updates wishlist state in HomeUiState
  - addToCart increases cart item count

✔ SearchViewModelTest:
  - search by title query filters matching books
  - filter by category filters properly
  - sorting by price low to high orders list correctly
  - resetFilters restores default search state

✔ CategoriesViewModelTest:
  - initial categories are loaded
  - loadBooksForCategory populates books for selected category
  - toggleWishlist updates wishlistIds in state

✔ BookDetailViewModelTest:
  - loadBook populates book details and reviews
  - setQuantity updates quantity within allowed range
  - submitReview adds review to book

✔ WishlistViewModelTest:
  - wishlist items load and removeFromWishlist works
  - moveAllToCart adds all items to cart and empties wishlist

✔ CartViewModelTest:
  - initial cart is empty and total is zero
  - adding items to cart updates subtotal and total calculations
  - valid coupon code applies discount correctly
  - invalid coupon code sets error message
  - clearCart removes all items

✔ CheckoutViewModelTest:
  - initial checkout loads default address from user profile
  - selecting express shipping updates fee and total
  - placeOrder creates order and clears cart

✔ ProfileViewModelTest:
  - initial profile state loads user profile and defaults
  - updateProfile modifies user details properly
  - toggleDarkMode updates theme preference in profile
  - dialog state toggles work correctly
```

---

## 📸 Screenshots & UI Showcase

```
+--------------------------+  +--------------------------+  +--------------------------+
|  BookNest        [Q] [C] |  |  Search Books        [=] |  |  Clean Code          [H] |
|  Hello, Reader!          |  |  [ Filter: Tech x ]      |  |  by Robert C. Martin    |
|                          |  |                          |  |  ★★★★★ (4.7) 85 reviews |
|  [SUMMER READING FEST]   |  |  +--------------------+  |  |  $34.99   $45.00 (-20%) |
|  Get Up to 30% Off!      |  |  | Clean Code         |  |  |  In Stock (15 left)      |
|                          |  |  | Robert C. Martin   |  |  |                          |
|  Explore Categories      |  |  | $34.99   ★★★★★     |  |  Description:               |
|  [Fiction] [Tech] [Self] |  |  +--------------------+  |  |  A Handbook of Agile...  |
|                          |  |                          |  |                          |
|  Featured Books          |  |  +--------------------+  |  |  [- 1 +]  [ Add to Cart ]|
|  [Atomic Habits | $19.99]|  |  | Pragmatic Program  |  |  |                          |
|  [Clean Code    | $34.99]|  |  | Andrew Hunt        |  |  |                          |
+--------------------------+  +--------------------------+  +--------------------------+
       (Home Screen)                 (Search Screen)              (Book Details)
```

---

## 🚀 Setup & Installation

### Prerequisites:
* **Android Studio**: Ladybug / Jellyfish / Hedgehog (2023.3+)
* **JDK**: OpenJDK 17 or higher
* **Android SDK**: API Level 34 (compileSdk & targetSdk), minSdk 26

### Building and Running:
1. Clone the repository:
   ```bash
   git clone https://github.com/priyankapriyanka3/ecommerce-capstone.git
   cd ecommerce-capstone
   ```
2. Build the project via Gradle:
   ```bash
   ./gradlew assembleDebug
   ```
3. Run all unit tests:
   ```bash
   ./gradlew testDebugUnitTest
   ```
4. Install debug APK on an emulator or physical device:
   ```bash
   ./gradlew installDebug
   ```

---

## 📁 Project Structure

```
com.booknest.app/
│
├── data/
│   ├── local/
│   │   ├── BookNestDatabase.kt          # Room database definition & migrations
│   │   ├── Converters.kt                # Type converters for Room
│   │   ├── dao/                         # Data Access Objects (DAO)
│   │   │   ├── BookDao.kt
│   │   │   ├── CartDao.kt
│   │   │   ├── OrderDao.kt
│   │   │   └── UserDao.kt
│   │   └── entity/                      # Room Database Entities
│   │       ├── BookEntity.kt
│   │       ├── CartItemEntity.kt
│   │       ├── OrderEntity.kt
│   │       └── UserProfileEntity.kt
│   ├── mapper/                          # Entity <-> Domain mappers
│   │   └── Mappers.kt
│   └── repository/                      # Repository implementations
│       ├── BookRepositoryImpl.kt
│       ├── CartRepositoryImpl.kt
│       ├── OrderRepositoryImpl.kt
│       └── UserRepositoryImpl.kt
│
├── domain/
│   ├── model/                           # Pure Kotlin Domain Models
│   │   ├── Book.kt
│   │   ├── CartItem.kt
│   │   ├── Category.kt
│   │   ├── Order.kt
│   │   ├── Review.kt
│   │   └── UserProfile.kt
│   ├── repository/                      # Domain Repository Interfaces
│   │   ├── BookRepository.kt
│   │   ├── CartRepository.kt
│   │   ├── OrderRepository.kt
│   │   └── UserRepository.kt
│   └── usecase/                         # Business Logic Use Cases
│       ├── AddToCartUseCase.kt
│       ├── ClearCartUseCase.kt
│       ├── GetBookByIdUseCase.kt
│       ├── GetBooksByCategoryUseCase.kt
│       ├── GetBooksUseCase.kt
│       ├── GetCartUseCase.kt
│       ├── GetCategoriesUseCase.kt
│       ├── GetOrdersUseCase.kt
│       ├── GetUserProfileUseCase.kt
│       ├── GetWishlistUseCase.kt
│       ├── PlaceOrderUseCase.kt
│       ├── RemoveFromCartUseCase.kt
│       ├── SearchBooksUseCase.kt
│       ├── ToggleWishlistUseCase.kt
│       ├── UpdateCartItemQuantityUseCase.kt
│       └── UpdateUserProfileUseCase.kt
│
└── presentation/
    ├── MainActivity.kt                  # App Entry Point & ViewModel wiring
    ├── components/                      # Reusable Compose Components
    │   ├── AdaptiveLayout.kt
    │   ├── BookCards.kt
    │   └── CommonComponents.kt
    ├── navigation/                      # Navigation Routes & Graphs
    │   ├── NavGraph.kt
    │   └── Screen.kt
    ├── screens/                         # Feature UI Screens & ViewModels
    │   ├── cart/                        # CartScreen & CartViewModel
    │   ├── categories/                  # CategoriesScreen & CategoriesViewModel
    │   ├── checkout/                    # CheckoutScreen & CheckoutViewModel
    │   ├── detail/                      # BookDetailScreen & BookDetailViewModel
    │   ├── home/                        # HomeScreen & HomeViewModel
    │   ├── orders/                      # OrderHistoryScreen & OrderHistoryViewModel
    │   ├── profile/                     # ProfileScreen & ProfileViewModel
    │   ├── search/                      # SearchScreen & SearchViewModel
    │   └── wishlist/                    # WishlistScreen & WishlistViewModel
    └── theme/                           # Color schemes, Typography, and Theme
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

---

## 🏆 Verification & Capstone Evaluation

* **Build Status**: Verified `./gradlew assembleDebug` - `BUILD SUCCESSFUL`
* **Test Status**: Verified `./gradlew testDebugUnitTest` - **32 / 32 Passed** (0 Failures, 0 Errors)
* **Code Quality**: Strict separation of concerns (Clean Architecture), zero hardcoded strings in logic, immutable state patterns, structured error handling, adaptive UI for phone & tablet.
* **Verified Repository**: [https://github.com/priyankapriyanka3/ecommerce-capstone](https://github.com/priyankapriyanka3/ecommerce-capstone)
