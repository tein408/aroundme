package com.aroundme.content

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class ContentRepositoryTest {
    @Autowired
    private lateinit var contentRepository: ContentRepository

    @Test
    fun `should delete a content successfully`() {
        val content = Content(
            category = "Test Category",
            feed = "Test Content",
            media = "Test Media",
            createdTime = LocalDateTime.now(),
            updatedTime = LocalDateTime.now()
        )
        val savedContent = contentRepository.save(content)
        val contentId = savedContent.contentId!!
        assertEquals(contentRepository.findById(contentId).orElse(null), savedContent)

        contentRepository.deleteById(contentId)

        assertTrue(contentRepository.findById(contentId).isEmpty)
    }
}
