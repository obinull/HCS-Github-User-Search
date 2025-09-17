package dev.byto.hcsgus.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_detail")
data class UserDetailEntity(
    @PrimaryKey
    val id: Int? = 0,
    val login: String?,
    val avatarUrl: String?,
    @ColumnInfo(name = "gists_url")
    val gistsUrl: String?,
    val reposUrl: String?,
    val followingUrl: String?,
    val twitterUsername: String?,
    val bio: String?,
    val createdAt: String?,
    val type: String?,
    val blog: String?,
    val subscriptionsUrl: String?,
    val updatedAt: String?,
    val siteAdmin: Boolean?,
    val company: String?,
    val publicRepos: Int?,
    val gravatarId: String?,
    val email: String?,
    val organizationsUrl: String?,
    val hireable: Boolean?,
    val starredUrl: String?,
    val followersUrl: String?,
    val publicGists: Int?,
    val url: String?,
    val receivedEventsUrl: String?,
    val followers: Int?,
    val eventsUrl: String?,
    val htmlUrl: String?,
    val following: Int?,
    val name: String?,
    val location: String?,
    val nodeId: String?
)