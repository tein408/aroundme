package com.aroundme.content

import org.springframework.data.repository.CrudRepository

interface ContentRepository: CrudRepository<Content, Int> {
    fun findAllBy(): List<Content>
}