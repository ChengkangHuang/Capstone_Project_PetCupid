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

    fun onEmailTextChanged(email: String) {
        this.email.value = email
    }

    fun onPasswordTextChanged(password: String) {
        this.password.value = password
    }

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

sealed class LoginResult {
    data class Success(val user: FirebaseUser?): LoginResult()
    data class Error(val exception: Exception): LoginResult()
}