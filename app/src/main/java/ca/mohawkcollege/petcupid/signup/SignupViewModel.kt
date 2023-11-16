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

    /**
     * Username text changed
     * @param userName
     */
    fun onUserNameTextChanged(userName: CharSequence) {
        this.userName.value = userName.toString()
    }

    /**
     * Phone number text changed
     * @param phoneNumber
     */
    fun onPhoneNumberTextChanged(phoneNumber: String) {
        this.phoneNumber.value = phoneNumber
    }

    /**
     * Email text changed
     * @param email
     */
    fun onEmailTextChanged(email: String) {
        this.email.value = email
    }

    /**
     * Password text changed
     * @param password
     */
    fun onPasswordTextChanged(password: String) {
        this.password.value = password
    }

    /**
     * Confirm password text changed
     * @param confirmPassword
     */
    fun onConfirmPasswordTextChanged(confirmPassword: String) {
        this.confirmPassword.value = confirmPassword
    }

    /**
     * Signup button click
     * Get the device token from SignupRepository
     * Call signup function from SignupRepository
     */
    fun onSignupButtonClick() {
        val userName = userName.value!!
        val email = email.value!!
        val phoneNumber = phoneNumber.value!!
        val password = password.value!!
        signupRepository.signup(email, password) { user, exception ->
            if (user != null) {
                _signupResult.value = SignupResult.Success(user)
                signupRepository.getToken { tokenResult, exception ->
                    if (tokenResult != null) {
                        signupRepository.saveToDatabase(
                            user.uid,
                            userName,
                            phoneNumber,
                            email,
                            tokenResult
                        )
                    } else {
                        Log.e(TAG, "onSignupButtonClick: failed to get token", exception!!)
                    }
                }
                Log.d(TAG, "onSignupButtonClick: current user id -> ${user.uid}")
            } else {
                _signupResult.value = SignupResult.Error(exception!!)
            }
        }
    }
}

/**
 * SignupResult sealed class
 * Success: return user
 * Error: return exception
 */
sealed class SignupResult {
    data class Success(val user: FirebaseUser?): SignupResult()
    data class Error(val exception: Exception): SignupResult()
}

/**
 * TokenResult sealed class
 * Success: return token
 * Error: return exception
 */
sealed class TokenResult {
    data class Success(val token: String): TokenResult()
    data class Error(val exception: Exception): TokenResult()
}