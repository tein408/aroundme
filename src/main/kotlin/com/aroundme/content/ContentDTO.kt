package com.aroundme.content

import java.time.LocalDateTime

data class ReadContentDTO (
    val contentId: Long? = null,
    val category: String,
    val feed: String,
    val media: String,
    val createdTime: LocalDateTime,
    val updatedTime: LocalDateTime
)

data class CreateContentDTO (
    val category: String,
    val feed: String,
    val media: String,
    val createdTime: LocalDateTime,
    val updatedTime: LocalDateTime
) {
    fun validate() {
        if (category.isBlank()) throw IllegalArgumentException("Category must not be blank")
        if (feed.isBlank()) throw IllegalArgumentException("Content must not be blank")
        if (media.isBlank()) throw IllegalArgumentException("Media must not be blank")
    }
}

data class ReadContentDetailDTO(
    val contentId: Long?,
    val category: String,
    val feed: String,
    val media: String,
    val createdTime: LocalDateTime,
    val updatedTime: LocalDateTime
)

data class UpdateContentDTO (
    val category: String,
    val feed: String,
    val media: String,
) {
    fun validate() {
        if (category.isBlank()) throw IllegalArgumentException("Category must not be blank")
        if (feed.isBlank()) throw IllegalArgumentException("Content must not be blank")
        if (media.isBlank()) throw IllegalArgumentException("Media must not be blank")
    }
}
