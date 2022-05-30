package com.thf.chat.controller

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.thf.chat.model.ChatRoom
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatWebSocketHandler : TextWebSocketHandler() {

    private val rooms = ArrayList<ChatRoom>()

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {

        val msg = Gson().fromJson(message.payload, JsonObject::class.java)
        val room = findRoomById(msg.get("roomId").asInt)

        room?.apply {
            msg.apply {
                remove("roomId")
                addProperty("type", "chat")
            }
            getSessions().forEach() {
                it.sendMessage(TextMessage(msg.toString()))
            }
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {

        if (rooms.isEmpty() || rooms.last().full) rooms.add(ChatRoom(generateChatRoomId()))
        val room = rooms.last()

        room.add(session)

        val greetingMessage = JsonObject().apply {
            addProperty("type", "greeting")
            addProperty("roomId", room.id)
        }

        session.sendMessage(TextMessage(greetingMessage.toString()))

        println(rooms.toString())
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {

        val iterator = rooms.iterator()

        while (iterator.hasNext()) {
            val room = iterator.next()
            if (room.contains(session)) {
                room.remove(session)
                if (room.empty) iterator.remove()
                break
            }
        }

        println(rooms.toString())
    }

    private fun generateChatRoomId (): Int {
        var i = 0
        while (true) {
            rooms.forEach {
                if (i == it.id) {
                    i++
                    return@forEach
                }
            }
            return i
        }
    }

    private fun findRoomById (id: Int): ChatRoom? {
        rooms.forEach {
            if (it.id == id) return it
        }
        return null
    }
}