package ca.mohawkcollege.petcupid.chatmsg

import android.annotation.SuppressLint
import java.io.Serializable

data class ChatMessage(
    val messageId: String,
    val senderUid: String,
    val receiverUid: String,
    val message: String,
    val timestamp: Long
) : Serializable {
    constructor() : this("", "", "", "", 0)

    @SuppressLint("SimpleDateFormat")
    fun timestampToString(): String {
        val timeZone = java.util.TimeZone.getTimeZone("America/Toronto")
        val dateFormat = java.text.SimpleDateFormat("HH:mm a")
        dateFormat.timeZone = timeZone
        return dateFormat.format(this.timestamp)
    }
}