package com.aroundme.content

import mu.KotlinLogging
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
     * Create content
     *
     * @param createContentDTO
     * @return ReadContentDetailDTO
     */
    fun createContent(createContentDTO: CreateContentDTO): ReadContentDetailDTO? {
        // TODO(https://github.com/tein408/aroundme/issues/2): Add session information to the log
        logger.info("Service - Creating new content: $createContentDTO")
        val currentTime = LocalDateTime.now()

        validateContent(createContentDTO)

        val content = Content(
            category = createContentDTO.category,
            content = createContentDTO.content,
            media = createContentDTO.media,
            createdTime = currentTime
        )

        val savedContent = contentRepository.save(content)
        logger.info { "Service - Content created successfully: $savedContent" }

        return ReadContentDetailDTO(
            savedContent.id,
            createContentDTO.category,
            createContentDTO.content,
            createContentDTO.media,
            currentTime
        )
    }

    private fun validateContent(createContentDTO: CreateContentDTO) {
        if (createContentDTO.content.length > 500) {
            throw IllegalArgumentException("Content length exceeds the limit of 500 characters")
        }
    }
}