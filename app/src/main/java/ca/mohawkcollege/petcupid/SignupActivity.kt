package ca.mohawkcollege.petcupid

import android.content.Context
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        binding.lifecycleOwner = this
        binding.signupViewModel = signupViewModel

        setupSignupResultObserver()
        setupTextInputHandler()
        setupSignupButtonHandler()
        setupSwitchToSignup()
    }

    private fun setupSignupResultObserver() {
        signupViewModel.signupResult.observe(this) { signupResult ->
            when(signupResult) {
                is SignupResult.Success -> {
                    Log.d(TAG, "setupSignupResultObserver: ${signupResult.user}")
                    finish()
                }
                is SignupResult.Error -> {
                    Log.d(TAG, "setupSignupResultObserver: ${signupResult.exception}")
                    Toast.makeText(this, "Sign up failed: ${signupResult.exception}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupSwitchToSignup() {
        val spannableString = SpannableString("Already have an account? Sign In")
        spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.orange, this.theme)), 25, 32, 0)
        binding.back2Login.text = spannableString
//        binding.jump2Signup.setOnClickListener { startActivity(Intent(this, SignupActivity::class.java)) }
    }

    private fun setupTextInputHandler() {
        binding.userNameTextInputEditText.doOnTextChanged { text, _, _, _ ->
            binding.userNameTextInputLayout.error = ""
            signupViewModel.onUserNameTextChanged(text.toString())
        }

        binding.emailTextInputEditText.doOnTextChanged { text, _, _, _ ->
            binding.emailTextInputLayout.error = ""
            signupViewModel.onEmailTextChanged(text.toString())
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            binding.emailTextInputLayout.helperText = ""
            if (!text.toString().matches(emailPattern.toRegex())) {
                binding.emailTextInputLayout.helperText = "Invalid email address"
                binding.emailTextInputLayout.setHelperTextColor(
                    resources.getColorStateList(R.color.red, null))
            }
        }

        binding.phoneNumberTextInputEditText.doOnTextChanged { text, _, _, _ ->
            binding.phoneNumberTextInputLayout.error = ""
            signupViewModel.onPhoneNumberTextChanged(text.toString())
        }

        binding.passwordTextInputEditText.doOnTextChanged { text, _, _, _ ->
            binding.passwordTextInputLayout.error = ""
            signupViewModel.onPasswordTextChanged(text.toString())
            binding.passwordTextInputLayout.helperText = ""
            if (text.toString().length < 8) {
                binding.passwordTextInputLayout.helperText =
                    "Password must be at least 8 characters"
                binding.passwordTextInputLayout.setHelperTextColor(
                    resources.getColorStateList(R.color.red, null))
            }
        }

        binding.confirmPasswordTextInputEditText.doOnTextChanged { text, _, _, _ ->
            binding.confirmPasswordTextInputLayout.error = ""
            signupViewModel.onConfirmPasswordTextChanged(text.toString())
            if (text.toString() != binding.passwordTextInputEditText.text.toString()) {
                binding.confirmPasswordTextInputLayout.helperText = "Password does not match"
                binding.confirmPasswordTextInputLayout.setHelperTextColor(
                    resources.getColorStateList(R.color.red, null))
            } else {
                binding.confirmPasswordTextInputLayout.helperText = "Password valid"
                binding.confirmPasswordTextInputLayout.setHelperTextColor(
                    resources.getColorStateList(R.color.green, null))
            }
        }
    }

    private val Context.vibrator: Vibrator
        get() = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    private fun callVibrator() {
        val vibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    }

    private fun TextInputLayout.showErrorAndShake(errorMessage: String) {
        error = errorMessage
        startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
    }

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