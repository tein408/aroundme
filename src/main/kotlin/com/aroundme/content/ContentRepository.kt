package com.aroundme.content

import org.springframework.data.repository.CrudRepository

interface ContentRepository: CrudRepository<Content, Long> {
    fun findAllBy(): List<Content>
}