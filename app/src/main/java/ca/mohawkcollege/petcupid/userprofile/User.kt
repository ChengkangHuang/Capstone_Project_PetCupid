package ca.mohawkcollege.petcupid.userprofile

import java.io.Serializable

data class User(
    val uid: String,
    var phone: String,
    var email: String,
    var username: String,
    val icon: String?
) : Serializable {
    constructor() : this("", "", "", "", "")
}
