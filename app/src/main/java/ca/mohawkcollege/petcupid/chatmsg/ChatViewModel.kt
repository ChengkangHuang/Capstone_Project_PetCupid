package ca.mohawkcollege.petcupid.chatmsg

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ca.mohawkcollege.petcupid.ChatActivity
import com.google.firebase.database.Query
import java.util.regex.Pattern

/**
 * The ChatViewModel class.
 * @property chatUid The UID of the chat.
 * @property senderUid The UID of the sender.
 * @property receiverUid The UID of the receiver.
 * @property chatRepository The ChatRepository object.
 */
class ChatViewModel: ViewModel() {

    private val TAG = "====ChatViewModel===="
    private val chatUid: String = ChatActivity.chatUid
    private val senderUid: String = ChatActivity.senderUid
    private val receiverUid: String = ChatActivity.receiverUid
    private val chatRepository = ChatRepository()

    // The database query for the chat messages.
    val query : Query
        get() = chatRepository.getDBQuery(chatUid)


    // The chat messages from the database.
    val chatMessageLiveData : LiveData<List<ChatMessage>>
        get() = chatRepository.getChatMessages(chatUid)

    /**
     * Sends a message to the database.
     * @param message The message to send.
     */
    private fun sendMsg(message: Message) {
        val currentTimestamp = System.currentTimeMillis()
        val uniqueMessageId = senderUid + currentTimestamp.toString() + receiverUid
        val chatMessage = ChatMessage(
            uniqueMessageId,
            senderUid,
            receiverUid,
            message,
            currentTimestamp
        )
        chatRepository.sendMessage(chatUid, chatMessage)
    }

    /**
     * Calls sendMsg() to send text message to the database.
     * @param message The text message to send.
     */
    fun sendTextMsg(message: String) {
        sendMsg(Message(MessageType.TEXT, message))
    }

    /**
     * Uploads a file to the database.
     * @param uri The URI of the file to upload.
     */
    fun uploadFile(uri: String) {
        chatRepository.uploadMedia(uri) { uploadUri, exception ->
            if (exception != null) {
                Log.d(TAG, "uploadFile: failure")
            } else {
                Log.d(TAG, "uploadFile: success")
                val messageType = determineMediaMessageType(uploadUri.toString())
                sendMsg(Message(messageType, uploadUri.toString()))
            }
        }
    }

    /**
     * Sets an appointment.
     * @param year The year of the appointment.
     * @param month The month of the appointment.
     * @param day The day of the appointment.
     * @param hour The hour of the appointment.
     * @param minute The minute of the appointment.
     */
    fun setAppointment(year: Long, month: Long, day: Long, hour: Long, minute: Long) {
        val appointment = Appointment(year, month, day, hour, minute, 0)
        sendMsg(Message(MessageType.APPOINTMENT, appointment))
    }

    /**
     * Determines the message type.
     * @param message The message to determine the type of.
     */
    private fun determineMediaMessageType(message: String): MessageType {
        val mediaRegex = Regex("^https://firebasestorage.googleapis.com/.*\\.(jpg|jpeg|png|gif|mp4|mp3)")
        val imagePattern = Pattern.compile("\\.(jpg|jpeg|png|gif)", Pattern.CASE_INSENSITIVE)
        val videoPattern = Pattern.compile("\\.(mp4|avi|mkv|mov|wmv|flv)", Pattern.CASE_INSENSITIVE)
        val audioPattern = Pattern.compile("\\.(mp3|wav|ogg|flac)", Pattern.CASE_INSENSITIVE)

        return if (mediaRegex.containsMatchIn(message)) {
            when {
                imagePattern.matcher(message).find() -> MessageType.IMAGE
                videoPattern.matcher(message).find() -> MessageType.VIDEO
                audioPattern.matcher(message).find() -> MessageType.AUDIO
                else -> MessageType.UNKNOWN
            }
        } else {
            MessageType.UNKNOWN
        }
    }
}
