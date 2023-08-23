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
    private lateinit var mediaUri: Uri
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main) // Inflate the layout
        binding.lifecycleOwner = this

        mAuth = FirebaseAuth.getInstance()
        initScreenOnClickHandler()
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

    private fun initScreenOnClickHandler() {
        binding.profileImgBtn.setOnClickListener { if (mAuth.currentUser != null) mAuth.signOut() }
        binding.camImgBtn.setOnClickListener { captureScenes(true) }
        binding.camImgBtn.setOnLongClickListener { captureScenes(false); true }
    }

    /**
     * Register an activity result launcher for taking pictures
     */
    private val myPictureActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            Log.d(TAG, "onActivityResult: Picture capture SUCCESS")
            Snackbar.make(binding.root, "Picture saved", Snackbar.LENGTH_SHORT).show()
        } else {
            Log.d(TAG, "onActivityResult: Picture capture CANCEL")
            Snackbar.make(binding.root, "Picture capture cancelled", Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Register an activity result launcher for taking videos
     */
    private val myVideoActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success) {
            Log.d(TAG, "onActivityResult: Video capture SUCCESS")
            Snackbar.make(binding.root, "Video saved", Snackbar.LENGTH_SHORT).show()
        } else {
            Log.d(TAG, "onActivityResult: Video capture CANCEL")
            Snackbar.make(binding.root, "Video capture cancelled", Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Capture scenes
     * File name format: photo/video + _yyyyMMdd_HHmmss_ + .jpg/mp4
     * @param isPicture: Boolean
     * @return Unit
     */
    @SuppressLint("SimpleDateFormat")
    private fun captureScenes(isPicture: Boolean) {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val values = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                if (isPicture) "photo_${timestamp}_.jpg" else "video_${timestamp}_.mp4")
            put(
                MediaStore.Images.Media.MIME_TYPE,
                if (isPicture) "image/jpeg" else "video/mp4")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                if (isPicture) Environment.DIRECTORY_PICTURES else Environment.DIRECTORY_MOVIES)
        }
        mediaUri = if (isPicture) contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        else contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)!!
        Log.d(TAG, "saveMedia: uri: $mediaUri")
        mediaUri.let {
            if (isPicture) myPictureActivityResultLauncher.launch(it)
            else myVideoActivityResultLauncher.launch(it)
        }
    }
}