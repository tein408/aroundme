package com.aroundme.content

import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
}