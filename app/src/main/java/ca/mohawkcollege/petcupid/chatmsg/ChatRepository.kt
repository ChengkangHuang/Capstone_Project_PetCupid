package ca.mohawkcollege.petcupid.chatmsg

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ca.mohawkcollege.petcupid.tools.ChatUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ChatRepository {

    private val TAG = "====ChatRepository===="
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val storageRef = FirebaseStorage.getInstance().reference

    /**
     * Gets the database query for the chat messages.
     * @param chatUid The UID of the chat.
     * @return A DatabaseReference object.
     */
    fun getDBQuery(chatUid: String): DatabaseReference {
        return databaseRef.child("messages").child(chatUid).apply {
            keepSynced(true)
        }
    }

    /**
     * Gets the chat messages from the database.
     * @param chatUid The UID of the chat.
     * @return A LiveData object containing a list of ChatMessage objects.
     * The list of ChatMessage objects will be updated whenever the database changes.
     */
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

    /**
     * Sends a message to the chat.
     * @param chatUid The UID of the chat.
     * @param chatMessage The chat message to be sent.
     */
    fun sendMessage(chatUid: String, chatMessage: ChatMessage) {
        databaseRef.child("messages")
            .child(chatUid)
            .child(chatMessage.messageId)
            .setValue(chatMessage)
    }

    /**
     * Uploads a media file to Firebase Storage.
     * @param uri The URI of the media file.
     * @param callback The callback function to be called when the upload is complete.
     * The callback function takes two parameters: Uri? and Exception?
     * Uri? is the download URL of the media file.
     * Exception? is the exception that occurred during the upload.
     * If the upload was successful, Uri? will be non-null and Exception? will be null.
     * If the upload was unsuccessful, Uri? will be null and Exception? will be non-null.
     */
    fun uploadMedia(uri: String, callback: (Uri?, Exception?) -> Unit) {
        val file = Uri.fromFile(File(uri))
        val mediaRef = storageRef.child("media").child(file.lastPathSegment!!)
        val uploadTask = mediaRef.putFile(file)
        uploadTask.addOnProgressListener {(bytesTransferred, totalBytes) ->
            val progress = (100.0 * bytesTransferred) / totalBytes
            Log.d(TAG, "====uploadMedia:progress: $progress====")
        }.addOnSuccessListener {
            Log.d(TAG, "====uploadMedia:success====")
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                    callback(null, task.exception)
                }
                mediaRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Log.d(TAG, "====downloadUri: $downloadUri====")
                    callback(downloadUri, null)
                } else {
                    Log.e(TAG, "====downloadUri:failure====")
                }
            }
        }.addOnFailureListener {
            Log.e(TAG, "====uploadMedia:failure====")
        }
    }

    /**
     * Uploads a voice record to Firebase Storage.
     * @param file The voice record file.
     * @param fileName The name of the voice record file.
     * @return The download URL of the voice record file.
     */
    suspend fun uploadVoiceRecord(file: File, fileName: String): String {
        return suspendCoroutine { continuation ->
            val fileUri = Uri.fromFile(file)
            val audioRef = storageRef.child("audio").child(fileName)
            val uploadTask = audioRef.putFile(fileUri)
            uploadTask.addOnSuccessListener {
                Log.d(TAG, "uploadVoiceRecord: start uploading")
                audioRef.downloadUrl.addOnSuccessListener { uri ->
                    continuation.resume(uri.toString())
                    Log.d(TAG, "getDownloadUri: success")
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                    Log.e(TAG, "getDownloadUri: failure")
                }
            }.addOnFailureListener {
                continuation.resumeWithException(it)
                Log.e(TAG, "uploadVoiceRecord: failure")
            }
        }
    }

    /**
     * Updates the appointment in the database.
     * @param chatUid The UID of the chat.
     * @param messageId The ID of the message.
     * @param updatedContent The updated appointment.
     */
    fun updateAppointment(chatUid: String, messageId: String, updatedContent: Appointment) {
        val childUpdates = hashMapOf<String, Any>(
            "/messages/$chatUid/$messageId/message/content" to updatedContent
        )
        databaseRef.updateChildren(childUpdates)
    }
}
