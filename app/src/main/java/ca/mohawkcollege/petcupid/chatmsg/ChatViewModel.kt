package ca.mohawkcollege.petcupid.chatmsg

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ca.mohawkcollege.petcupid.ChatActivity
import com.google.firebase.database.Query

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
        val chatMessage = ChatMessage(
            "test-msg",
            senderUid,
            receiverUid,
            message,
            System.currentTimeMillis()
        )
        chatRepository.sendMessage(chatUid, chatMessage)
    }
}