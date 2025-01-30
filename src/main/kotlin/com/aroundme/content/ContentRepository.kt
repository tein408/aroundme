package com.aroundme.content

import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface ContentRepository: CrudRepository<Content, Long> {
    fun findAllBy(): List<Content>
    fun findAllByFeedContains(contentContains: String): List<Content>
    fun findAllByCreatedTimeBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Content>
}