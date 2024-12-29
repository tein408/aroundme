package com.aroundme.content

import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Content controller class
 */
@RestController
class ContentController (
    private val contentService: ContentService
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Get content list
     *
     * @return content list
     */
    @GetMapping("/contents")
    fun contentList(): ResponseEntity<List<ReadContentDTO>> {
        logger.info("Controller - Getting all contents")
        val contentList = contentService.getContentList()
        return ResponseEntity
            .ok()
            .body(contentList)
    }
}