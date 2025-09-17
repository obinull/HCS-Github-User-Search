package dev.byto.hcsgus.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class UserDetailTest {

    @Test
    fun `userDetail properties are assigned correctly`() {
        val id = 1
        val login = "octocat"
        val avatarUrl = "https://avatars.githubusercontent.com/u/583231?v=4"
        val name = "The Octocat"
        val company = "@github"
        val location = "San Francisco"
        val bio = "A short bio about the Octocat."
        val blog = "https.github.blog"
        val followers = 1000
        val following = 10
        val publicRepos = 8

        val userDetail = UserDetail(
            id = id,
            login = login,
            avatarUrl = avatarUrl,
            name = name,
            company = company,
            location = location,
            bio = bio,
            blog = blog,
            followers = followers,
            following = following,
            publicRepos = publicRepos
        )

        assertEquals(id, userDetail.id)
        assertEquals(login, userDetail.login)
        assertEquals(avatarUrl, userDetail.avatarUrl)
        assertEquals(name, userDetail.name)
        assertEquals(company, userDetail.company)
        assertEquals(location, userDetail.location)
        assertEquals(bio, userDetail.bio)
        assertEquals(blog, userDetail.blog)
        assertEquals(followers, userDetail.followers)
        assertEquals(following, userDetail.following)
        assertEquals(publicRepos, userDetail.publicRepos)
    }

    @Test
    fun `userDetail properties with nullables assigned correctly as null`() {
        val id = 2
        val login = "anotheruser"
        val avatarUrl = "https://example.com/avatar.png"
        val name: String? = null
        val company: String? = null
        val location: String? = null
        val bio: String? = null
        val blog: String? = null
        val followers = 50
        val following = 5
        val publicRepos = 2

        val userDetail = UserDetail(
            id = id,
            login = login,
            avatarUrl = avatarUrl,
            name = name,
            company = company,
            location = location,
            bio = bio,
            blog = blog,
            followers = followers,
            following = following,
            publicRepos = publicRepos
        )

        assertEquals(id, userDetail.id)
        assertEquals(login, userDetail.login)
        assertEquals(avatarUrl, userDetail.avatarUrl)
        assertEquals(null, userDetail.name)
        assertEquals(null, userDetail.company)
        assertEquals(null, userDetail.location)
        assertEquals(null, userDetail.bio)
        assertEquals(null, userDetail.blog)
        assertEquals(followers, userDetail.followers)
        assertEquals(following, userDetail.following)
        assertEquals(publicRepos, userDetail.publicRepos)
    }
}
