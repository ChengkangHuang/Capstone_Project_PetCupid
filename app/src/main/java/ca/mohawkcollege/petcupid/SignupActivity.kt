package ca.mohawkcollege.petcupid

import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import ca.mohawkcollege.petcupid.databinding.ActivitySignupBinding
import ca.mohawkcollege.petcupid.signup.SignupResult
import ca.mohawkcollege.petcupid.signup.SignupViewModel
import com.google.android.material.textfield.TextInputLayout

class SignupActivity : AppCompatActivity() {

    private val TAG = "====SignupActivity===="
    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel: SignupViewModel by viewModels()

    companion object {
        const val RESULT_CODE_SUCCESS = 1
        const val RESULT_CODE_FAILURE = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        binding.lifecycleOwner = this
        binding.signupViewModel = signupViewModel

        setupSignupResultObserver()
        setupTextInputHandler()
        setupSignupButtonHandler()
        setupSwitchToLogin()
    }

    /**
     * Setup the signup result observer
     * This will handle the signup result and show a toast message if the signup failed
     * If the signup succeeded, it will finish the activity
     */
    private fun setupSignupResultObserver() {
        signupViewModel.signupResult.observe(this) { signupResult ->
            when(signupResult) {
                is SignupResult.Success -> {
                    Log.d(TAG, "setupSignupResultObserver: ${signupResult.user}")
                    setResult(LoginActivity.RESULT_CODE_SUCCESS, Intent().putExtra("user", signupResult.user))
                    finish()
                }
                is SignupResult.Error -> {
                    Log.d(TAG, "setupSignupResultObserver: ${signupResult.exception}")
                    setResult(LoginActivity.RESULT_CODE_FAILURE, Intent().putExtra("exception", signupResult.exception))
                    Toast.makeText(this, "Sign up failed: ${signupResult.exception}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Setup the sign in textview handler
     * This will handle the textview click and switch back to the login activity
     */
    private fun setupSwitchToLogin() {
        val spannableString = SpannableString("Already have an account? Sign In")
        spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.orange, this.theme)), 25, 32, 0)
        binding.back2Login.text = spannableString
        binding.back2Login.setOnClickListener { finish() }
    }

    /**
     * Setup the text input handler
     * This will handle the text input and validate it
     * If the text is invalid, it will show an error message
     * If the text is valid, it will hide the error message
     */
    private fun setupTextInputHandler() {
        // For userNameTextInputEditText
        binding.userNameTextInputEditText.doOnTextChanged { text, _, _, _ ->
            binding.userNameTextInputLayout.isErrorEnabled = false
            signupViewModel.onUserNameTextChanged(text.toString())
        }

        // For emailTextInputEditText
        binding.emailTextInputEditText.doOnTextChanged { text, _, _, _ ->
            signupViewModel.onEmailTextChanged(text.toString())
            val isMatch = text.toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex())
            if (!isMatch) {
                binding.emailTextInputLayout.error = "Invalid email address"
            } else {
                binding.emailTextInputLayout.isErrorEnabled = false
            }
        }

        // For phoneNumberTextInputEditText
        binding.phoneNumberTextInputEditText.doOnTextChanged { text, _, _, _ ->
            binding.phoneNumberTextInputLayout.isErrorEnabled = false
            signupViewModel.onPhoneNumberTextChanged(text.toString())
        }

        // For passwordTextInputEditText
        binding.passwordTextInputEditText.doOnTextChanged { text, _, _, _ ->
            binding.passwordTextInputLayout.isErrorEnabled = false
            signupViewModel.onPasswordTextChanged(text.toString())
            if (text.toString().length < 6) {
                binding.passwordTextInputLayout.helperText =
                    "Password must be at least 6 characters"
                binding.passwordTextInputLayout.setHelperTextColor(resources.getColorStateList(R.color.red, this.theme))
            } else {
                binding.passwordTextInputLayout.helperText = "Password valid"
                binding.passwordTextInputLayout.setHelperTextColor(resources.getColorStateList(R.color.green, this.theme))
            }
        }

        // For confirmPasswordTextInputEditText
        binding.confirmPasswordTextInputEditText.doOnTextChanged { text, _, _, _ ->
            binding.confirmPasswordTextInputLayout.isErrorEnabled = false
            signupViewModel.onConfirmPasswordTextChanged(text.toString())
            if (text.toString() != binding.passwordTextInputEditText.text.toString()) {
                binding.confirmPasswordTextInputLayout.helperText = "Password does not match"
                binding.confirmPasswordTextInputLayout.setHelperTextColor(resources.getColorStateList(R.color.red, this.theme))
            } else {
                binding.confirmPasswordTextInputLayout.helperText = "Password valid"
                binding.confirmPasswordTextInputLayout.setHelperTextColor(resources.getColorStateList(R.color.green, this.theme))
//                TODO("Need to use the green check-icon")
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
        isErrorEnabled = true
        error = errorMessage
        startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.shake))
    }

    /**
     * Setup the signup button handler
     * Check if the user name, email, phone number, password and confirm password are valid
     * If not, show error message and shake the text input layout
     * If valid, call the signup function in the view model
     * If the signup is successful, finish the activity
     */
    private fun setupSignupButtonHandler() {
        binding.signupBtn.setOnClickListener {
            var hasErrors = false
            val userName = signupViewModel.userName.value
            val email = signupViewModel.email.value
            val phoneNumber = signupViewModel.phoneNumber.value
            val password = signupViewModel.password.value
            val confirmPassword = signupViewModel.confirmPassword.value

            if (userName.isNullOrEmpty()) {
                binding.userNameTextInputLayout.showErrorAndShake("User name cannot be empty")
                hasErrors = true
            }

            if (email.isNullOrEmpty()) {
                binding.emailTextInputLayout.showErrorAndShake("Email cannot be empty")
                hasErrors = true
            }

            if (phoneNumber.isNullOrEmpty()) {
                binding.phoneNumberTextInputLayout.showErrorAndShake("Phone number cannot be empty")
                hasErrors = true
            }

            if (password.isNullOrEmpty()) {
                binding.passwordTextInputLayout.showErrorAndShake("Password cannot be empty")
                hasErrors = true
            }

            if (confirmPassword.isNullOrEmpty()) {
                binding.confirmPasswordTextInputLayout.showErrorAndShake("Confirm password cannot be empty")
                hasErrors = true
            }

            if (!hasErrors) {
                signupViewModel.onSignupButtonClick()
            } else {
                callVibrator()
            }
        }
    }
}