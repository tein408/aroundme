package com.aroundme.likes

import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class LikeService (
    private val likeRepository: LikeRepository
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Creates a like on a content by user
     *
     * @param contentId, userId
     * @return void
     */
    @Transactional
    fun addLike(contentId: Long, userId: Long) {
        logger.info("Service - Adding like to $contentId by $userId")
        val currentTime = LocalDateTime.now()
        val findLikes = likeRepository.findByContentIdAndUserId(contentId, userId)

        if (findLikes != null) {
            throw IllegalStateException("User $userId already liked this content $contentId")
        }

        val likes = Likes(
            contentId = contentId,
            userId = userId,
            createdTime = currentTime
        )
        val savedLikes = likeRepository.save(likes)
        logger.info("Service - Saving like to $contentId by $userId successfully: $savedLikes")
    }

}