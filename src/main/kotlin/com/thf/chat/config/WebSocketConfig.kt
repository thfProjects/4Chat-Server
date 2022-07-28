package com.thf.chat.config

import com.thf.chat.controller.ChatWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.*
import org.springframework.web.socket.server.HandshakeInterceptor
import java.lang.Exception
import javax.websocket.server.PathParam

@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {

    override fun registerWebSocketHandlers (registry: WebSocketHandlerRegistry) {
        registry.addHandler(ChatWebSocketHandler(), "/chatwebsocket/*").addInterceptors(ChatHandshakeInterceptor())
    }

    private class ChatHandshakeInterceptor : HandshakeInterceptor {

        override fun beforeHandshake(
            request: ServerHttpRequest,
            response: ServerHttpResponse,
            wsHandler: WebSocketHandler,
            attributes: MutableMap<String, Any>
        ): Boolean {
            val username = request.uri.path.substringAfterLast("/")
            return if (username.matches(Regex("[A-Za-z0-9 ]*"))) {
                attributes["username"] = username
                true
            }else
                false
        }

        override fun afterHandshake(
            request: ServerHttpRequest,
            response: ServerHttpResponse,
            wsHandler: WebSocketHandler,
            exception: Exception?
        ) {
            //do nothing
        }

    }
}