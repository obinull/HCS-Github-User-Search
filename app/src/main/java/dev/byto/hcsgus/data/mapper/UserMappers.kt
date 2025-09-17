package dev.byto.hcsgus.data.mapper

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
        bio = null,
        followers = null,
        following = null,
        createdAt = null,
        publicRepos = null,
        blog = null,
        subscriptionsUrl = this.subscriptionsUrl,
        updatedAt = null,
        siteAdmin = this.siteAdmin,
        company = null,
        followingUrl = this.followingUrl,
        followersUrl = this.followersUrl,
        reposUrl = this.reposUrl,
        gravatarId = this.gravatarId,
        email = null,
        organizationsUrl = this.organizationsUrl,
        name = null,
        location = null,
        publicGists = null,
        url = this.url
    )
}

fun UserEntity.toDomainModel(): User {
    return User(
        id = this.id.orZero(),
        login = this.login.orEmpty(),
        avatarUrl = this.avatarUrl.orEmpty()
    )
}

fun UserDetailDto.toDomainModel(): UserDetail {
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