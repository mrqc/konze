package net.masterstudios.konze.agent.logging

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Logger {
    @JvmStatic
    public fun info(message: String) {
        val threadId = Thread.currentThread().threadId()
        println("[AGENT] [${getTime()}] [Thread-$threadId] $message")
    }
    @JvmStatic
    public fun error(message: String) {
        val threadId = Thread.currentThread().threadId()
        error("[AGENT] [${getTime()}] [Thread-$threadId] $message")
    }
    
    fun getTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            .format(Date())
    }
}
