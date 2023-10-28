package ca.mohawkcollege.petcupid.chatmsg

import android.annotation.SuppressLint
import java.io.Serializable

/**
 * The chat message data class.
 * @param messageId The ID of the message.
 * @param senderUid The UID of the sender.
 * @param receiverUid The UID of the receiver.
 * @param message The message.
 * @param timestamp The timestamp of the message.
 * @constructor Creates a chat message.
 */
data class ChatMessage(
    val messageId: String,
    val senderUid: String,
    val receiverUid: String,
    val message: Message,
    val timestamp: Long
) : Serializable {
    constructor() : this("", "", "", Message(), 0)

    /**
     * Converts the timestamp to a string.
     * @return The timestamp as a string.
     */
    @SuppressLint("SimpleDateFormat")
    fun timestampToString(): String {
        val timeZone = java.util.TimeZone.getTimeZone("America/Toronto")
        val dateFormat = java.text.SimpleDateFormat("HH:mm a")
        dateFormat.timeZone = timeZone
        return dateFormat.format(this.timestamp)
    }
}

/**
 * The message data class.
 * @param type The type of message.
 * @param content The content of the message.
 * @constructor Creates a message.
 */
data class Message(
    val type: MessageType?,
    val content: Any
) : Serializable {
    constructor() : this(MessageType.UNKNOWN, "")
}

/**
 * The appointment data class.
 * @param year The year of the appointment.
 * @param month The month of the appointment.
 * @param day The day of the appointment.
 * @param hour The hour of the appointment.
 * @param minute The minute of the appointment.
 * @param appointmentConfirmation The appointment confirmation.
 * @constructor Creates an appointment.
 * @see MessageType.APPOINTMENT
 */
data class Appointment(
    var year: Long,
    var month: Long,
    var day: Long,
    var hour: Long,
    var minute: Long,
    var appointmentConfirmation: Long,
) : Serializable {
    constructor() : this(0, 0, 0, 0, 0, 0)

    constructor(hashMap: HashMap<String, Long>) : this(
        hashMap["year"] as Long,
        hashMap["month"] as Long,
        hashMap["day"] as Long,
        hashMap["hour"] as Long,
        hashMap["minute"] as Long,
        hashMap["appointmentConfirmation"] as Long
    )

    /**
     * Converts the appointment to a HashMap.
     * @return The HashMap.
     */
    fun toMap(): HashMap<String, Long> {
        return hashMapOf(
            "year" to year,
            "month" to month,
            "day" to day,
            "hour" to hour,
            "minute" to minute,
            "appointmentConfirmation" to appointmentConfirmation
        )
    }
}

/**
 * The type of message.
 */
enum class MessageType {
    TEXT, IMAGE, VIDEO, AUDIO, APPOINTMENT, UNKNOWN
}