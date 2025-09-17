package dev.byto.hcsgus.util.constant

import dev.byto.hcsgus.BuildConfig

object Constants {
    const val TOKEN: String = BuildConfig.AUTH_TOKEN
    const val AUTH_TOKEN: String = "Bearer $TOKEN"
    const val GITHUB_VERSION: String = "2022-11-28"
    const val SERVER_URL: String = "https://api.github.com/"

    const val USERNAME_KEY = "username"
    const val ERROR_MESSAGE_KEY = "Username argument is missing"
}