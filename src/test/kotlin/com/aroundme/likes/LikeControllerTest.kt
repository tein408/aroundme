package com.aroundme.likes

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.justRun
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(LikeController::class)
class LikeControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var likeService: LikeService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should like content and return No Content`() {
        val contentId = 1L
        val userId = 100L

        justRun {  likeService.addLike(contentId, userId) }

        mockMvc.perform(
            post("/contents/$contentId/likes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userId))
        )
            .andExpect(status().isNoContent)
    }

}