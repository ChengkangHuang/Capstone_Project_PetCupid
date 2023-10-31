package ca.mohawkcollege.petcupid.searchcontact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactViewModel : ViewModel() {

    private val TAG = "====ContactViewModel===="
    private val contactRepository = ContactRepository()
    private val _dataResult = MutableLiveData<DataResult>()
    val dataResult: LiveData<DataResult>
        get() = _dataResult

    /**
     * Get data by search email
     * @param email
     */
    fun getDataBySearchEmail(email: String) {
        contactRepository.searchEmail(email) { resultCollection, exception ->
            if (resultCollection != null) {
                _dataResult.value = DataResult.Success(resultCollection)
            } else {
                _dataResult.value = DataResult.Error(exception!!)
            }
        }
    }

    /**
     * Get data by search phone number
     * @param phoneNumber
     */
    fun getDataBySearchPhoneNumber(phoneNumber: String) {
        contactRepository.searchPhoneNumber(phoneNumber) { resultCollection, exception ->
            if (resultCollection != null) {
                _dataResult.value = DataResult.Success(resultCollection)
            } else {
                _dataResult.value = DataResult.Error(exception!!)
            }
        }
    }
}

/**
 * DataResult sealed class
 * Success: return data
 * Error: return exception
 */
sealed class DataResult {
    data class Success(val searchContactResult: MutableList<SearchContactItem>?): DataResult()
    data class Error(val exception: Exception): DataResult()
}