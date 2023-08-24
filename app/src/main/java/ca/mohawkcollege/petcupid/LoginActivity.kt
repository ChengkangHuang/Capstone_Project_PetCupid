package ca.mohawkcollege.petcupid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import ca.mohawkcollege.petcupid.databinding.ActivityLoginBinding
import ca.mohawkcollege.petcupid.login.LoginResult
import ca.mohawkcollege.petcupid.login.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseUser
import java.lang.Error

class LoginActivity : AppCompatActivity() {

    private val TAG = "====LoginActivity===="
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    /**
     * Setup the result codes
     */
    companion object {
        const val RESULT_CODE_SUCCESS = 1
        const val RESULT_CODE_FAILURE = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.loginViewModel = loginViewModel

        setupLoginResultObserver()
        setupTextInputHandler()
        setupLoginButtonHandler()
        setupSwitchToSignup()
    }

    /**
     * Setup the login result observer
     * This will handle the login result and show a toast message if the login failed
     * If the login succeeded, it will finish the activity
     */
    private fun setupLoginResultObserver() {
        loginViewModel.loginResult.observe(this) { loginResult ->
            when(loginResult) {
                is LoginResult.Success -> {
                    Log.d(TAG, "setupLoginResultObserver: ${loginResult.user}")
                    setResult(RESULT_CODE_SUCCESS, Intent().putExtra("user", loginResult.user))
                    finish()
                }
                is LoginResult.Error -> {
                    Log.d(TAG, "setupLoginResultObserver: ${loginResult.exception}")
                    setResult(RESULT_CODE_FAILURE, Intent().putExtra("exception", loginResult.exception))
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Signup Activity Result Launcher
     * This will handle the result from the signup activity
     * If the result is RESULT_CODE_SUCCESS, it will set the email textview to the email of the user
     * If the result is RESULT_CODE_FAILURE, it will show a Snackbar to the user
     */
    private val mySignupActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == SignupActivity.RESULT_CODE_SUCCESS) {
            val user = result.data?.getParcelableExtra<FirebaseUser>("user")
            Log.d(TAG, "onActivityResult: User -> $user sign up successfully")
            Log.d(TAG, "onActivityResult: Email -> ${user?.email} | UID -> ${user?.uid}")
            binding.emailTextInputEditText.setText(user?.email)
        } else if (result.resultCode == SignupActivity.RESULT_CODE_FAILURE) {
            Log.d(TAG, "onActivityResult: Sign up failed")
            Snackbar.make(binding.root, "Sign up failed", Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Setup the sign up textview handler
     * This will handle the textview click and switch to the sign up activity
     */
    private fun setupSwitchToSignup() {
        val spannableString = SpannableString("Don't have an account? Sign up")
        spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.orange, this.theme)), 23, 30, 0)
        binding.jump2Signup.text = spannableString
        binding.jump2Signup.setOnClickListener { mySignupActivityResultLauncher.launch(Intent(this, SignupActivity::class.java)) }
    }

    /**
     * Setup the text input handler
     * This will check if the email and password are valid
     * If not, show error message
     * If yes, hide error message
     */
    private fun setupTextInputHandler() {
        // For emailTextInputEditText
        binding.emailTextInputEditText.doOnTextChanged { text, _, _, _ ->
            binding.emailTextInputLayout.error = ""
            loginViewModel.onEmailTextChanged(text.toString())
            val isMatch = text.toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex())
            if (!isMatch) {
                binding.emailTextInputLayout.error = "Invalid email address"
            } else {
                binding.emailTextInputLayout.isErrorEnabled = false
            }
        }

        // For passwordTextInputEditText
        binding.passwordTextInputEditText.doOnTextChanged { text, _, _, _ ->
            binding.passwordTextInputLayout.isErrorEnabled = false
            loginViewModel.onPasswordTextChanged(text.toString())
            if (text.toString().length < 6) {
                binding.passwordTextInputLayout.helperText =
                    "Password must be at least 6 characters"
                binding.passwordTextInputLayout.setHelperTextColor(resources.getColorStateList(R.color.red, this.theme))
            } else {
                binding.passwordTextInputLayout.helperText = "Password valid"
                binding.passwordTextInputLayout.setHelperTextColor(resources.getColorStateList(R.color.green, this.theme))
            }
        }
    }

    /**
     * Get the vibrator service
     * @return Vibrator
     */
    private val vibrator: Vibrator
        get() = getSystemService(VIBRATOR_SERVICE) as Vibrator

    /**
     * Call the vibrator to vibrate for 100ms
     * @return Unit
     */
    private fun callVibrator() {
        val vibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    }

    /**
     * Show error message and shake the text input layout
     * @param errorMessage the error message to show
     * @return Unit
     */
    private fun TextInputLayout.showErrorAndShake(errorMessage: String) {
        error = errorMessage
        isErrorEnabled = true
        startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.shake))
    }

    /**
     * Setup the login button handler
     * Check if the email and password are empty
     * If not, call the login function in the view model
     * If yes, show error message and shake the text input layout
     */
    private fun setupLoginButtonHandler() {
        binding.loginBtn.setOnClickListener {
            var hasErrors = false
            val email = loginViewModel.email.value
            val password = loginViewModel.password.value

            if (email.isNullOrEmpty()) {
                binding.emailTextInputLayout.showErrorAndShake("Email can not be empty")
                hasErrors = true
            }

            if (password.isNullOrEmpty()) {
                binding.passwordTextInputLayout.showErrorAndShake("Password can not be empty")
                hasErrors = true
            }

            if (!hasErrors) {
                loginViewModel.onLoginButtonClick()
            } else {
                callVibrator()
            }
        }
    }
}