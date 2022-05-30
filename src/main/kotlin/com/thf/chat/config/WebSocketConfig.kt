package com.thf.chat.config

import com.thf.chat.controller.ChatWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.*

@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {

    override fun registerWebSocketHandlers (registry: WebSocketHandlerRegistry) {
        registry.addHandler(ChatWebSocketHandler(), "/chatwebsocket")
    }
}