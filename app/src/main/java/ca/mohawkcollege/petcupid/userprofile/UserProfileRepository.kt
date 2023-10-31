package ca.mohawkcollege.petcupid.userprofile

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserProfileRepository {

    private val TAG = "====UserProfileRepository===="
    private val storeDB = Firebase.firestore
    private val query = storeDB.collection("users")

    fun setProfileData() {}

    fun getProfileData() {}

    fun updateProfileData() {}

    fun deleteProfileData() {}
}