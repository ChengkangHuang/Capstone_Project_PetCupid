package ca.mohawkcollege.petcupid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import ca.mohawkcollege.petcupid.databinding.ActivityUserProfileBinding
import ca.mohawkcollege.petcupid.userprofile.UserProfileViewModel

class UserProfileActivity : AppCompatActivity() {

    private val TAG = "====UserProfileActivity===="
    private lateinit var binding: ActivityUserProfileBinding
    private val userProfileViewModel: UserProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile)
        binding.lifecycleOwner = this
        binding.viewModel = userProfileViewModel
    }
}