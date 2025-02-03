package com.aroundme.likes

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class LikeServiceDatabaseTest {

    @Autowired
    private lateinit var likeRepository: LikeRepository

    private lateinit var likeService: LikeService

    @BeforeEach
    fun setup() {
        likeService = LikeService(likeRepository)
    }

    @Test
    fun `should successfully add a like to database`() {
        val contentId = 1L
        val userId = 100L

        likeService.addLike(contentId, userId)

        val savedLike = likeRepository.findByContentIdAndUserId(contentId, userId)
        assertNotNull(savedLike)
        assertEquals(contentId, savedLike?.contentId)
        assertEquals(userId, savedLike?.userId)
    }

    @Test
    fun `should prevent duplicate likes`() {
        val contentId = 1L
        val userId = 100L
        likeService.addLike(contentId, userId)

        val exception = assertThrows<IllegalStateException> {
            likeService.addLike(contentId, userId)
        }

        assertEquals("User $userId already liked this content $contentId", exception.message)
        val findLikes = likeRepository.findByContentIdAndUserId(contentId, userId)
        assertEquals(contentId, findLikes?.contentId)
        assertEquals(userId, findLikes?.userId)
    }

}
