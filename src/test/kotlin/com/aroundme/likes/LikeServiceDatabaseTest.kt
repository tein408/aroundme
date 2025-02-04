package com.aroundme.likes

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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

        likeService.increaseLikeCount(contentId, userId)

        val savedLike = likeRepository.findById(LikesId(contentId, userId)).orElse(null)
        assertNotNull(savedLike)
        assertEquals(contentId, savedLike?.likeId?.contentId)
        assertEquals(userId, savedLike?.likeId?.userId)
    }

    @Test
    fun `should raise an exception when a pair of contentId and userId is already counted for likes`() {
        val contentId = 1L
        val userId = 100L
        likeService.increaseLikeCount(contentId, userId)

        val exception = assertThrows<IllegalStateException> {
            likeService.increaseLikeCount(contentId, userId)
        }

        assertEquals("User $userId already liked this content $contentId", exception.message)
        val findLikes = likeRepository.findById(LikesId(contentId, userId)).orElse(null)
        assertEquals(contentId, findLikes?.likeId?.contentId)
        assertEquals(userId, findLikes?.likeId?.userId)
    }

}
