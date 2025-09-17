package dev.byto.hcsgus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int? = 0,
    val login: String?,
    val avatarUrl: String?,
    val gistsUrl: String?,
    val reposUrl: String?,
    val followingUrl: String?,
    val starredUrl: String?,
    val followersUrl: String?,
    val type: String?,
    val url: String?,
    val subscriptionsUrl: String?,
    val receivedEventsUrl: String?,
    val eventsUrl: String?,
    val htmlUrl: String?,
    val siteAdmin: Boolean?,
    val gravatarId: String?,
    val nodeId: String?,
    val organizationsUrl: String?
)