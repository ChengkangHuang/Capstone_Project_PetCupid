package ca.mohawkcollege.petcupid.login

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginRepository {

    private val TAG = "====LoginRepository===="

    /**
     * Login to Firebase with email and password.
     * @param email The email to login with.
     * @param password The password to login with.
     * @param callback The callback to return the result to.
     */
    fun login(email: String, password: String, callback: (FirebaseUser?, Exception?) -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(task.result?.user, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }
}