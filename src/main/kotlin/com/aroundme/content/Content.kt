package com.aroundme.content

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
data class Content (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val contentId: Long? = null,
    val category: String,
    val content: String,
    val media: String,
    val createdTime: LocalDateTime
) {
    fun toReadContentDTO(): ReadContentDTO {
        return ReadContentDTO(
            contentId = contentId,
            content = content,
            category = category,
            media = media,
            createdTime = createdTime
        )
    }

    fun toReadContentDetailDTO(): ReadContentDetailDTO {
        return ReadContentDetailDTO(
            contentId = contentId,
            content = content,
            category = category,
            media = media,
            createdTime = createdTime
        )
    }
}