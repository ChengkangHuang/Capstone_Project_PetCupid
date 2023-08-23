package ca.mohawkcollege.petcupid.login

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginRepository {
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