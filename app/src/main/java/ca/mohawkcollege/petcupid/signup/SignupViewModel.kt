package ca.mohawkcollege.petcupid.signup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class SignupViewModel : ViewModel() {

    private val TAG = "====SignupViewModel===="
    private val signupRepository = SignupRepository()
    val userName = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()
    private val _signupResult = MutableLiveData<SignupResult>()
    val signupResult: MutableLiveData<SignupResult> = _signupResult

    fun onUserNameTextChanged(userName: CharSequence) {
        this.userName.value = userName.toString()
    }

    fun onPhoneNumberTextChanged(phoneNumber: String) {
        this.phoneNumber.value = phoneNumber
    }

    fun onEmailTextChanged(email: String) {
        this.email.value = email
    }

    fun onPasswordTextChanged(password: String) {
        this.password.value = password
    }

    fun onConfirmPasswordTextChanged(confirmPassword: String) {
        this.confirmPassword.value = confirmPassword
    }

    fun onSignupButtonClick() {
        val userName = userName.value!!
        val email = email.value!!
        val phoneNumber = phoneNumber.value!!
        val password = password.value!!
        signupRepository.signup(email, password) { user, exception ->
            if (user != null) {
                _signupResult.value = SignupResult.Success(user)
                signupRepository.saveToDatabase(user.uid, userName, phoneNumber, email)
                Log.d(TAG, "onSignupButtonClick: current user id -> ${user.uid}")
            } else {
                _signupResult.value = SignupResult.Error(exception!!)
            }
        }
    }
}

sealed class SignupResult {
    data class Success(val user: FirebaseUser?): SignupResult()
    data class Error(val exception: Exception): SignupResult()
}