package com.aroundme.content

import java.time.LocalDateTime

data class ReadContentDTO (
    val id: Long? = null,
    val category: String,
    val content: String,
    val media: String,
    val createdTime: LocalDateTime
)