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
                id = 1L,
                category = "Software",
                content = "John Doe's log",
                media = "/image.jpg",
                createdTime = LocalDateTime.now()
            ),
            ReadContentDTO(
                id = 2L,
                category = "Network",
                content = "Jane Smith's announce",
                media = "/tech.jpg",
                createdTime = LocalDateTime.now()
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
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].category").value("Software"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].category").value("Network"))
    }

    @Test
    fun `should create content successfully`() {
        val createContentDTO = CreateContentDTO(
            category = "Technology",
            content = "Kotlin is amazing!",
            media = "https://example.com/image.png",
            createdTime = LocalDateTime.now()
        )

        val readContentDetailDTO = ReadContentDetailDTO(
            id = 1L,
            category = createContentDTO.category,
            content = createContentDTO.content,
            media = createContentDTO.media,
            createdTime = LocalDateTime.now()
        )

        every { contentService.createContent(createContentDTO) } returns readContentDetailDTO

        mockMvc.perform(
            post("/contents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createContentDTO))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
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
            createdTime = LocalDateTime.now()
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
}
