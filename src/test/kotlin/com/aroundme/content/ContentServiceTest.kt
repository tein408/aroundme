package com.aroundme.content

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class ContentServiceTest {

    private val contentRepository: ContentRepository = mockk()
    private val contentService = ContentService(contentRepository)

    @Test
    fun `should return content list`() {
        val contentId = 1L
        val mockContent = Content(
            contentId = 1L,
            category = "Software",
            content = "Recent trend",
            media = "/img/trend.jpg",
            createdTime = LocalDateTime.now(),
            updatedTime = LocalDateTime.now()
        )
        val contentList = listOf(mockContent)
        every { contentRepository.findAllBy() } returns contentList

        val content = contentService.getContentList()

        assertThat(content.size).isEqualTo(1)
        assertThat(content[0].contentId).isEqualTo(contentId)
    }

    @Test
    fun `should create content successfully`() {
        val createContentDTO = CreateContentDTO(
            category = "Technology",
            content = "Kotlin is a great programming language.",
            media = "https://example.com/kotlin.png",
            createdTime = LocalDateTime.now(),
            updatedTime = LocalDateTime.now()
        )
        val currentTime = LocalDateTime.now()
        val mockContent = Content(
            contentId = 1L,
            category = createContentDTO.category,
            content = createContentDTO.content,
            media = createContentDTO.media,
            createdTime = currentTime,
            updatedTime = currentTime
        )
        every { contentRepository.save(any()) } returns mockContent
        val result = contentService.createContent(createContentDTO)

        assertEquals(createContentDTO.category, result?.category)
        assertEquals(createContentDTO.content, result?.content)
        assertEquals(createContentDTO.media, result?.media)

        verify(exactly = 1) { contentRepository.save(any()) }
    }

    @Test
    fun `should throw an exception when content length exceeds limit`() {
        val createContentDTO = CreateContentDTO(
            category = "Technology",
            content = "A".repeat(501),
            media = "https://example.com/long_content.png",
            createdTime = LocalDateTime.now(),
            updatedTime = LocalDateTime.now()
        )
        val exception = assertThrows<IllegalArgumentException> {
            contentService.createContent(createContentDTO)
        }

        assertEquals("Content length exceeds the limit of 500 characters", exception.message)
        verify(exactly = 0) { contentRepository.save(any()) }
    }

    @Test
    fun `should return content detail when content exists`() {
        val contentId = 1L
        val findContent = Content(
            contentId = contentId,
            category = "Technology",
            content = "Sample content",
            media = "/image.jpg",
            createdTime = LocalDateTime.now(),
            updatedTime = LocalDateTime.now()
        )
        every { contentRepository.findByIdOrNull(contentId) } returns findContent

        val result = contentService.getContentDetail(contentId)

        assertEquals(contentId, result.contentId)
        assertEquals("Technology", result.category)
        assertEquals("Sample content", result.content)
        assertEquals("/image.jpg", result.media)
        assertEquals(findContent.createdTime, result.createdTime)
    }

    @Test
    fun `should throw an exception when a content does not exist`() {
        val contentId = 99L
        every { contentRepository.findByIdOrNull(contentId) } returns null

        val exception = assertThrows<IllegalArgumentException> {
            contentService.getContentDetail(contentId)
        }

        assertEquals("Content with id $contentId not found", exception.message)
    }
}
