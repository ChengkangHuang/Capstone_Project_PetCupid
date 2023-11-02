package ca.mohawkcollege.petcupid.userprofile

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserProfileRepository {

    private val TAG = "====UserProfileRepository===="
    private val storeDB = Firebase.firestore
    private val query = storeDB.collection("users")

    fun setProfileData() {}

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

    fun updateProfileData(newData: User, callback: (User?, Exception?) -> Unit) {

    }

    fun deleteProfileData() {}
}