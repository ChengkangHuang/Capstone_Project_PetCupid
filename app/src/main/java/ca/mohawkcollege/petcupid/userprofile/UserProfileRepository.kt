package ca.mohawkcollege.petcupid.userprofile

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * User profile repository
 */
class UserProfileRepository {

    private val TAG = "====UserProfileRepository===="
    private val storeDB = Firebase.firestore
    private val query = storeDB.collection("users")

    /**
     * Get user profile data by uid
     * @param uid user id
     */
    fun getProfileDataByUid(uid: String, callback: (User?, Exception?) -> Unit) {
        query.whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val data = document.toObject(User::class.java)
                    Log.d(TAG, "getProfileDataByUid: $data")
                    callback(data, null)
                }
            }
            .addOnFailureListener { exception ->
                callback(null, exception)
            }
    }

    /**
     * Update user profile data
     * @param uid user id
     * @param field field to update
     * @param newValue new value
     */
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

    /**
     * Delete user profile data
     * @param deleteData user data to delete
     */
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