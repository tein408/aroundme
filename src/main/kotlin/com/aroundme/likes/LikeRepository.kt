package com.aroundme.likes

import org.springframework.data.repository.CrudRepository

interface LikeRepository: CrudRepository<Likes, LikesId> {
    fun findLikesByLikeId(likeId: LikesId): Likes?
}