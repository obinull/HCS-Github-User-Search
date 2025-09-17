package dev.byto.hcsgus.domain.model

data class UserDetail(
    val id: Int,
    val login: String,
    val avatarUrl: String,
    val name: String?,
    val company: String?,
    val location: String?,
    val bio: String?,
    val blog: String?,
    val followers: Int,
    val following: Int,
    val publicRepos: Int
)