# HCS GitHub User Search

<div align="center">

# 📱 GitHub User Search

A simple, modern Android application to search for GitHub users and view their details, built with **Clean Architecture**, **MVVM**, and **Jetpack Compose**.

---

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-7F52FF?logo=kotlin)  
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose)  
![Architecture](https://img.shields.io/badge/Architecture-MVVM%20+%20Clean-00A599)  
![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)

</div>

## App Preview

| Search & List (Light) | User Detail (Light) | Loading State | Search & List (Dark) |
| :--------------------: | :------------------: | :-------------: | :--------------------: |
| *[Image placeholder for Search Screen]* | *[Image placeholder for Detail Screen]* | *[Image placeholder for Shimmer Loading]* | *[Image placeholder for Dark Mode]* |

*(Replace the placeholders above with your app's screenshots)*

## ✅ Features

* **User Search**: Dynamically search for GitHub users.
* **Paginated List**: Search results are displayed with infinite scroll using Paging 3, which is efficient for large datasets.
* **User Detail**: View detailed information for a selected user.
* **Offline Caching**: The app caches data in a local Room database for offline access and reduced network usage.
* **Encrypted Database**: Local data security is enhanced with on-disk encryption using SQLCipher.
* **Cache Management**: A background task (WorkManager) periodically cleans up the cache.
* **Light & Dark Theme**: Supports both light and dark themes, following the system settings.
* **Modern UI**: A clean and responsive interface built entirely with Jetpack Compose and Material Design 3.

## 🏛️ Architecture

This project implements the principles of **Clean Architecture** with the **MVVM (Model-View-ViewModel)** pattern to promote separation of concerns, testability, and maintainability.

```
+---------------------------------------------------------------------------------+
|                                                                                 |
|   UI Layer (Jetpack Compose, ViewModel)                                         |
|   - Displays data and handles user interactions.                                |
|   - Observes state changes from ViewModels.                                     |
|                                                                                 |
+---------------------------------+-----------------------------------------------+
                                  |
                                  v
+---------------------------------------------------------------------------------+
|                                                                                 |
|   Domain Layer (Use Cases, Domain Models)                                       |
|   - Contains core business logic.                                               |
|   - Independent of UI and Data layers.                                          |
|                                                                                 |
+---------------------------------+-----------------------------------------------+
                                  |
                                  v
+---------------------------------------------------------------------------------+
|                                                                                 |
|   Data Layer (Repository, Remote/Local Data Sources)                            |
|   - Manages data sources (Network API & Local DB).                              |
|   - Abstracts data operations from the rest of the app.                         |
|                                                                                 |
+---------------------------------------------------------------------------------+
```

## 🛠️ Tech Stack & Key Dependencies

* **Language**:

    * [Kotlin](https://kotlinlang.org/): Primary programming language.

* **UI (Presentation Layer)**:

    * [Jetpack Compose](https://developer.android.com/jetpack/compose): Modern declarative UI toolkit.
    * [Material Design 3](https://m3.material.io/): UI components and theming.
    * [Navigation Compose](https://developer.android.com/jetpack/compose/navigation): For navigating between screens.
    * [Paging 3 Compose](https://developer.android.com/topic/libraries/architecture/paging/v3-overview): Paging integration for efficient lists.
    * [Coil](https://coil-kt.github.io/coil/): For loading images from URLs.
    * [Shimmer Compose](https://github.com/valentinilk/compose-shimmer): A loading placeholder effect.

* **Architecture & Lifecycle**:

    * [MVVM (Model-View-ViewModel)](https://developer.android.com/topic/architecture): Architecture pattern.
    * [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel): Manages UI-related data in a lifecycle-conscious way.
    * [Kotlin Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html): For asynchronous programming.

* **Data Layer**:

    * [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview): For loading and displaying data in chunks.
    * [Retrofit](https://square.github.io/retrofit/): HTTP client for interacting with the GitHub API.
    * [Room](https://developer.android.com/training/data-storage/room): Persistence library for a local SQLite database.
    * [SQLCipher](https://www.zetetic.net/sqlcipher/): Full-database encryption for Room.

* **Dependency Injection**:

    * [Hilt](https://developer.android.com/training/dependency-injection/hilt-android): DI framework for Android.

* **Background Processing**:

    * [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager): For guaranteed background task execution.

## 🚀 Getting Started

### Prerequisites

* Android Studio Iguana | 2023.2.1 or newer.
* Android SDK API level 29 or higher.

### Installation & Setup

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/your-username/HCSGithubUserSearch.git
    cd HCSGithubUserSearch
    ```

    *(Replace `your-username` with your actual username)*

2.  **Set up GitHub API Token:**
    This application requires a GitHub API token to fetch data.

    * Open the `gradle.properties` file in the project's root directory.
    * Add the following line, replacing `YOUR_GITHUB_API_TOKEN` with your Personal Access Token:
      ```properties
      auth_token="YOUR_GITHUB_API_TOKEN"
      ```

    > **Important\!**
    > For security reasons, ensure the `gradle.properties` file containing your token is not committed to a public repository. This file is included in `.gitignore` by default.

3.  **Open in Android Studio:**

    * Launch Android Studio.
    * Select **Open** and navigate to the `HCSGithubUserSearch` directory you just cloned.

4.  **Sync Gradle & Build:**

    * Wait for Android Studio to complete the Gradle Sync process.
    * Build the project by selecting `Build > Make Project` (or clicking the hammer icon).

### How to Run

1.  **Connect a device or start an emulator:**
    Ensure your Android device is connected or an Android Virtual Device (AVD) is running.
2.  **Run the app:**
    * Select the `app` configuration.
    * Click the **Run** button (green play icon) or select `Run > Run 'app'`.

## 📁 Project Structure

```
.
└── app/src/main/java/dev/byto/hcsgus/
    ├── core/              # Base classes and core utilities (BaseViewModel)
    ├── data/              # Data handling logic
    │   ├── local/         # Room database (DAO, Entity, AppDatabase)
    │   ├── remote/        # Retrofit API definitions (ApiService, DTOs)
    │   ├── paging/        # Paging 3 implementation (UserRemoteMediator)
    │   └── mapper/        # Functions to map models between layers
    ├── di/                # Dependency Injection modules (Hilt)
    ├── domain/            # Domain layer (Use Cases, Domain Models)
    ├── ui/                # UI-related components
    │   ├── screen/        # Composable functions for each screen
    │   └── theme/         # Compose theme definitions (Color, Theme, Type)
    └── util/              # Utility classes and constants
        └── CacheCleanupWorker.kt # Worker for WorkManager
```

## 📄 License

```
MIT License

Copyright (c) 2025 Toby Zulkarnain

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```