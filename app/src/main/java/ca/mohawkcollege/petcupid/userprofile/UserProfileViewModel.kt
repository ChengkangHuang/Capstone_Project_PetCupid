package ca.mohawkcollege.petcupid.userprofile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserProfileViewModel : ViewModel() {

    private val TAG = "====UserProfileViewModel===="
    private val userProfileRepository = UserProfileRepository()
    val user = MutableLiveData<User>()
    private val _userProfileResult = MutableLiveData<UserProfileResult>()
    val userProfileResult: MutableLiveData<UserProfileResult> = _userProfileResult
    var isDataChanged = MutableLiveData<Boolean>()

    fun getProfileData(uid: String) {
        userProfileRepository.getProfileDataByUid(uid) { user, exception ->
            if (user != null) {
                this.user.value = user
            } else {
                _userProfileResult.value = UserProfileResult.Error(exception!!)
            }
        }
    }

    fun onUserNameChanged(userName: CharSequence) {
        this.user.value?.username ?: userName.toString()
        this.isDataChanged.value = true
    }

    fun onPhoneNumberChanged(phoneNumber: String) {
        this.user.value?.phone ?: phoneNumber
        this.isDataChanged.value = true
    }

    fun onEmailChanged(email: String) {
        this.user.value?.email ?: email
        this.isDataChanged.value = true
    }

    fun onSaveButtonClicked() {
        userProfileRepository.updateProfileData(user.value!!) { user, exception ->
            if (user != null) {
                _userProfileResult.value = UserProfileResult.Success(user)
            } else {
                _userProfileResult.value = UserProfileResult.Error(exception!!)
            }
        }
    }
}

sealed class UserProfileResult {
    data class Success(val user: User): UserProfileResult()
    data class Error(val exception: Exception): UserProfileResult()
}