package dev.byto.hcsgus.data.mapper

import dev.byto.hcsgus.data.local.entity.UserDetailEntity
import dev.byto.hcsgus.data.local.entity.UserEntity
import dev.byto.hcsgus.data.remote.dto.UserDetailDto
import dev.byto.hcsgus.data.remote.dto.UserDto
import dev.byto.hcsgus.domain.model.User
import dev.byto.hcsgus.domain.model.UserDetail
import dev.byto.hcsgus.util.orZero

fun UserDto.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        login = this.login,
        avatarUrl = this.avatarUrl,
        gistsUrl = this.gistsUrl,
        reposUrl = this.reposUrl,
        followingUrl = this.followingUrl,
        starredUrl = this.starredUrl,
        followersUrl = this.followersUrl,
        type = this.type,
        url = this.url,
        subscriptionsUrl = this.subscriptionsUrl,
        receivedEventsUrl = this.receivedEventsUrl,
        eventsUrl = this.eventsUrl,
        htmlUrl = this.htmlUrl,
        siteAdmin = this.siteAdmin,
        gravatarId = this.gravatarId,
        nodeId = this.nodeId,
        organizationsUrl = this.organizationsUrl
    )
}

fun UserEntity.toDomainModel(): User {
    return User(
        id = this.id.orZero(),
        login = this.login.orEmpty(),
        avatarUrl = this.avatarUrl.orEmpty()
    )
}

fun UserDetailDto.toEntity(): UserDetailEntity {
    return UserDetailEntity(
        id = this.id,
        login = this.login,
        avatarUrl = this.avatarUrl,
        gistsUrl = this.gistsUrl,
        reposUrl = this.reposUrl,
        followingUrl = this.followingUrl,
        twitterUsername = this.twitterUsername,
        bio = this.bio,
        createdAt = this.createdAt,
        type = this.type,
        blog = this.blog,
        subscriptionsUrl = this.subscriptionsUrl,
        updatedAt = this.updatedAt,
        siteAdmin = this.siteAdmin,
        company = this.company,
        publicRepos = this.publicRepos,
        gravatarId = this.gravatarId,
        email = this.email,
        organizationsUrl = this.organizationsUrl,
        hireable = this.hireable,
        starredUrl = this.starredUrl,
        followersUrl = this.followersUrl,
        publicGists = this.publicGists,
        url = this.url,
        receivedEventsUrl = this.receivedEventsUrl,
        followers = this.followers,
        eventsUrl = this.eventsUrl,
        htmlUrl = this.htmlUrl,
        following = this.following,
        name = this.name,
        location = this.location,
        nodeId = this.nodeId
    )
}

fun UserDetailEntity.toDomainModel(): UserDetail {
    return UserDetail(
        id = this.id.orZero(),
        login = this.login.orEmpty(),
        avatarUrl = this.avatarUrl.orEmpty(),
        name = this.name,
        company = this.company,
        location = this.location,
        bio = this.bio,
        followers = this.followers.orZero(),
        following = this.following.orZero(),
        publicRepos = this.publicRepos.orZero(),
        blog = this.blog
    )
}