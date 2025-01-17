package com.aroundme.content

import java.time.LocalDateTime

data class ReadContentDTO (
    val id: Long? = null,
    val category: String,
    val content: String,
    val media: String,
    val createdTime: LocalDateTime
)

data class CreateContentDTO (
    val category: String,
    val content: String,
    val media: String,
    val createdTime: LocalDateTime
) {
    fun validate() {
        if (category.isBlank()) throw IllegalArgumentException("Category must not be blank")
        if (content.isBlank()) throw IllegalArgumentException("Content must not be blank")
        if (media.isBlank()) throw IllegalArgumentException("Media must not be blank")
    }
}

data class ReadContentDetailDTO(
    val id: Long?,
    val category: String,
    val content: String,
    val media: String,
    val createdTime: LocalDateTime
)