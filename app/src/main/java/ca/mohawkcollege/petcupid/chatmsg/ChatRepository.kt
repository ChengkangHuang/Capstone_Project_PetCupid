package ca.mohawkcollege.petcupid.chatmsg

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatRepository {

    private val TAG = "====ChatRepository===="
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun getDBQuery(chatUid: String): DatabaseReference {
        return databaseRef.child("messages").child(chatUid).apply {
            keepSynced(true)
        }
    }

    fun getChatMessages(chatUid: String): LiveData<List<ChatMessage>> {
        val chatMessagesLiveData = MutableLiveData<List<ChatMessage>>()

        getDBQuery(chatUid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatMessages = mutableListOf<ChatMessage>()
                for (child in snapshot.children) {
                    val chatMessage = child.getValue(ChatMessage::class.java)
                    chatMessage?.let { chatMessages.add(it) }
                }
                chatMessagesLiveData.value = chatMessages
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "====onCancelled====")
            }
        })

        return chatMessagesLiveData
    }

    fun sendMessage(chatUid: String, chatMessage: ChatMessage) {
        databaseRef.child("messages").child(chatUid).push().setValue(chatMessage)
    }
}
