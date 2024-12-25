package com.aroundme.content

import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(ContentController::class)
class ContentControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var contentService: ContentService

    @Test
    fun `should return content list`() {
        val contentList = listOf(
            ReadContentDTO(id = 1L, category = "Software", content = "John Doe's log", media = "/image.jpg", addedAt = LocalDateTime.now()),
            ReadContentDTO(id = 2L, category = "Network", content = "Jane Smith's announce", media = "/tech.jpg", addedAt = LocalDateTime.now())
        )
        given(contentService.getContentList()).willReturn(contentList)

        mockMvc.perform(get("/contents")
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
}
