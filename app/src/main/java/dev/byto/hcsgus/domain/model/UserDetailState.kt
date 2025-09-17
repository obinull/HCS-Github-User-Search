package dev.byto.hcsgus.domain.model

// Sealed class representing the different states for the user detail screen.
data class UserDetailState(
    val isLoading: Boolean = true,
    val data: UserDetail? = null
)