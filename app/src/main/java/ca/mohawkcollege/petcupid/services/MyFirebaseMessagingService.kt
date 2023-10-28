package ca.mohawkcollege.petcupid.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "====MyFirebaseMessagingService===="

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "onMessageReceived: ${message.from}")
        if (message.notification != null) {
            val msg = message.notification!!
            Log.d(TAG, "onMessageReceived: ${msg.channelId}")
            Log.d(TAG, "onMessageReceived: ${msg.body}")
            Log.d(TAG, "onMessageReceived: ${message.data}")
        }
    }

    override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: $token")
    }
}