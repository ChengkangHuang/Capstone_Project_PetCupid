package ca.mohawkcollege.petcupid.chatmsg

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ChatRepository {

    private val TAG = "====ChatRepository===="
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val storageRef = FirebaseStorage.getInstance().reference

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

    fun uploadMedia(uri: String) {
        val file = Uri.fromFile(File(uri))
        val mediaRef = storageRef.child("media").child(file.lastPathSegment!!)
        val uploadTask = mediaRef.putFile(file)
        uploadTask.addOnSuccessListener {
            Log.d(TAG, "====uploadMedia:success====")
        }.addOnFailureListener {
            Log.e(TAG, "====uploadMedia:failure====")
        }
        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            mediaRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                Log.d(TAG, "====downloadUri: $downloadUri====")
            } else {
                Log.e(TAG, "====downloadUri:failure====")
            }
        }
    }
}
