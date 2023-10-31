package ca.mohawkcollege.petcupid.userprofile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserProfileViewModel : ViewModel() {

    private val TAG = "====UserProfileViewModel===="
    private val userProfileRepository = UserProfileRepository()
    val user = MutableLiveData<User>()

}