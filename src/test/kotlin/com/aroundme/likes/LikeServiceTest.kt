package com.aroundme.likes

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class LikeServiceTest {

    private val likeRepository: LikeRepository = mockk()
    private val likeService = LikeService(likeRepository)

    @Test
    fun `should successfully call increaseLikeCount function when user has not liked before`() {
        val contentId = 1L
        val userId = 100L
        val likeId = LikesId(contentId, userId)
        val mockLikes = Likes(
            likeId = likeId,
            createdTime = LocalDateTime.now()
        )
        every { likeRepository.findLikesByLikeId(likeId) } returns null
        every { likeRepository.save(any()) } returns mockLikes

        likeService.increaseLikeCount(contentId, userId)

        verify { likeRepository.findLikesByLikeId(likeId) }
        verify { likeRepository.save(any()) }
    }

    @Test
    fun `should throw an exception when user already liked the content`() {
        val contentId = 1L
        val userId = 100L
        val likeId = LikesId(contentId, userId)
        val mockLikes = Likes(
            likeId = likeId,
            createdTime = LocalDateTime.now()
        )
        every { likeRepository.findLikesByLikeId(likeId) } returns mockLikes

        val exception = assertThrows<IllegalStateException> {
            likeService.increaseLikeCount(contentId, userId)
        }

        assertEquals("User $userId already liked this content $contentId", exception.message)
        verify { likeRepository.findLikesByLikeId(likeId) }
        verify(exactly = 0) { likeRepository.save(any()) }
    }

}