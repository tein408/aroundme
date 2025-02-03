package com.aroundme.likes

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime

@Entity
@Table(
    name = "content_likes",
    uniqueConstraints = [UniqueConstraint(columnNames = ["content_id", "user_id"])]
)
data class Likes (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val likeId: Long? = null,

    @Column(name = "content_id", nullable = false)
    val contentId: Long,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(nullable = false)
    val createdTime: LocalDateTime
) {
}