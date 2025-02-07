package com.aroundme.likes

import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Likes controller class
 */
@RestController
class LikeController (
    private val likeService: LikeService
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Increases a like on a content by user
     *
     * @param contentId, userId
     * @return ResponseEntity<Void>
     */
    @PostMapping("/contents/{contentId}/like")
    fun likeContent(@PathVariable contentId: Long, @RequestBody userId: Long): ResponseEntity<Void> {
        logger.info("Liking content $contentId $userId")
        likeService.increaseLikeCount(contentId, userId)
        return ResponseEntity.noContent().build()
    }
}