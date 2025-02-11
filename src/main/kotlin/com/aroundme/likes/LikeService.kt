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
     * Increases a like on a content by user
     *
     * @param contentId, userId
     * @return void
     */
    @Transactional
    fun increaseLikeCount(contentId: Long, userId: Long) {
        logger.info("Service - Increasing a like to $contentId by $userId")
        val currentTime = LocalDateTime.now()
        val likeId = LikesId(contentId, userId)
        val findLikes = likeRepository.findLikesByLikeId(LikesId(contentId, userId))

        if (findLikes != null) {
            throw IllegalStateException("User $userId already liked this content $contentId")
        }

        val likes = Likes(
            likeId = likeId,
            createdTime = currentTime
        )
        val savedLikes = likeRepository.save(likes)
        logger.info("Service - Saving like to $contentId by $userId successfully: $savedLikes")
    }

    /**
     * Decreases a like count on content by user
     *
     * @param contentId, userId
     * @return void
     */
    @Transactional
    fun decreaseLikeCount(contentId: Long, userId: Long) {
        logger.info("Service - Decreasing a like to $contentId by $userId")
        val findLikes = likeRepository.findLikesByLikeId(LikesId(contentId, userId))
            ?: throw IllegalStateException("User $userId is not liked this content $contentId")
        likeRepository.delete(findLikes)
        logger.info("Service - Deleting a like to $contentId by $userId successfully.")
    }

}