package com.thf.chat.controller

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
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
        val room = findRoomById(session.roomId)

        if (room != null) {
            msg.apply {
                remove("roomId")
                addProperty("type", "chat")
                addProperty("sender", session.username)
            }

            val whisperingTo = msg.remove("whisperingTo")?.asString

            whisperingTo?.let {
                msg.addProperty("whisperRecipient", it)
                room.find(it)?.sendMessage(TextMessage(msg.toString()))
                session.sendMessage(TextMessage(msg.toString()))
            } ?: run {
                msg.add("whisperRecipient", null)
                room.getSessions().forEach() {
                    it.sendMessage(TextMessage(msg.toString()))
                }
            }
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {

        if (rooms.isEmpty() || rooms.last().full) rooms.add(ChatRoom(generateChatRoomId()))
        val room = rooms.last()

        //add user to room
        room.add(session)

        //inform other users in room that user joined
        val userJoinedMessage = JsonObject().apply {
            addProperty("type", "userJoined")
            addProperty("username", session.username)
        }

        room.getSessions().forEach {
            if (it.username != session.username) it.sendMessage(TextMessage(userJoinedMessage.toString()))
        }

        //send info to new user
        val users = Gson().toJsonTree(
            room.getSessions().map { it.username },
            (object : TypeToken<List<String>>(){}).type
        ).asJsonArray

        val greetingMessage = JsonObject().apply {
            addProperty("type", "greeting")
            addProperty("username", session.username)
            add("users", users)
        }

        session.sendMessage(TextMessage(greetingMessage.toString()))

        println(rooms.toString())
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {

        val userLeftMessage = JsonObject().apply {
            addProperty("type", "userLeft")
            addProperty("username", session.username)
        }

        val iterator = rooms.iterator()

        while (iterator.hasNext()) {
            val room = iterator.next()
            if (room.contains(session)) {
                room.remove(session)
                if (room.empty) iterator.remove()
                else room.getSessions().forEach {
                    it.sendMessage(TextMessage(userLeftMessage.toString()))
                }
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

    private val WebSocketSession.username: String
        get() = this.attributes["username"].toString()

    private val WebSocketSession.roomId: Int
        get() = this.attributes["roomId"] as Int
}