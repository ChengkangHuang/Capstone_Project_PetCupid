package ca.mohawkcollege.petcupid.chatlist

import android.os.Parcel
import android.os.Parcelable

data class ChatListItem(
    val receiverUid: String,
    var avatarId: Int,
    var name: String,
    var lastMessage: String,
    var lstMessageTime: String,): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(receiverUid)
        parcel.writeInt(avatarId)
        parcel.writeString(name)
        parcel.writeString(lastMessage)
        parcel.writeString(lstMessageTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatListItem> {
        override fun createFromParcel(parcel: Parcel): ChatListItem {
            return ChatListItem(parcel)
        }

        override fun newArray(size: Int): Array<ChatListItem?> {
            return arrayOfNulls(size)
        }
    }
}