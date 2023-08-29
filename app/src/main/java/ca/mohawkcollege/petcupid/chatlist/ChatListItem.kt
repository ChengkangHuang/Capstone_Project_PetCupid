package ca.mohawkcollege.petcupid.chatlist

data class ChatListItem(
    val uid: String,
    var avatarId: Int,
    var name: String,
    var lastMessage: String,
    var lstMessageTime: String)