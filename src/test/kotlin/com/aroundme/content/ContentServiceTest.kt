package com.aroundme.content

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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

    @Test
    fun `should update content successfully`() {
        val contentId = 1L
        val updateContentDTO = UpdateContentDTO(
            category = "Updated Category",
            content = "Updated Content",
            media = "Updated Media"
        )
        val existingContent = Content(
            contentId = contentId,
            category = "Old Category",
            content = "Old Content",
            media = "Old Media",
            createdTime = LocalDateTime.now().minusDays(1),
            updatedTime = LocalDateTime.now().minusDays(1)
        )
        val updatedContent = Content(
            contentId = contentId,
            category = "Updated Category",
            content = "Updated Content",
            media = "Updated Media",
            createdTime = existingContent.createdTime,
            updatedTime = LocalDateTime.now()
        )

        every { contentRepository.findByIdOrNull(contentId) } returns existingContent
        every { contentRepository.save(any()) } returns updatedContent

        val result = contentService.updateContent(contentId, updateContentDTO)
        assertEquals("Updated Category", result.category)
        assertEquals("Updated Content", result.content)
        assertEquals("Updated Media", result.media)
        assertNotNull(result.updatedTime)
    }

    @Test
    fun `should throw an exception when updating content if the content's text does not exist`() {
        val contentId = 1L
        val updateContentDTO = UpdateContentDTO(
            category = "Updated Category",
            content = "",
            media = "Updated Media"
        )
        val existingContent = Content(
            contentId = contentId,
            category = "Old Category",
            content = "Old Content",
            media = "Old Media",
            createdTime = LocalDateTime.now().minusDays(1),
            updatedTime = LocalDateTime.now().minusDays(1)
        )
        every { contentRepository.findByIdOrNull(contentId) } returns existingContent

        val exception = assertThrows<IllegalArgumentException> {
            contentService.updateContent(contentId, updateContentDTO)
        }

        assertEquals("Content must not be blank", exception.message)
    }

    @Test
    fun `should throw an exception when updating content if the content does not exist`() {
        val contentId = 99L
        val updateContentDTO = UpdateContentDTO(
            category = "Updated Category",
            content = "Updated Content",
            media = "Updated Media"
        )
        every { contentRepository.findByIdOrNull(contentId) } returns null

        val exception = assertThrows<IllegalArgumentException> {
            contentService.updateContent(contentId, updateContentDTO)
        }

        assertEquals("Content with id $contentId not found", exception.message)
    }

    @Test
    fun `should return content list when valid category is provided`() {
        val category = "Technology"
        val mockContents = listOf(
            Content(1L, "Technology", "Tech News", "media1", LocalDateTime.now(), LocalDateTime.now()),
            Content(2L, "Technology", "AI Trends", "media2", LocalDateTime.now(), LocalDateTime.now())
        )
        every { contentRepository.findAllByCategoryIs(category) } returns mockContents

        val result = contentService.filterByCategory(category)

        assertThat(result).hasSize(2)
        assertThat(result[0].content).isEqualTo("Tech News")
        assertThat(result[1].content).isEqualTo("AI Trends")
        verify(exactly = 1) { contentRepository.findAllByCategoryIs(category) }
    }

    @Test
    fun `should return empty list when category does not exist`() {
        val category = "NonExistentCategory"
        every { contentRepository.findAllByCategoryIs(category) } returns emptyList()

        val result = contentService.filterByCategory(category)

        assertThat(result).isEmpty()
        verify(exactly = 1) { contentRepository.findAllByCategoryIs(category) }
    }

    @Test
    fun `should throw IllegalArgumentException when category is empty`() {
        val category = ""

        val exception = assertThrows<IllegalArgumentException> {
            contentService.filterByCategory(category)
        }
        assertEquals("Invalid category string", exception.message)
    }
}
