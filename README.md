# HCS Github User Search

A simple Android application to search for GitHub users and view their details. This project demonstrates modern Android development practices.

## Features

*   Search for GitHub users.
*   View a paginated list of users.
*   (Potentially: View user details - based on `UserDetail.kt`)
*   (Potentially: Dark theme support - based on `Color.kt` in theme directory)

## Architecture

This project follows the MVVM (Model-View-ViewModel) architecture pattern and incorporates principles of Clean Architecture.

*   **UI Layer (Jetpack Compose):** The UI is built entirely with Jetpack Compose, a modern declarative UI toolkit.
    *   **Compose Navigation:** Used for navigating between different screens.
    *   **Paging Compose:** Integrated with the Paging 3 library for displaying large, scrollable lists of users.
*   **ViewModel Layer:** ViewModels are responsible for preparing and managing data for the UI. They interact with the Data Layer and expose data via observable streams (e.g., StateFlow, LiveData).
*   **Data Layer:**
    *   **Repository Pattern:** A repository centralizes data operations, abstracting the data sources (network, local database) from the ViewModels.
    *   **Remote Data Source:**
        *   **Retrofit:** Used for making network requests to the GitHub API.
    *   **Local Data Source:**
        *   **Room Persistence Library:** Used for caching user data locally. This enables offline access and a smoother user experience.
        *   **SQLCipher:** Integrated with Room for database encryption, enhancing data security.
    *   **Paging 3 Library:** `RemoteMediator` is implemented to coordinate between the network data source and the local Room database, providing a robust pagination solution.
*   **Dependency Injection (Hilt):** Hilt is used for managing dependencies throughout the application, simplifying boilerplate and improving testability.
*   **Asynchronous Operations (Kotlin Coroutines):** Coroutines are used for managing background threads and simplifying asynchronous programming.
*   **Background Tasks (WorkManager):** Used for deferrable background tasks such as cache cleanup.

## Tech Stack & Dependencies

*   **Kotlin:** Primary programming language.
*   **Jetpack Compose:** For declarative UI development.
    *   `androidx.compose.ui`: Core UI components.
    *   `androidx.compose.material3`: Material Design 3 components.
    *   `androidx.compose.material:material-icons-extended`: Extended set of Material icons.
    *   `androidx.activity:activity-compose`: Integration with Android Activities.
    *   `androidx.navigation:navigation-compose`: For in-app navigation.
    *   `androidx.paging:paging-compose`: For integrating Paging 3 with Compose.
*   **MVVM Architecture:** ViewModel, LiveData/StateFlow.
*   **Paging 3:** For efficient loading of large datasets.
    *   `androidx.paging:paging-runtime`
    *   `androidx.room:room-paging`
*   **Networking:**
    *   **Retrofit:** Type-safe HTTP client for Android and Java.
    *   **(Likely) OkHttp:** Underlying HTTP client for Retrofit.
    *   **(Likely) Moshi/Gson:** For JSON parsing.
*   **Database:**
    *   **Room:** ORM for local SQLite database.
        *   `androidx.room:room-runtime`
        *   `androidx.room:room-ktx`
    *   **SQLCipher:** For encrypted SQLite databases (`net.zetetic:android-database-sqlcipher`).
*   **Dependency Injection:**
    *   **Hilt:** `com.google.dagger:hilt-android`
    *   `androidx.hilt:hilt-navigation-compose`
    *   `androidx.hilt:hilt-work`
*   **Asynchronous Programming:** Kotlin Coroutines.
    *   `androidx.lifecycle:lifecycle-runtime-ktx`
*   **Image Loading:**
    *   **Coil:** `io.coil-kt:coil-compose` (or Glide `com.github.bumptech.glide:compose`)
*   **Background Processing:**
    *   **WorkManager:** `androidx.work:work-runtime-ktx`
*   **UI Enhancements:**
    *   **Shimmer Effect:** `com.valentinilk.shimmer:compose-shimmer` for loading placeholders.
    *   **Splash Screen:** `androidx.core:core-splashscreen`
*   **Core Libraries:**
    *   `androidx.core:core-ktx`: Kotlin extensions for core Android libraries.

## Installation

1.  **Clone the repository:**
    ```bash
    git clone <repository-url> # Replace <repository-url> with the actual URL
    cd HCSGithubUserSearch
    ```
2.  **Set up GitHub API Token:**
    *   You will need a GitHub API token for the application to fetch data from the GitHub API.
    *   Open the `gradle.properties` file located in the root directory of the project.
    *   Add the following line to this file, replacing `YOUR_GITHUB_API_TOKEN` with your actual GitHub Personal Access Token:
        ```properties
        auth_token=YOUR_GITHUB_API_TOKEN
        ```
    *   **Important Note:** If you have a personal `gradle.properties` in your user-level Gradle directory (e.g., `~/.gradle/gradle.properties`), ensure it doesn't override this project-specific token, or add the token there. For security, ensure your `gradle.properties` file containing the token is listed in your project's `.gitignore` file if it's not already, to prevent accidentally committing sensitive credentials.

3.  **Open in Android Studio:**
    *   Open Android Studio.
    *   Click on "Open" or "Import Project".
    *   Navigate to the cloned `HCSGithubUserSearch` directory and select it.
4.  **Sync Gradle:**
    *   Android Studio should automatically start syncing the project with Gradle. If not, click on "Sync Project with Gradle Files" (elephant icon in the toolbar). This step will also make the `auth_token` available to your project build.
5.  **Build the project:**
    *   Once Gradle sync is complete, build the project by clicking on `Build > Make Project` or the hammer icon in the toolbar.

## How to Run

1.  **Connect a device or start an emulator:**
    *   Ensure you have an Android device connected via USB with Developer Options and USB Debugging enabled.
    *   Or, create and start an Android Virtual Device (AVD) from the AVD Manager in Android Studio.
2.  **Run the app:**
    *   Select the `app` configuration from the run configurations dropdown.
    *   Choose your connected device or running emulator.
    *   Click the "Run" button (green play icon) or select `Run > Run 'app'`.

## Project Structure (Key Components)

*   `app/src/main/java/dev/byto/hcsgus/`: Root package.
    *   `core/`: Base classes and core utilities.
        *   `base/BaseViewModel.kt`: Base class for ViewModels.
    *   `data/`: Contains data handling logic.
        *   `local/`: Room database entities, DAOs, and database class (`AppDatabase.kt`).
        *   `remote/`: Network API service definition (`ApiService.kt`).
        *   `paging/UserRemoteMediator.kt`: Implements Paging 3 `RemoteMediator` for user data.
        *   `mapper/`: Data mapping functions (e.g., DTO to Entity).
    *   `di/`: Hilt dependency injection modules.
    *   `domain/`: Domain layer containing business logic and models (e.g., `UserDetail.kt`).
    *   `ui/`: UI-related components.
        *   `screen/`: Composable functions representing different app screens.
        *   `theme/`: Compose theme definitions (`Color.kt`, `Theme.kt`, `Type.kt`).
        *   `GithubSearchApp.kt`: Main application class (likely).
    *   `util/`: Utility classes and extensions.
        *   `constant/`: Application constants.
        *   `CacheCleanupWorker.kt`: WorkManager worker for cache management.

## License

```text
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

---

*This README was auto-generated and can be further customized.*
