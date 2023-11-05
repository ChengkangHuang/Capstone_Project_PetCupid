package ca.mohawkcollege.petcupid.userprofile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * User profile view model
 */
class UserProfileViewModel : ViewModel() {

    private val TAG = "====UserProfileViewModel===="
    private val userProfileRepository = UserProfileRepository()
    private var currentUser: User? = null
    val newUser = MutableLiveData<User>()
    private val _userProfileResult = MutableLiveData<UserProfileResult>()
    val userProfileResult: MutableLiveData<UserProfileResult> = _userProfileResult

    /**
     * Get user profile data by uid
     * @param uid user id
     */
    fun getProfileData(uid: String) {
        userProfileRepository.getProfileDataByUid(uid) { user, exception ->
            if (user != null) {
                this.currentUser = user
                this.newUser.value = user.copy()
                _userProfileResult.value = UserProfileResult.Success(user)
            } else {
                _userProfileResult.value = UserProfileResult.Error(exception!!)
            }
        }
    }

    /**
     * Update username when username changed
     * @param userName new username
     */
    fun onUserNameChanged(userName: CharSequence) {
        this.newUser.value?.username = userName.toString()
        Log.d(TAG, "onUserNameChanged: ${this.newUser.value?.username} | $userName")
    }

    /**
     * Update phone number when phone number changed
     * @param phoneNumber new phone number
     */
    fun onPhoneNumberChanged(phoneNumber: CharSequence) {
        this.newUser.value?.phone = phoneNumber.toString()
        Log.d(TAG, "onPhoneNumberChanged: ${this.newUser.value?.phone} | $phoneNumber")
    }

    /**
     * Update email when email changed
     * @param email new email
     */
    fun onEmailChanged(email: CharSequence) {
        this.newUser.value?.email = email.toString()
        Log.d(TAG, "onEmailChanged: ${this.newUser.value?.email} | $email")
    }

    /**
     * Save button clicked
     * @param user user data
     */
    fun onSaveButtonClicked(user: User) {
        Log.d(TAG, "onSaveButtonClicked: $user")
        when {
            user.username != currentUser?.username -> {
                updateField(user.uid, "username", user.username)
            }
            user.phone != currentUser?.phone -> {
                updateField(user.uid, "phone", user.phone)
            }
            user.email != currentUser?.email -> {
                updateField(user.uid, "email", user.email)
            }
        }
        this.currentUser = user.copy()
    }

    /**
     * Update field in database
     * @param uid user id
     * @param field field to update
     * @param newValue new value
     */
    private fun updateField(uid: String, field: String, newValue: String) {
        userProfileRepository.updateProfileData(uid, field, newValue)
    }
}

/**
 * User profile result
 */
sealed class UserProfileResult {
    data class Success(val user: User): UserProfileResult()
    data class Error(val exception: Exception): UserProfileResult()
}