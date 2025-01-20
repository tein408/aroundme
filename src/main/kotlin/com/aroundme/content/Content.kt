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
    var category: String,
    var content: String,
    var media: String,
    val createdTime: LocalDateTime,
    var updatedTime: LocalDateTime
) {
    fun toReadContentDTO(): ReadContentDTO {
        return ReadContentDTO(
            contentId = contentId,
            content = content,
            category = category,
            media = media,
            createdTime = createdTime,
            updatedTime = updatedTime
        )
    }

    fun toReadContentDetailDTO(): ReadContentDetailDTO {
        return ReadContentDetailDTO(
            contentId = contentId,
            content = content,
            category = category,
            media = media,
            createdTime = createdTime,
            updatedTime = updatedTime
        )
    }
}