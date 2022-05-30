package com.thf.chat.model

import org.springframework.web.socket.WebSocketSession
import java.util.Collections

class ChatRoom(val id: Int) {

    companion object {
        const val MAX_SESSIONS = 4
    }

    private val sessions = ArrayList<WebSocketSession>()

    val full: Boolean
        get() = sessions.size == MAX_SESSIONS

    val empty: Boolean
        get() = sessions.isEmpty()

    fun contains (session: WebSocketSession) = sessions.contains(session)

    fun add (session: WebSocketSession) {
        if (!full) sessions.add(session)
    }

    fun remove (session: WebSocketSession) {
        sessions.remove(session)
    }

    fun getSessions () = Collections.unmodifiableList(sessions)

    override fun toString(): String {
        return "id: $id, sessions: ${sessions.size}"
    }
}