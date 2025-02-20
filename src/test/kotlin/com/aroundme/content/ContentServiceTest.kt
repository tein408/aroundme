package com.aroundme.content

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate
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
            feed = "Recent trend",
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
            feed = "Kotlin is a great programming language.",
            media = "https://example.com/kotlin.png",
            createdTime = LocalDateTime.now(),
            updatedTime = LocalDateTime.now()
        )
        val currentTime = LocalDateTime.now()
        val mockContent = Content(
            contentId = 1L,
            category = createContentDTO.category,
            feed = createContentDTO.feed,
            media = createContentDTO.media,
            createdTime = currentTime,
            updatedTime = currentTime
        )
        every { contentRepository.save(any()) } returns mockContent
        val result = contentService.createContent(createContentDTO)

        assertEquals(createContentDTO.category, result?.category)
        assertEquals(createContentDTO.feed, result?.feed)
        assertEquals(createContentDTO.media, result?.media)

        verify(exactly = 1) { contentRepository.save(any()) }
    }

    @Test
    fun `should throw an exception when content length exceeds limit`() {
        val createContentDTO = CreateContentDTO(
            category = "Technology",
            feed = "A".repeat(501),
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
            feed = "Sample content",
            media = "/image.jpg",
            createdTime = LocalDateTime.now(),
            updatedTime = LocalDateTime.now()
        )
        every { contentRepository.findByIdOrNull(contentId) } returns findContent

        val result = contentService.getContentDetail(contentId)

        assertEquals(contentId, result.contentId)
        assertEquals("Technology", result.category)
        assertEquals("Sample content", result.feed)
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
            feed = "Updated Content",
            media = "Updated Media"
        )
        val existingContent = Content(
            contentId = contentId,
            category = "Old Category",
            feed = "Old Content",
            media = "Old Media",
            createdTime = LocalDateTime.now().minusDays(1),
            updatedTime = LocalDateTime.now().minusDays(1)
        )
        val updatedContent = Content(
            contentId = contentId,
            category = "Updated Category",
            feed = "Updated Content",
            media = "Updated Media",
            createdTime = existingContent.createdTime,
            updatedTime = LocalDateTime.now()
        )

        every { contentRepository.findByIdOrNull(contentId) } returns existingContent
        every { contentRepository.save(any()) } returns updatedContent

        val result = contentService.updateContent(contentId, updateContentDTO)
        assertEquals("Updated Category", result.category)
        assertEquals("Updated Content", result.feed)
        assertEquals("Updated Media", result.media)
        assertNotNull(result.updatedTime)
    }

    @Test
    fun `should throw an exception when updating content if the content text does not exist`() {
        val contentId = 1L
        val updateContentDTO = UpdateContentDTO(
            category = "Updated Category",
            feed = "",
            media = "Updated Media"
        )
        val existingContent = Content(
            contentId = contentId,
            category = "Old Category",
            feed = "Old Content",
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
            feed = "Updated Content",
            media = "Updated Media"
        )
        every { contentRepository.findByIdOrNull(contentId) } returns null

        val exception = assertThrows<IllegalArgumentException> {
            contentService.updateContent(contentId, updateContentDTO)
        }

        assertEquals("Content with id $contentId not found", exception.message)
    }

    @Test
    fun `should delete a content successfully`() {
        val contentId = 1L
        val content = Content(
            contentId = contentId,
            category = "Test Category",
            feed = "Test Content",
            media = "Test Media",
            createdTime = LocalDateTime.now(),
            updatedTime = LocalDateTime.now()
        )
        every { contentRepository.findByIdOrNull(contentId) } returns content
        justRun { contentRepository.delete(content) }

        contentService.deleteContent(contentId)

        verify(exactly = 1) { contentRepository.findByIdOrNull(contentId) }
        verify(exactly = 1) { contentRepository.delete(content) }
    }

    @Test
    fun `should throw an exception when deleting content if the content does not exist`() {
        val contentId = 99L
        every { contentRepository.findByIdOrNull(contentId) } returns null

        val exception = assertThrows<IllegalArgumentException> {
            contentService.deleteContent(contentId)
        }
        assertEquals("Content with id $contentId not found", exception.message)
        verify(exactly = 1) { contentRepository.findByIdOrNull(contentId) }
        verify(exactly = 0) { contentRepository.delete(any()) }
    }

    @Test
    fun `should return a list of contents when query matches`() {
        val query = "example"
        val mockContents = listOf(
            Content(
                contentId = 1L,
                category = "Category1",
                feed = "This is an example content",
                media = "image1.jpg",
                createdTime = LocalDateTime.now().minusDays(2),
                updatedTime = LocalDateTime.now()
            ),
            Content(
                contentId = 2L,
                category = "Category2",
                feed = "Another example2 content",
                media = "image2.jpg",
                createdTime = LocalDateTime.now().minusDays(3),
                updatedTime = LocalDateTime.now()
            )
        )
        every { contentRepository.findAllByFeedContains(query) } returns mockContents

        val result = contentService.searchContent(query)

        assertEquals(2, result.size)
        assertEquals("Category1", result[0].category)
        assertEquals("This is an example content", result[0].feed)
        assertEquals("image1.jpg", result[0].media)
        assertEquals("Category2", result[1].category)
        assertEquals("Another example2 content", result[1].feed)
        assertEquals("image2.jpg", result[1].media)
        verify { contentRepository.findAllByFeedContains(query) }
    }

    @Test
    fun `should return an empty list when query does not match any content`() {
        val query = "nonexistent"
        every { contentRepository.findAllByFeedContains(query) } returns emptyList()

        val result = contentService.searchContent(query)

        assertTrue(result.isEmpty())
        verify { contentRepository.findAllByFeedContains(query) }
    }

    @Test
    fun `should filter contents by created time`() {
        val startDate = LocalDate.of(2025, 1, 1)
        val endDate = LocalDate.of(2025, 1, 10)
        val mockContents = listOf(
            Content(
                contentId = 1L,
                category = "sample",
                feed = "Sample feed 1",
                media = "Test Media",
                createdTime = LocalDateTime.of(2025, 1, 5, 12, 0),
                updatedTime = LocalDateTime.of(2025, 1, 5, 12, 0)
            ),
            Content(
                contentId = 2L,
                category = "sample",
                feed = "Sample feed 2",
                media = "Test Media",
                createdTime = LocalDateTime.of(2025, 1, 9, 18, 0),
                updatedTime = LocalDateTime.of(2025, 1, 9, 18, 0)
            )
        )
        every { contentRepository.findAllByCreatedTimeBetween(any(), any()) } returns mockContents

        val result = contentService.filterByCreatedTime(startDate.toString(), endDate.toString())

        assertEquals(2, result.size)
        verify { contentRepository.findAllByCreatedTimeBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)) }
    }

    @Test
    fun `should filter contents by created time when start date only provided`() {
        val startDate = LocalDate.of(2025, 1, 1)
        val mockContents = listOf(
            Content(
                contentId = 1L,
                category = "sample",
                feed = "Sample feed 1",
                media = "Test Media",
                createdTime = LocalDateTime.of(2025, 1, 5, 12, 0),
                updatedTime = LocalDateTime.of(2025, 1, 5, 12, 0)
            ),
            Content(
                contentId = 2L,
                category = "sample",
                feed = "Sample feed 2",
                media = "Test Media",
                createdTime = LocalDateTime.of(2025, 1, 9, 18, 0),
                updatedTime = LocalDateTime.of(2025, 1, 9, 18, 0)
            )
        )
        every { contentRepository.findAllByCreatedTimeBetween(any(), any()) } returns mockContents

        val result = contentService.filterByCreatedTime(startDate.toString(), null)

        assertEquals(2, result.size)
        verify { contentRepository.findAllByCreatedTimeBetween(startDate.atStartOfDay(), startDate.atTime(23, 59, 59)) }
    }

    @Test
    fun `should filter contents by created time when end date only provided`() {
        val endDate = LocalDate.of(2025, 1, 10)
        val mockContents = listOf(
            Content(
                contentId = 1L,
                category = "sample",
                feed = "Sample feed 1",
                media = "Test Media",
                createdTime = LocalDateTime.of(2025, 1, 5, 12, 0),
                updatedTime = LocalDateTime.of(2025, 1, 5, 12, 0)
            ),
            Content(
                contentId = 2L,
                category = "sample",
                feed = "Sample feed 2",
                media = "Test Media",
                createdTime = LocalDateTime.of(2025, 1, 9, 18, 0),
                updatedTime = LocalDateTime.of(2025, 1, 9, 18, 0)
            )
        )
        every { contentRepository.findAllByCreatedTimeBetween(any(), any()) } returns mockContents

        val result = contentService.filterByCreatedTime(null, endDate.toString())

        assertEquals(2, result.size)
        verify { contentRepository.findAllByCreatedTimeBetween(endDate.atStartOfDay(), endDate.atTime(23, 59, 59)) }
    }

    @Test
    fun `should throw an exception when not valid dates are provided`() {
        val mockContents = listOf(
            Content(
                contentId = 1L,
                category = "sample",
                feed = "Sample feed 1",
                media = "Test Media",
                createdTime = LocalDateTime.of(2025, 1, 5, 12, 0),
                updatedTime = LocalDateTime.of(2025, 1, 5, 12, 0)
            ),
            Content(
                contentId = 2L,
                category = "sample",
                feed = "Sample feed 2",
                media = "Test Media",
                createdTime = LocalDateTime.of(2025, 1, 9, 18, 0),
                updatedTime = LocalDateTime.of(2025, 1, 9, 18, 0)
            )
        )
        every { contentRepository.findAllByCreatedTimeBetween(any(), any()) } returns mockContents

        val exception = assertThrows<IllegalArgumentException> {
            contentService.filterByCreatedTime(null, null)
        }

        assertEquals("At least one of startDate or endDate must be provided", exception.message)
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
        assertThat(result[0].feed).isEqualTo("Tech News")
        assertThat(result[1].feed).isEqualTo("AI Trends")
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
