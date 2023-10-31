package ca.mohawkcollege.petcupid.searchcontact

import java.io.Serializable

data class SearchContactItem(
    val uid: String,
    val phone: String,
    val email: String,
    val username: String,
    val icon: String?
) : Serializable {
    constructor() : this("", "", "", "", "")
}