package com.aroundme.content

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
data class Content (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val category: String,
    val content: String,
    val media: String,
    val createdTime: LocalDateTime = LocalDateTime.now()
) {
    fun toReadContentDTO(): ReadContentDTO {
        return ReadContentDTO(
            id = id,
            content = content,
            category = category,
            media = media,
            createdTime = LocalDateTime.now()
        )
    }
}