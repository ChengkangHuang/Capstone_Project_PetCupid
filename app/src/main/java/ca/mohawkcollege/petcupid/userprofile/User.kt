package ca.mohawkcollege.petcupid.userprofile

import java.io.Serializable

data class User(
    var uid: String,
    var phone: String,
    var email: String,
    var username: String,
    var icon: String?
) : Serializable {
    constructor() : this("", "", "", "", "")
}
