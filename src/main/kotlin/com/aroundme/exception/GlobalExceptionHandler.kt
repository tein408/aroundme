package com.aroundme.exception

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, String?>> {
        val errorResponse = mapOf(
            "error" to "Invalid Request",
            ("message" to ex.message)
        )
        logger.error(ex.message, ex)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}
