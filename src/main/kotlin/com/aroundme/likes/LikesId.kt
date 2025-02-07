package com.aroundme.likes

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class LikesId(
    @Column(name = "content_id", nullable = false)
    val contentId: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long
) : Serializable
