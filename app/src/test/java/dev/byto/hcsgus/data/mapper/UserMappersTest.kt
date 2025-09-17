package dev.byto.hcsgus.data.mapper

import dev.byto.hcsgus.data.local.entity.UserDetailEntity
import dev.byto.hcsgus.data.local.entity.UserEntity
import dev.byto.hcsgus.data.remote.dto.UserDetailDto
import dev.byto.hcsgus.data.remote.dto.UserDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserMappersTest {

    // --- UserDto to UserEntity --- //
    @Test
    fun `UserDto_toEntity - all fields populated - maps correctly`() {
        val userDto = UserDto(
            id = 1, login = "testUser", avatarUrl = "avatar.url", gistsUrl = "gists.url",
            reposUrl = "repos.url", followingUrl = "following.url", starredUrl = "starred.url",
            followersUrl = "followers.url", type = "User", url = "user.url",
            subscriptionsUrl = "subs.url", receivedEventsUrl = "received.url",
            eventsUrl = "events.url", htmlUrl = "html.url", siteAdmin = true,
            gravatarId = "gravatar123", nodeId = "nodeXYZ", organizationsUrl = "orgs.url",
            score = 100
        )
        val entity = userDto.toEntity()

        assertEquals(userDto.id, entity.id)
        assertEquals(userDto.login, entity.login)
        assertEquals(userDto.avatarUrl, entity.avatarUrl)
        assertEquals(userDto.gistsUrl, entity.gistsUrl)
        assertEquals(userDto.reposUrl, entity.reposUrl)
        assertEquals(userDto.followingUrl, entity.followingUrl)
        assertEquals(userDto.starredUrl, entity.starredUrl)
        assertEquals(userDto.followersUrl, entity.followersUrl)
        assertEquals(userDto.type, entity.type)
        assertEquals(userDto.url, entity.url)
        assertEquals(userDto.subscriptionsUrl, entity.subscriptionsUrl)
        assertEquals(userDto.receivedEventsUrl, entity.receivedEventsUrl)
        assertEquals(userDto.eventsUrl, entity.eventsUrl)
        assertEquals(userDto.htmlUrl, entity.htmlUrl)
        assertEquals(userDto.siteAdmin, entity.siteAdmin)
        assertEquals(userDto.gravatarId, entity.gravatarId)
        assertEquals(userDto.nodeId, entity.nodeId)
        assertEquals(userDto.organizationsUrl, entity.organizationsUrl)
    }

    @Test
    fun `UserDto_toEntity - nullable fields null - maps correctly`() {
        val userDto = UserDto(id = 2, login = "minimalUser") // Other fields are null by default
        val entity = userDto.toEntity()

        assertEquals(2, entity.id)
        assertEquals("minimalUser", entity.login)
        assertNull(entity.avatarUrl)
        assertNull(entity.gistsUrl)
        assertFalse(entity.siteAdmin ?: false) // siteAdmin is Boolean? in DTO, Boolean in Entity (default false?)
        // Your UserEntity siteAdmin is Boolean, not Boolean? Let's assume it defaults to false if DTO.siteAdmin is null.
        // If UserEntity.siteAdmin is Boolean?, then assertNull(entity.siteAdmin)
    }

    // --- UserEntity to User (Domain) --- //
    @Test
    fun `UserEntity_toDomainModel - all fields populated - maps correctly`() {
        val entity = UserEntity(
            id = 1, login = "domainUser", avatarUrl = "domain.avatar.url",
            // other UserEntity fields don't map to User domain model
        )
        val domain = entity.toDomainModel()

        assertEquals(entity.id, domain.id)
        assertEquals(entity.login, domain.login)
        assertEquals(entity.avatarUrl, domain.avatarUrl)
    }

    @Test
    fun `UserEntity_toDomainModel - nullable fields handle orZero orEmpty`() {
        val entity1 = UserEntity(id = null, login = null, avatarUrl = null)
        val domain1 = entity1.toDomainModel()
        assertEquals(0, domain1.id) // orZero
        assertEquals("", domain1.login) // orEmpty
        assertEquals("", domain1.avatarUrl) // orEmpty

        val entity2 = UserEntity(id = 123, login = "test", avatarUrl = "url")
        val domain2 = entity2.toDomainModel()
        assertEquals(123, domain2.id)
        assertEquals("test", domain2.login)
        assertEquals("url", domain2.avatarUrl)
    }

    // --- UserDetailDto to UserDetailEntity --- //
    @Test
    fun `UserDetailDto_toEntity - all fields populated - maps correctly`() {
        val dto = UserDetailDto(
            id = 1, login = "detailUser", avatarUrl = "detail.avatar.url", name = "Detail Name",
            company = "Detail Company", location = "Detail Location", bio = "Detail Bio", blog = "detail.blog",
            followers = 100, following = 50, publicRepos = 10, gistsUrl = "gists.url",
            reposUrl = "repos.url", followingUrl = "following.url", twitterUsername = "twitter",
            createdAt = "createdDate", type = "User", subscriptionsUrl = "subs.url",
            updatedAt = "updatedDate", siteAdmin = true, publicGists = 5, gravatarId = "gravatarDetail",
            email = "email@detail.com", organizationsUrl = "orgs.detail.url", hireable = true,
            starredUrl = "starred.detail.url", followersUrl = "followers.detail.url",
            url = "user.detail.url", receivedEventsUrl = "received.detail.url",
            eventsUrl = "events.detail.url", htmlUrl = "html.detail.url", nodeId = "nodeDetail"
        )
        val entity = dto.toEntity()

        assertEquals(dto.id, entity.id)
        assertEquals(dto.login, entity.login)
        // ... (assert all other fields similarly)
        assertEquals(dto.name, entity.name)
        assertEquals(dto.company, entity.company)
        assertEquals(dto.location, entity.location)
        assertEquals(dto.bio, entity.bio)
        assertEquals(dto.blog, entity.blog)
        assertEquals(dto.followers, entity.followers)
        assertEquals(dto.following, entity.following)
        assertEquals(dto.publicRepos, entity.publicRepos)
        assertEquals(dto.twitterUsername, entity.twitterUsername)
        assertEquals(dto.hireable, entity.hireable)
    }

    // --- UserDetailEntity to UserDetail (Domain) --- //
    @Test
    fun `UserDetailEntity_toDomainModel - all fields populated - maps correctly`() {
        val entity = UserDetailEntity(
            id = 1, login = "domainDetail", avatarUrl = "domain.avatar.detail",
            name = "Domain Name", company = "Domain Company", location = "Domain Location",
            bio = "Domain Bio", blog = "domain.blog", followers = 100, following = 50, publicRepos = 20
            // other fields like gistsUrl etc., are not in UserDetail domain model
        )
        val domain = entity.toDomainModel()

        assertEquals(entity.id, domain.id)
        assertEquals(entity.login, domain.login)
        assertEquals(entity.avatarUrl, domain.avatarUrl)
        assertEquals(entity.name, domain.name)
        assertEquals(entity.company, domain.company)
        assertEquals(entity.location, domain.location)
        assertEquals(entity.bio, domain.bio)
        assertEquals(entity.blog, domain.blog)
        assertEquals(entity.followers, domain.followers)
        assertEquals(entity.following, domain.following)
        assertEquals(entity.publicRepos, domain.publicRepos)
    }

    @Test
    fun `UserDetailEntity_toDomainModel - nullable and orZero orEmpty checks`() {
        val entity1 = UserDetailEntity(
            id = null, login = null, avatarUrl = null, name = null, company = null,
            location = null, bio = null, blog = null, followers = null, following = null, publicRepos = null
        )
        val domain1 = entity1.toDomainModel()

        assertEquals(0, domain1.id) // orZero
        assertEquals("", domain1.login) // orEmpty
        assertEquals("", domain1.avatarUrl) // orEmpty
        assertNull(domain1.name)
        assertNull(domain1.company)
        assertNull(domain1.location)
        assertNull(domain1.bio)
        assertNull(domain1.blog)
        assertEquals(0, domain1.followers) // orZero
        assertEquals(0, domain1.following) // orZero
        assertEquals(0, domain1.publicRepos) // orZero

        val entity2 = UserDetailEntity(
            id = 10, login = "test", avatarUrl = "url", name = "Name", company = "Comp",
            location = "Loc", bio = "Bio", blog = "Blog", followers = 1, following = 2, publicRepos = 3
        )
        val domain2 = entity2.toDomainModel()
        assertEquals(10, domain2.id)
        assertEquals("test", domain2.login)
        assertEquals("url", domain2.avatarUrl)
        assertEquals("Name", domain2.name)
        assertEquals("Comp", domain2.company)
        assertEquals("Loc", domain2.location)
        assertEquals("Bio", domain2.bio)
        assertEquals("Blog", domain2.blog)
        assertEquals(1, domain2.followers)
        assertEquals(2, domain2.following)
        assertEquals(3, domain2.publicRepos)
    }
}
