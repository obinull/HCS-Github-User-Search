package dev.byto.hcsgus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int? = 0,
    val login: String? = null,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val followers: Int? = 0,
    val following: Int? = 0,
    val createdAt: String? = null,
    val publicRepos: Int? = 0,
    val blog: String? = null,
    val subscriptionsUrl: String? = null,
    val updatedAt: String? = null,
    val siteAdmin: Boolean? = false,
    val company: String? = null,
    val followingUrl: String? = null,
    val followersUrl: String? = null,
    val reposUrl: String? = null,
    val gravatarId: String? = null,
    val email: String? = null,
    val organizationsUrl: String? = null,
    val name: String? = null,
    val location: String? = null,
    val publicGists: Int?,
    val url: String? = null,
)