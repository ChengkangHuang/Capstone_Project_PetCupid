package ca.mohawkcollege.petcupid

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import ca.mohawkcollege.petcupid.databinding.FragmentUpperBehaviorBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.Date

/**
 * An UpperBehaviorFragment subclass.
 */
class UpperBehaviorFragment : Fragment() {

    private val TAG = "====UpperBehaviorFragment===="
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mediaUri: Uri

    private var _binding: FragmentUpperBehaviorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpperBehaviorBinding.inflate(inflater, container, false)
//        initScreenOnClickHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScreenOnClickHandler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leak
    }

    /**
     * Initialize profile image button and camera image button
     * @return Unit
     */
    private fun initScreenOnClickHandler() {
        binding.profileImgBtn.setOnClickListener {
            // This behavior just for testing
            if (mAuth.currentUser != null) mAuth.signOut()
//            initFirebaseAuth()
        }
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
        mediaUri = if (isPicture) activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        else activity?.contentResolver?.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)!!
        Log.d(TAG, "saveMedia: uri: $mediaUri")
        mediaUri.let {
            if (isPicture) myPictureActivityResultLauncher.launch(it)
            else myVideoActivityResultLauncher.launch(it)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment UpperBehaviorFragment.
         */
        @JvmStatic
        fun newInstance() =
            UpperBehaviorFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}