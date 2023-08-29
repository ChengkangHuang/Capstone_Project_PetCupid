package ca.mohawkcollege.petcupid

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ca.mohawkcollege.petcupid.databinding.ActivityMainBinding
import ca.mohawkcollege.petcupid.login.LoginResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    private val TAG = "====MainActivity===="
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        initFirebaseAuth()
    }

    private fun initFirebaseAuth() {
        if (mAuth.currentUser == null) {
            Log.d(TAG, "initFirebaseAuth: No user login")
            myFirebaseAuthActivityResultLauncher.launch(Intent(this, LoginActivity::class.java))
        } else {
            currentUser = mAuth.currentUser!!
            Log.d(TAG, "initFirebaseAuth: User -> $currentUser login")
        }
    }

    /**
     * Register an activity result launcher for login activity
     */
    private val myFirebaseAuthActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LoginActivity.RESULT_CODE_SUCCESS) {
            val user = result.data?.getParcelableExtra<FirebaseUser>("user")
            currentUser = user!!
            Log.d(TAG, "onActivityResult: User -> $currentUser login")
        } else if (result.resultCode == LoginActivity.RESULT_CODE_FAILURE) {
            Log.d(TAG, "onActivityResult: Login failed")
            Snackbar.make(binding.root, "Login failed", Snackbar.LENGTH_SHORT).show()
        }
    }
}