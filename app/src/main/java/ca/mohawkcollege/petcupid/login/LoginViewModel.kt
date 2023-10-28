package ca.mohawkcollege.petcupid.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class LoginViewModel : ViewModel() {

    private val TAG = "====UserViewModel===="
    private val loginRepository = LoginRepository()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    /**
     * Called when the email text changes.
     */
    fun onEmailTextChanged(email: String) {
        this.email.value = email
    }

    /**
     * Called when the password text changes.
     */
    fun onPasswordTextChanged(password: String) {
        this.password.value = password
    }

    /**
     * Called when the login button is clicked.
     */
    fun onLoginButtonClick() {
        val email = this.email.value!!
        val password = password.value!!
        Log.d(TAG, "onLoginButtonClick: $email | $password")
        loginRepository.login(email, password) { user, exception ->
            if (user != null) {
                _loginResult.value = LoginResult.Success(user)
            } else {
                _loginResult.value = LoginResult.Error(exception!!)
            }
        }
    }
}

/**
 * A sealed class that represents the result of a login attempt.
 */
sealed class LoginResult {

    /**
     * A data class that represents a successful login attempt.
     * @param user The FirebaseUser object.
     */
    data class Success(val user: FirebaseUser?): LoginResult()

    /**
     * A data class that represents a failed login attempt.
     * @param exception The exception that was thrown.
     */
    data class Error(val exception: Exception): LoginResult()
}