package ca.mohawkcollege.petcupid.userprofile

import java.io.Serializable

data class User(
    val uid: String,
    val phone: String,
    val email: String,
    var username: String,
    val icon: String?
) : Serializable {
    constructor() : this("", "", "", "", "")
}
