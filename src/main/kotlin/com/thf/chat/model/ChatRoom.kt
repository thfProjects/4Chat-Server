package com.thf.chat.model

import org.springframework.web.socket.WebSocketSession
import java.util.Collections
import java.util.regex.Pattern

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
        if (!full) {
            var name = session.attributes["username"].toString()
            val nameOccurrences = nameOccurences(name)
            if (nameOccurrences != 0) name = name.plus("(${nameOccurrences})")
            session.attributes["username"] = name
            session.attributes["roomId"] = id
            sessions.add(session)
        }
    }

    fun remove (session: WebSocketSession) {
        sessions.remove(session)
    }

    fun find (username: String): WebSocketSession? {
        return sessions.find { it.attributes["username"] == username}
    }

    fun getSessions () = Collections.unmodifiableList(sessions)

    private fun nameOccurences(name: String): Int {
        var exists = 0
        sessions.forEach {
            if (Regex("${name}(\\(\\d\\))??").matches(it.attributes["username"].toString())) exists++
        }
        return exists
    }

    override fun toString(): String {
        return "id: $id, sessions: ${sessions.size}"
    }
}