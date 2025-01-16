package com.aroundme.content

import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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
        // TODO(https://github.com/tein408/aroundme/issues/2): Add session information to the log
        logger.info("Controller - Getting all contents")
        val contentList = contentService.getContentList()
        return ResponseEntity
            .ok()
            .body(contentList)
    }

    /**
     * Create content
     *
     * @param createContentDTO
     * @return content details
     */
    @PostMapping("/contents")
    fun createContent(@RequestBody createContentDTO: CreateContentDTO): ResponseEntity<ReadContentDetailDTO> {
        // TODO(https://github.com/tein408/aroundme/issues/2): Add session information to the log
        logger.info("Controller - Creating new content")
        createContentDTO.validate()
        val readContentDetailDTO = contentService.createContent(createContentDTO)
        return ResponseEntity
            .ok()
            .body(readContentDetailDTO)
    }
}