package com.aroundme.content

import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * Content service class
 */
@Service
@Transactional(readOnly = true)
class ContentService (
    private val contentRepository: ContentRepository
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Get content list
     *
     * @return content list
     */
    fun getContentList(): List<ReadContentDTO> {
        // TODO(https://github.com/tein408/aroundme/issues/2): Add session information to the log
        logger.info("Service - Getting content list")
        val contentList = contentRepository.findAllBy()
        return contentList.map { it.toReadContentDTO() }
    }

    /**
     * Creates a content to cover text and media.
     *
     * @param createContentDTO
     * @return ReadContentDetailDTO
     */
    @Transactional
    fun createContent(createContentDTO: CreateContentDTO): ReadContentDetailDTO? {
        // TODO(https://github.com/tein408/aroundme/issues/2): Add session information to the log
        logger.info("Service - Creating new content: $createContentDTO")
        val currentTime = LocalDateTime.now()

        validateContent(createContentDTO.content)

        val content = Content(
            category = createContentDTO.category,
            content = createContentDTO.content,
            media = createContentDTO.media,
            createdTime = currentTime,
            updatedTime = currentTime
        )
        val savedContent = contentRepository.save(content)
        logger.info { "Service - Content created successfully: $savedContent" }

        return ReadContentDetailDTO(
            savedContent.contentId,
            createContentDTO.category,
            createContentDTO.content,
            createContentDTO.media,
            createContentDTO.createdTime,
            createContentDTO.updatedTime
        )
    }

    private fun validateContent(content: String) {
        if (content.length > 500) {
            throw IllegalArgumentException("Content length exceeds the limit of 500 characters")
        }
    }

    /**
     * Gets content details when requested via content id
     *
     * @param contentId the ID of the content to retrieve
     * @return the content details or throws an exception if not found
     */
    fun getContentDetail(contentId: Long): ReadContentDetailDTO {
        logger.info("Service - Fetching content with ID: $contentId")
        val content = contentRepository.findByIdOrNull(contentId)
            ?: throw IllegalArgumentException("Content with id $contentId not found")
        return content.toReadContentDetailDTO()
    }

    /**
     * Updates a content to cover text and media
     *
     * @param updateContentDTO
     * @return content details
     */
    @Transactional
    fun updateContent(contentId: Long, updateContentDTO: UpdateContentDTO): ReadContentDetailDTO {
        logger.info("Service - Updating content by id: $contentId and updated content: $updateContentDTO")

        updateContentDTO.validate()
        validateContent(updateContentDTO.content)

        val currentTime = LocalDateTime.now()
        val findContent = contentRepository.findByIdOrNull(contentId)
            ?: throw IllegalArgumentException("Content with id $contentId not found")

        updateContentDTO.category.let { findContent.category = it }
        updateContentDTO.content.let { findContent.content = it }
        updateContentDTO.media.let { findContent.media = it }
        findContent.updatedTime = currentTime

        return ReadContentDetailDTO(
            contentId,
            updateContentDTO.category,
            updateContentDTO.content,
            updateContentDTO.media,
            findContent.createdTime,
            findContent.updatedTime
        )
    }

    /**
     * Deletes a content through contentId
     *
     * @param contentId
     * @return void
     */
    @Transactional
    fun deleteContent(contentId: Long) {
        logger.info("Service - Deleting content by id: $contentId")
        val content = contentRepository.findByIdOrNull(contentId)
            ?: throw IllegalArgumentException("Content with id $contentId not found")
        contentRepository.delete(content)
        logger.info("Content with id $contentId has been deleted")
    }

}