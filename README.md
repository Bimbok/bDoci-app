# 📱 bDoci: The Developer's Pocket Knowledge Base

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-blue?style=for-the-badge)
![UI](https://img.shields.io/badge/UI-Gruvbox_Inspired-orange?style=for-the-badge)

**bDoci** is a beautifully crafted, native Android application acting as the mobile client for the Documentation Hub ecosystem. Designed for developers, by a developer, it provides lightning-fast, beautifully rendered access to technical documentation, algorithm explanations, and code snippets.

Built with modern Android development practices, a bespoke "retro-terminal" aesthetic, and an incredibly robust offline-first architecture, bDoci ensures your knowledge is always in your pocket—even when the internet drops.

---

## ✨ Standout Features

### 📡 Offline P2P Sync (QR Deep-Linking)

bDoci breaks the barrier of internet dependency with its flagship **Offline Peer-to-Peer Sharing** feature.

- **How it works:** Find a vital algorithm or code snippet while offline? Tap "Share" to serialize the document into a Base64-encoded JSON payload, which is instantly converted into a custom `bdoci://share` QR code.
- **The Intercept:** A teammate scans the code using their native camera. Android's Intent system catches the deep link, launches bDoci, decodes the payload, and injects the document directly into their local Room Database. True zero-network collaboration.

### 🎨 Custom "Gruvbox-Chic" Aesthetic

Moving away from the harsh glaring whites of standard Material Design, bDoci utilizes a warm, high-end, custom UI.

- **Cozy Readability:** Features a pastel cream background with soft retro oranges, yellows, and greens.
- **Floating UI:** Document cards and search bars feature deep 16dp rounded corners and soft elevation shadows, creating a "floating" layered effect.
- **Code Containers:** Code snippets are housed in deep, warm dark `#282828` containers, ensuring monospace syntax pops beautifully without straining the eyes.

### 🗃️ True Offline-First Architecture

- Powered by an SQLite-abstracted **Room Database**, every document fetched from the live Vercel backend is intelligently cached.
- Custom `NetworkUtils` passively monitor connectivity. If you open the app on a subway or without Wi-Fi, bDoci instantly falls back to your local cache. A dynamic **Offline Status Indicator** seamlessly updates the UI to reflect your connection state.

### 🏷️ Category Sidebar & Instant Search

- **Dynamic Sidebar:** A scrollable, pill-shaped side navigation bar allows users to instantly filter their cached knowledge base by tags (e.g., C++, Java, Linux).
- **Real-time Filtering:** The custom-styled top search bar filters the RecyclerView instantly as you type, allowing for zero-latency lookups of complex algorithms.

---

## 📸 Sample Screens

<p align="center">
  <img src="Sample/Screenshot_20260422_215212_bDoci.jpg" alt="bDoci dashboard screen" width="220" />
  <img src="Sample/Screenshot_20260422_215224_bDoci.jpg" alt="bDoci category filter screen" width="220" />
  <img src="Sample/Screenshot_20260422_215236_bDoci.jpg" alt="bDoci documentation detail screen" width="220" />
  <img src="Sample/Screenshot_20260422_215245_bDoci.jpg" alt="bDoci QR sharing screen" width="220" />
  <img src="Sample/Screenshot_20260422_215254_bDoci.jpg" alt="bDoci code viewer screen" width="220" />
</p>

---

## 🛠️ Tech Stack & Engineering

bDoci strictly adheres to the **MVVM (Model-View-ViewModel)** architectural pattern, ensuring clean separation of concerns, testability, and a crash-free, lifecycle-aware UI.

- **Language:** Kotlin
- **Architecture:** MVVM + Single Source of Truth Repository Pattern
- **Networking:** Retrofit2 & OkHttp3
  - _Includes custom interceptors to spoof browser headers, preventing automated bot-blocking from Vercel/Cloudflare, and specific `HttpException` handlers for 429 Rate Limits._
- **Local Storage (Caching):** Room Database
- **Asynchronous Execution:** Kotlin Coroutines & `lifecycleScope`
- **JSON Serialization:** Gson
- **QR Generation:** ZXing Core
- **UI Components:** Complex XML Layouts, customized State Selectors, Constraints, and RecyclerViews.

---

## 🏗️ Architecture Flow

1. **The UI Layer (`Dashboard.kt` & `DocDetailActivity.kt`):** Passively observes data states and renders the custom Gruvbox layouts.
2. **ViewModel (`DocViewModel.kt`):** Manages all UI states and requests data without blocking the main thread.
3. **Repository (`DocRepository.kt`):** The brain of the app. It consults `NetworkUtils`—if online, it fetches fresh JSON via the Retrofit `ApiService` and updates the Room cache. If offline, it streams data directly from the `DocDao`.
4. **Deep Link Interceptor:** `MainActivity` serves as a routing hub, catching custom schema URIs from external camera apps and parsing incoming Base64 payloads into database entries.

---

## 📂 Project Structure

```text
com.example.bdoci
│
├── app/                  # Main BDociApp Initialization
├── database/             # Offline Caching (AppDatabase, DocDao)
├── models/               # Serialized Data classes (Doc.kt)
├── network/              # Retrofit routing (ApiClient, ApiService)
├── repository/           # Single source of truth (DocRepository)
├── utils/                # Helper singletons (NetworkUtils, QRUtils)
├── viewmodels/           # Lifecycle-aware logic (DocViewModel)
│
├── Dashboard.kt          # Main List, Search, and Category Filtering
├── DocDetailActivity.kt  # Document Reader & QR Generation
├── DocAdapter.kt         # Adapter for floating document cards
└── CategoryAdapter.kt    # Adapter for sidebar navigation pills
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (Latest Version)
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 34 (Android 14)

### Installation

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/Bimbok/bdoci-app.git](https://github.com/Bimbok/bdoci-app.git)
   ```
2. **Open & Sync:** Open the project in Android Studio. Wait for Gradle to sync dependencies (Retrofit, Room, Coroutines, ZXing).
3. **Run:** Select your target emulator or physical device and run the application.

_Note: This mobile app is designed as a highly optimized, read-only client. Documentation authoring, Markdown formatting, and database administration are handled exclusively via the accompanying Express/Node.js web dashboard._

---

## 👨‍💻 Developed By

**Bimbok** _Architected and developed as a comprehensive mobile companion for high-performance developer workflows._
