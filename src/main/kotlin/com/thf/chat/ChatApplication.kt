package com.thf.chat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@Controller
class ChatApplication {

	@RequestMapping
	fun getPage(): String {
		return "index.html"
	}
}

fun main(args: Array<String>) {
	runApplication<ChatApplication>(*args)
}


