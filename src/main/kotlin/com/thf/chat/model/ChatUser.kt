package com.thf.chat.model

import org.springframework.web.socket.WebSocketSession

class ChatUser(val id: Int, val session: WebSocketSession) {
}