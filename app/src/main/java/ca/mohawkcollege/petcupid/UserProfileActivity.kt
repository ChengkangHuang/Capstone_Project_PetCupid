package ca.mohawkcollege.petcupid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import ca.mohawkcollege.petcupid.databinding.ActivityUserProfileBinding
import ca.mohawkcollege.petcupid.userprofile.User
import ca.mohawkcollege.petcupid.userprofile.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth

class UserProfileActivity : AppCompatActivity() {

    private val TAG = "====UserProfileActivity===="
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityUserProfileBinding
    private val userProfileViewModel: UserProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile)
        binding.lifecycleOwner = this
        binding.viewModel = userProfileViewModel

        getUserProfileData()
//        setupUserProfileDataObserver()
    }

    private fun getUserProfileData() {
        mAuth = FirebaseAuth.getInstance()
        userProfileViewModel.getProfileData(mAuth.uid!!)
    }

    private fun setupUserProfileDataObserver() {
//        userProfileViewModel.user.observe(this) { userProfileData ->
//            Log.d(TAG, "userProfileDataObserver: $userProfileData")
//        }
    }
}