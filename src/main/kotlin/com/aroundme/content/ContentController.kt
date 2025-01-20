package com.aroundme.content

import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
     * Creates a content to cover text and media.
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

    /**
     * Gets content details when requested via content id
     *
     * @param contentId
     * @return content details
     */
    @GetMapping("/contents/{contentId}")
    fun getContent(@PathVariable contentId: Long): ResponseEntity<ReadContentDetailDTO> {
        logger.info("Controller - Getting content by id $contentId")
        val readContentDetailDTO = contentService.getContentDetail(contentId)
        return ResponseEntity
            .ok()
            .body(readContentDetailDTO)
    }
}