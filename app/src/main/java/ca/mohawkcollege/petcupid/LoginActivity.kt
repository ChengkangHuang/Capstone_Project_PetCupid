package ca.mohawkcollege.petcupid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import ca.mohawkcollege.petcupid.databinding.ActivityLoginBinding
import ca.mohawkcollege.petcupid.login.LoginResult
import ca.mohawkcollege.petcupid.login.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private val TAG = "====LoginActivity===="
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    companion object {
        const val RESULT_CODE_SUCCESS = 1
        const val RESULT_CODE_FAILURE = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.loginViewModel = loginViewModel

//        setupLoginResultObserver()
        setupTextInputHandler()
        setupSwitchToSignup()
    }

    override fun onStart() {
        super.onStart()
        setupLoginResultObserver()
    }

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

    private val mySignupActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 1) {
            val user = result.data?.getParcelableExtra<FirebaseUser>("user")
            Log.d(TAG, "onActivityResult: User -> $user login")
        } else if (result.resultCode == -1) {
            Log.d(TAG, "onActivityResult: Login failed")
            Snackbar.make(binding.root, "Login failed", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupSwitchToSignup() {
        val spannableString = SpannableString("Don't have an account? Sign up")
        spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.orange, this.theme)), 23, 30, 0)
        binding.jump2Signup.text = spannableString
        binding.jump2Signup.setOnClickListener { mySignupActivityResultLauncher.launch(Intent(this, SignupActivity::class.java)) }
    }

    private fun setupTextInputHandler() {
        binding.emailTextInputEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                loginViewModel.onEmailTextChanged(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input: String = s.toString()
                val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                if (!input.matches(emailPattern.toRegex())) {
                    binding.emailTextInputLayout.helperText = "Invalid email address"
                    binding.emailTextInputLayout.setHelperTextColor(resources.getColorStateList(R.color.red, null))
                } else {
                    binding.emailTextInputLayout.helperText = ""
                }
            }
        })

        binding.passwordTextInputEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                loginViewModel.onPasswordTextChanged(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input: String = s.toString()
                if (input.length < 8) {
                    binding.passwordTextInputLayout.helperText = "Password must be at least 8 characters"
                    binding.passwordTextInputLayout.setHelperTextColor(resources.getColorStateList(R.color.red, null))
                } else {
                    binding.passwordTextInputLayout.helperText = ""
                }
            }
        })
    }
}