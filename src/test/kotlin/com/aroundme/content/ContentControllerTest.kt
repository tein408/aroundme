package com.aroundme.content

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import com.ninjasquad.springmockk.MockkBean
import io.mockk.verify
import io.mockk.justRun
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.LocalDateTime

@WebMvcTest(ContentController::class)
class ContentControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var contentService: ContentService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should return content list`() {
        val contentList = listOf(
            ReadContentDTO(
                contentId = 1L,
                category = "Software",
                feed = "John Doe's log",
                media = "/image.jpg",
                createdTime = LocalDateTime.now(),
                updatedTime = LocalDateTime.now()
            ),
            ReadContentDTO(
                contentId = 2L,
                category = "Network",
                feed = "Jane Smith's announce",
                media = "/tech.jpg",
                createdTime = LocalDateTime.now(),
                updatedTime = LocalDateTime.now()
            )
        )

        every { contentService.getContentList() } returns contentList

        mockMvc.perform(
            get("/contents")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[0].contentId").value(1L))
            .andExpect(jsonPath("$[0].category").value("Software"))
            .andExpect(jsonPath("$[1].contentId").value(2L))
            .andExpect(jsonPath("$[1].category").value("Network"))
    }

    @Test
    fun `should create content successfully`() {
        val createContentDTO = CreateContentDTO(
            category = "Technology",
            feed = "Kotlin is amazing!",
            media = "https://example.com/image.png",
            createdTime = LocalDateTime.now(),
            updatedTime = LocalDateTime.now()
        )
        val readContentDetailDTO = ReadContentDetailDTO(
            contentId = 1L,
            category = createContentDTO.category,
            feed = createContentDTO.feed,
            media = createContentDTO.media,
            createdTime = LocalDateTime.now(),
            updatedTime = LocalDateTime.now()
        )
        every { contentService.createContent(createContentDTO) } returns readContentDetailDTO

        mockMvc.perform(
            post("/contents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createContentDTO))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.contentId").value(1L))
            .andExpect(jsonPath("$.category").value("Technology"))
            .andExpect(jsonPath("$.feed").value("Kotlin is amazing!"))
            .andExpect(jsonPath("$.media").value("https://example.com/image.png"))
    }

    @Test
    fun `should return bad request when content is invalid`() {
        val invalidContentDTO = CreateContentDTO(
            category = "",
            feed = "",
            media = "",
            createdTime = LocalDateTime.now(),
            updatedTime = LocalDateTime.now()
        )

        mockMvc.perform(
            post("/contents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidContentDTO))
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun `should return content detail when content exists`() {
        val contentId = 1L
        val readContentDetailDTO = ReadContentDetailDTO(
            contentId = contentId,
            category = "Technology",
            feed = "Sample content",
            media = "/image.jpg",
            createdTime = LocalDateTime.now(),
            updatedTime = LocalDateTime.now()
        )
        every { contentService.getContentDetail(contentId) } returns readContentDetailDTO

        mockMvc.perform(
            get("/contents/$contentId")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.contentId").value(contentId))
            .andExpect(jsonPath("$.category").value("Technology"))
            .andExpect(jsonPath("$.feed").value("Sample content"))
            .andExpect(jsonPath("$.media").value("/image.jpg"))
    }

    @Test
    fun `should return 404 when updating content does not exist`() {
        val contentId = 99L
        every { contentService.getContentDetail(contentId) } throws IllegalArgumentException("Content with id $contentId not found")

        mockMvc.perform(
            get("/contents/$contentId")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").value("Content with id $contentId not found"))
    }

    @Test
    fun `should update content successfully`() {
        val contentId = 1L
        val updateContentDTO = UpdateContentDTO(
            category = "Updated Category",
            feed = "Updated Content",
            media = "Updated Media"
        )
        val updatedContent = ReadContentDetailDTO(
            contentId = contentId,
            category = "Updated Category",
            feed = "Updated Content",
            media = "Updated Media",
            createdTime = LocalDateTime.now(),
            updatedTime = LocalDateTime.now()
        )
        every { contentService.updateContent(contentId, updateContentDTO) } returns updatedContent

        mockMvc.perform(
            patch("/contents/$contentId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateContentDTO))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.category").value("Updated Category"))
            .andExpect(jsonPath("$.feed").value("Updated Content"))
            .andExpect(jsonPath("$.media").value("Updated Media"))
            .andExpect(jsonPath("$.updatedTime").exists())
    }

    @Test
    fun `should delete content successfully`() {
        val contentId = 1L
        justRun { contentService.deleteContent(contentId) }

        mockMvc.perform(delete("/contents/{contentId}", contentId))
            .andExpect(status().isNoContent)
    }
    
    @Test
    fun `should return content list when valid query is provided`() {
        val query = "sample"
        val mockContentList = listOf(
            ReadContentDTO(
                contentId = 1L,
                category = "sample",
                feed = "Sample feed 1",
                media = "/sample.jpg",
                createdTime = LocalDateTime.of(2025, 1, 5, 12, 0),
                updatedTime = LocalDateTime.of(2025, 1, 5, 12, 0)
            ),
            ReadContentDTO(
                contentId = 2L,
                category = "sample2",
                feed = "Sample feed 2",
                media = "/sample2.jpg",
                createdTime = LocalDateTime.of(2025, 1, 9, 18, 0),
                updatedTime = LocalDateTime.of(2025, 1, 9, 18, 0)
            )
        )
        
        every { contentService.searchContent(query) } returns mockContentList

        mockMvc.perform(get("/contents/search")
            .param("query", query))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(mockContentList.size))
            .andExpect(jsonPath("$[0].contentId").value(mockContentList[0].contentId))
            .andExpect(jsonPath("$[0].feed").value(mockContentList[0].feed))
            .andExpect(jsonPath("$[1].contentId").value(mockContentList[1].contentId))
            .andExpect(jsonPath("$[1].feed").value(mockContentList[1].feed))
    }

    @Test
    fun `should return filtered content list when valid dates are provided`() {
        val startDate = LocalDate.of(2025, 1, 1)
        val endDate = LocalDate.of(2025, 1, 10)
        val mockContentList = listOf(
            ReadContentDTO(
                contentId = 1L,
                category = "sample",
                content = "Sample feed 1",
                media = "/sample.jpg",
                createdTime = LocalDateTime.of(2025, 1, 5, 12, 0),
                updatedTime = LocalDateTime.of(2025, 1, 5, 12, 0)
            ),
            ReadContentDTO(
                contentId = 2L,
                category = "sample2",
                content = "Sample feed 2",
                media = "/sample2.jpg",
                createdTime = LocalDateTime.of(2025, 1, 9, 18, 0),
                updatedTime = LocalDateTime.of(2025, 1, 9, 18, 0)
            )
        )

        every { contentService.filterByCreatedTime(startDate.toString(), endDate.toString()) } returns mockContentList

        mockMvc.perform(get("/contents/filter/date")
            .queryParam("startDate", startDate.toString())
            .queryParam("endDate", endDate.toString()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(mockContentList.size))
            .andExpect(jsonPath("$[0].contentId").value(mockContentList[0].contentId))
            .andExpect(jsonPath("$[0].content").value(mockContentList[0].content))
            .andExpect(jsonPath("$[1].contentId").value(mockContentList[1].contentId))
            .andExpect(jsonPath("$[1].content").value(mockContentList[1].content))
    }

    @Test
    fun `should return filtered content list when start date only provided`() {
        val startDate = LocalDate.of(2025, 1, 1)
        val mockContentList = listOf(
            ReadContentDTO(
                contentId = 1L,
                category = "sample",
                content = "Sample feed 1",
                media = "/sample.jpg",
                createdTime = LocalDateTime.of(2025, 1, 5, 12, 0),
                updatedTime = LocalDateTime.of(2025, 1, 5, 12, 0)
            ),
            ReadContentDTO(
                contentId = 2L,
                category = "sample2",
                content = "Sample feed 2",
                media = "/sample2.jpg",
                createdTime = LocalDateTime.of(2025, 1, 9, 18, 0),
                updatedTime = LocalDateTime.of(2025, 1, 9, 18, 0)
            )
        )

        every { contentService.filterByCreatedTime(startDate.toString(), "") } returns mockContentList

        mockMvc.perform(get("/contents/filter/date")
            .queryParam("startDate", startDate.toString())
            .queryParam("endDate", ""))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(mockContentList.size))
            .andExpect(jsonPath("$[0].contentId").value(mockContentList[0].contentId))
            .andExpect(jsonPath("$[0].content").value(mockContentList[0].content))
            .andExpect(jsonPath("$[1].contentId").value(mockContentList[1].contentId))
            .andExpect(jsonPath("$[1].content").value(mockContentList[1].content))
    }

    @Test
    fun `should return filtered content list when end date only provided`() {
        val endDate = LocalDate.of(2025, 1, 10)
        val mockContentList = listOf(
            ReadContentDTO(
                contentId = 1L,
                category = "sample",
                content = "Sample feed 1",
                media = "/sample.jpg",
                createdTime = LocalDateTime.of(2025, 1, 5, 12, 0),
                updatedTime = LocalDateTime.of(2025, 1, 5, 12, 0)
            ),
            ReadContentDTO(
                contentId = 2L,
                category = "sample2",
                content = "Sample feed 2",
                media = "/sample2.jpg",
                createdTime = LocalDateTime.of(2025, 1, 9, 18, 0),
                updatedTime = LocalDateTime.of(2025, 1, 9, 18, 0)
            )
        )

        every { contentService.filterByCreatedTime("", endDate.toString()) } returns mockContentList

        mockMvc.perform(get("/contents/filter/date")
            .queryParam("startDate", "")
            .queryParam("endDate", endDate.toString()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(mockContentList.size))
            .andExpect(jsonPath("$[0].contentId").value(mockContentList[0].contentId))
            .andExpect(jsonPath("$[0].content").value(mockContentList[0].content))
            .andExpect(jsonPath("$[1].contentId").value(mockContentList[1].contentId))
            .andExpect(jsonPath("$[1].content").value(mockContentList[1].content))
    }
    
    
    @Test
    fun `should return content list when valid category is provided`() {
        val category = "Technology"
        val mockContents = listOf(
            ReadContentDTO(1L,
                "Technology",
                "Tech News",
                "media1",
                LocalDateTime.now(),
                LocalDateTime.now()),
            ReadContentDTO(2L,
                "Technology",
                "AI Trends",
                "media2",
                LocalDateTime.now(),
                LocalDateTime.now())
        )
        every { contentService.filterByCategory(category) } returns mockContents

        mockMvc.perform(
            get("/contents/filter/category")
                .param("category", category)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[0].contentId").value(1))
            .andExpect(jsonPath("$[0].content").value("Tech News"))
            .andExpect(jsonPath("$[1].contentId").value(2))
            .andExpect(jsonPath("$[1].content").value("AI Trends"))
        verify(exactly = 1) { contentService.filterByCategory(category) }
    }

    @Test
    fun `should return empty list when category does not exist`() {
        val category = "NonExistentCategory"
        every { contentService.filterByCategory(category) } returns emptyList()

        mockMvc.perform(
            get("/contents/filter/category")
                .param("category", category)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.size()").value(0))
        verify(exactly = 1) { contentService.filterByCategory(category) }
    }

}
