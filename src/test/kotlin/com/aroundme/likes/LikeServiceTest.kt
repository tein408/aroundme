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
    fun `should successfully call addlike function when user has not liked before`() {
        val contentId = 1L
        val userId = 100L
        val like = Likes(
            contentId = contentId,
            userId = userId,
            createdTime = LocalDateTime.now()
        )
        every { likeRepository.findByContentIdAndUserId(contentId, userId) } returns null
        every { likeRepository.save(any()) } returns like

        likeService.addLike(contentId, userId)

        verify { likeRepository.findByContentIdAndUserId(contentId, userId) }
        verify { likeRepository.save(any()) }
    }

    @Test
    fun `should throw an exception when user already liked the content`() {
        val contentId = 1L
        val userId = 100L
        val mockLikes = Likes(
            likeId = 1L,
            contentId = contentId,
            userId = userId,
            createdTime = LocalDateTime.now()
        )
        every { likeRepository.findByContentIdAndUserId(contentId, userId) } returns mockLikes

        val exception = assertThrows<IllegalStateException> {
            likeService.addLike(contentId, userId)
        }

        assertEquals("User $userId already liked this content $contentId", exception.message)
        verify { likeRepository.findByContentIdAndUserId(contentId, userId) }
        verify(exactly = 0) { likeRepository.save(any()) }
    }

}