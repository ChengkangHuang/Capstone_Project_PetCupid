package ca.mohawkcollege.petcupid.chatmsg

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ca.mohawkcollege.petcupid.ChatActivity
import com.google.firebase.database.Query
import java.util.regex.Pattern

class ChatViewModel: ViewModel() {

    private val TAG = "====ChatViewModel===="
    private val chatUid: String = ChatActivity.chatUid
    private val senderUid: String = ChatActivity.senderUid
    private val receiverUid: String = ChatActivity.receiverUid
    private val chatRepository = ChatRepository()

    val query : Query
        get() = chatRepository.getDBQuery(chatUid)

    val chatMessageLiveData : LiveData<List<ChatMessage>>
        get() = chatRepository.getChatMessages(chatUid)

    fun sendMsg(message: String) {
        val messageType = determineMessageFromUrl(message)
        val chatMessage = ChatMessage(
            "test-msg",
            messageType,
            senderUid,
            receiverUid,
            message,
            System.currentTimeMillis()
        )
        chatRepository.sendMessage(chatUid, chatMessage)
    }

    private fun determineMessageFromUrl(url: String): MessageType {
        val regex = Regex("^https://firebasestorage.googleapis.com/.*\\.(jpg|jpeg|png|gif|bmp|svg|webp|mp4|avi|mov|wmv|flv|mp3|wav|ogg)$")
        val imagePattern = Pattern.compile("\\.(jpg|jpeg|png|gif|bmp|webp)$", Pattern.CASE_INSENSITIVE)
        val videoPattern = Pattern.compile("\\.(mp4|avi|mkv|mov|wmv|flv)$", Pattern.CASE_INSENSITIVE)
        val audioPattern = Pattern.compile("\\.(mp3|wav|ogg|flac)$", Pattern.CASE_INSENSITIVE)

        return if (regex.matches(url)) {
            when {
                imagePattern.matcher(url).find() -> MessageType.IMAGE
                videoPattern.matcher(url).find() -> MessageType.VIDEO
                audioPattern.matcher(url).find() -> MessageType.AUDIO
                else -> MessageType.TEXT
            }
        } else {
            MessageType.TEXT
        }
    }

    fun uploadFile(uri: String) {
        chatRepository.uploadMedia(uri)
    }
}