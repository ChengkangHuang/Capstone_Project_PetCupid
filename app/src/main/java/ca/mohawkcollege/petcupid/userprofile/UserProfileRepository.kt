package ca.mohawkcollege.petcupid.userprofile

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserProfileRepository {

    private val TAG = "====UserProfileRepository===="
    private val storeDB = Firebase.firestore
    private val query = storeDB.collection("users")

    fun getProfileDataByUid(uid: String, callback: (User?, Exception?) -> Unit) {
        query.whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val data = document.toObject(User::class.java)
                    callback(data, null)
                }
            }
            .addOnFailureListener { exception ->
                callback(null, exception)
            }
    }

    fun updateProfileData(uid: String, field: String, newValue: String) {
        query.whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    query.document(document.id)
                        .update(field, newValue)
                        .addOnSuccessListener { Log.d(TAG, "updateProfileData: $field | $newValue") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                }
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error getting documents.", e) }
    }

    fun deleteProfileData(deleteData: User, callback: (User?, Exception?) -> Unit) {
        query.whereEqualTo("uid", deleteData.uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    query.document(document.id)
                        .delete()
                        .addOnSuccessListener {
                            callback(deleteData, null)
                        }
                        .addOnFailureListener { exception ->
                            callback(null, exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                callback(null, exception)
            }
    }
}