package com.aroundme.likes

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
    name = "content_likes",
)
data class Likes (
    @EmbeddedId
    val likeId: LikesId,

    @Column(nullable = false)
    val createdTime: LocalDateTime
) {
}