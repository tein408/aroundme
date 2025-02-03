package com.aroundme.likes

import org.springframework.data.repository.CrudRepository

interface LikeRepository: CrudRepository<Likes, Long> {
    fun findByContentIdAndUserId(contentId: Long, userId: Long): Likes?
}