package ca.mohawkcollege.petcupid.searchcontact

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Contact repository
 * @property TAG log tag
 * @property storeDB firestore database
 * @property query firestore query
 * @property resultCollection search result collection
 */
class ContactRepository {

    private val TAG = "====ContactRepository===="
    private val storeDB = Firebase.firestore
    private val query = storeDB.collection("users")
    private val resultCollection = mutableListOf<SearchContactItem>()

    /**
     * Search contact by email
     * @param email search email
     * @param callback return resultCollection or exception
     */
    fun searchEmail(email: String, callback: (MutableList<SearchContactItem>?, Exception?) -> Unit) {
        searchData("email", email, callback)
    }

    /**
     * Search contact by phone number
     * @param phoneNumber search phone number
     * @param callback return resultCollection or exception
     */
    fun searchPhoneNumber(phoneNumber: String, callback: (MutableList<SearchContactItem>?, Exception?) -> Unit) {
        searchData("phone", phoneNumber, callback)
    }

    /**
     * Search contact by field and value
     * @param field search field
     * @param value search value
     * @param callback return resultCollection or exception
     */
    private fun searchData(field: String, value: String, callback: (MutableList<SearchContactItem>?, Exception?) -> Unit) {
        query.whereEqualTo(field, value)
            .get()
            .addOnSuccessListener { result ->
                resultCollection.clear()
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