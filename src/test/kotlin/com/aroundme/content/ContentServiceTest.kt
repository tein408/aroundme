package com.aroundme.content

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
class ContentServiceTest {

    @Mock
    private lateinit var contentRepository: ContentRepository

    @InjectMocks
    private lateinit var contentService: ContentService

    @Test
    fun `should return content list`() {
        val contentId = 1L
        val mockContent = Content(id = 1L, category = "Software", content = "Recent trend", media = "/img/trend.jpg", createdTime = LocalDateTime.now())
        val contentList = listOf(mockContent)
        whenever(contentRepository.findAllBy()).thenReturn(contentList)

        val content = contentService.getContentList()

        assertThat(content.size).isEqualTo(1)
        assertThat(content[0].id).isEqualTo(contentId)
    }

}