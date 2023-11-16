package ca.mohawkcollege.petcupid.signup

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class SignupRepository {

    private val TAG = "====SignupRepository===="

    /**
     * Signup a user with the given email and password.
     * @param email The email of the user.
     * @param password The password of the user.
     * @param callback The callback function to be called when the signup is complete.
     */
    fun signup(email: String, password: String, callback: (FirebaseUser?, Exception?) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = task.result?.user
                    callback(currentUser, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }

    /**
     * Get the token of the current user.
     * @return The token of the current user.
     */
    fun getToken(callback: (String?, Exception?) -> Unit) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    callback(null, task.exception)
                } else {
                    val token = task.result?.toString()
                    callback(token, null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Fetching FCM registration token failed", exception)
                callback(null, exception)
            }
    }

    /**
     * Save the user to the database.
     * @param uid The uid of the user.
     * @param userName The username of the user.
     * @param phoneNumber The phone number of the user.
     * @param email The email of the user.
     * @param token The token of the user.
     */
    fun saveToDatabase(uid: String, userName: String, phoneNumber: String, email: String, token: String) {
        val user = hashMapOf(
            "uid" to uid,
            "username" to userName,
            "phone" to phoneNumber,
            "email" to email,
            "token" to token,
            "icon" to ""
        )
        val db = Firebase.firestore
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}