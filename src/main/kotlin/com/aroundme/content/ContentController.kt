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

    /**
     * Updates a content to cover text and media
     *
     * @param updateContentDTO
     * @return content details
     */
    @PatchMapping("/contents/{contentId}")
    fun updateContent(@PathVariable contentId: Long, @RequestBody updateContentDTO: UpdateContentDTO): ResponseEntity<ReadContentDetailDTO> {
        logger.info("Controller - Updating content by id: $contentId and updated content: $updateContentDTO")
        val readContentDetailDTO = contentService.updateContent(contentId, updateContentDTO)
        return ResponseEntity
            .ok()
            .body(readContentDetailDTO)
    }

    /**
     * Deletes a content through contentId
     *
     * @param contentId
     * @return ResponseEntity<Void>
     */
    @DeleteMapping("/contents/{contentId}")
    fun deleteContent(@PathVariable contentId: Long): ResponseEntity<Void> {
        contentService.deleteContent(contentId)
        return ResponseEntity.noContent().build()
    }

    /**
     * Searches contents through query
     *
     * @param query
     * @return content list
     */
    @GetMapping("/contents/search")
    fun searchContent(@RequestParam query: String): ResponseEntity<List<ReadContentDTO>> {
        logger.info("Controller - Searching contents: $query")
        val searchResult = contentService.searchContent(query)
        return ResponseEntity.ok().body(searchResult)
    }

}