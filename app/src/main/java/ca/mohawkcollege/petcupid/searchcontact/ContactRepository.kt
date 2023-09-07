package ca.mohawkcollege.petcupid.searchcontact

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ContactRepository {

    private val TAG = "====ContactRepository===="
    private val storeDB = Firebase.firestore
    private val query = storeDB.collection("users")
    private val resultCollection = mutableListOf<SearchContactItem>()

    fun searchEmail(email: String, callback: (MutableList<SearchContactItem>?, Exception?) -> Unit) {
        searchData("email", email, callback)
    }

    fun searchPhoneNumber(phoneNumber: String, callback: (MutableList<SearchContactItem>?, Exception?) -> Unit) {
        searchData("phone", phoneNumber, callback)
    }

    private fun searchData(field: String, value: String, callback: (MutableList<SearchContactItem>?, Exception?) -> Unit) {
        query.whereEqualTo(field, value)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "getData: ${document.id} => ${document.data}")
                    val data = document.toObject(SearchContactItem::class.java)
                    resultCollection.add(data)
                }
                callback(resultCollection, null)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "getData: ", exception)
                callback(null, exception)
            }
    }
}