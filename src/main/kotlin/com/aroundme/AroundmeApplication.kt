package com.aroundme

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AroundmeApplication

fun main(args: Array<String>) {
	runApplication<AroundmeApplication>(*args)
}
