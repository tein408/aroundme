package com.aroundme.content

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
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
                content = "John Doe's log",
                media = "/image.jpg",
                createdTime = LocalDateTime.now(),
                updatedTime = LocalDateTime.now()
            ),
            ReadContentDTO(
                contentId = 2L,
                category = "Network",
                content = "Jane Smith's announce",
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
            content = "Kotlin is amazing!",
            media = "https://example.com/image.png",
            createdTime = LocalDateTime.now(),
            updatedTime = LocalDateTime.now()
        )
        val readContentDetailDTO = ReadContentDetailDTO(
            contentId = 1L,
            category = createContentDTO.category,
            content = createContentDTO.content,
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
            .andExpect(jsonPath("$.content").value("Kotlin is amazing!"))
            .andExpect(jsonPath("$.media").value("https://example.com/image.png"))
    }

    @Test
    fun `should return bad request when content is invalid`() {
        val invalidContentDTO = CreateContentDTO(
            category = "",
            content = "",
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
            content = "Sample content",
            media = "/image.jpg",
            createdTime = LocalDateTime.now(),
            updatedTime = LocalDateTime.now()
        )
        every { contentService.getContentDetail(contentId) } returns readContentDetailDTO

        mockMvc.perform(get("/contents/$contentId")
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.contentId").value(contentId))
            .andExpect(jsonPath("$.category").value("Technology"))
            .andExpect(jsonPath("$.content").value("Sample content"))
            .andExpect(jsonPath("$.media").value("/image.jpg"))
    }

    @Test
    fun `should return 404 when content does not exist`() {
        val contentId = 99L
        every { contentService.getContentDetail(contentId) } throws IllegalArgumentException("Content with id $contentId not found")

        mockMvc.perform(get("/contents/$contentId")
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
            content = "Updated Content",
            media = "Updated Media"
        )
        val updatedContent = ReadContentDetailDTO(
            contentId = contentId,
            category = "Updated Category",
            content = "Updated Content",
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
            .andExpect(jsonPath("$.content").value("Updated Content"))
            .andExpect(jsonPath("$.media").value("Updated Media"))
            .andExpect(jsonPath("$.updatedTime").exists())
    }
}
