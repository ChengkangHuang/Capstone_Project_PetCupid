package ca.mohawkcollege.petcupid.chatlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ca.mohawkcollege.petcupid.R

class ChatListViewModel : ViewModel() {

    private val TAG = "====ChatListViewModel===="
    private val _chatListItems = MutableLiveData<List<ChatListItem>>()
    val chatListItems: LiveData<List<ChatListItem>> = _chatListItems

    init {
        val items = listOf(
            ChatListItem("SSkvJs8CvdTtvO0qdEOccj3Dq742", R.drawable.lucy, "Lucy", "I'm great", "12:06 PM"),
            ChatListItem("sPmNBVcpoEVFoHVGKdYKY5DfAnC3", R.drawable.bella, "Bella", "Hello", "12:01 AM"),
            ChatListItem("nlls2yoqAhdg223dg8SJe528iWC3", R.drawable.daisy, "Daisy", "I'm fine", "12:04 PM"),
            ChatListItem("IF0v2Xt2L1Owsqi32TpOgNJ3ydl2", R.drawable.bailey, "Baily", "Hi", "12:00 AM"),
            ChatListItem("IOa02UDmeKQxa6YOCPaiNp92s5l2", R.drawable.coco, "Coco", "Hey", "12:02 AM"),
            ChatListItem("XtqSBDTj2NTRxMSaTena04leHSf2", R.drawable.charlie, "Charlie", "How are you?", "12:03 AM"),
            ChatListItem("89QckuS9XlgQlrGOkq8DkOiAq8i2", R.drawable.ginger, "Ginger", "I'm good", "12:05 AM"),
            ChatListItem("SZfBNO6gYiV7Gqrpx7m5acSMNOB2", R.drawable.kitty, "Kitty", "I'm great", "12:06 AM"),
            ChatListItem("Opjq4hCCIlWhtPvHSDI0D1jG5hu2", R.drawable.leo, "Leo", "Hello", "12:01 AM"),
            ChatListItem("", R.drawable.luna, "Luna", "I'm fine", "12:04 AM"),
            ChatListItem("", R.drawable.molly, "Molly", "Hi", "12:00 AM"),
            ChatListItem("", R.drawable.max, "Max", "Hey", "12:02 AM"),
            ChatListItem("", R.drawable.rocky, "Rocky", "How are you?", "12:03 AM"),
            ChatListItem("", R.drawable.oliver, "Oliver", "I'm good", "12:05 AM"),
            ChatListItem("", R.drawable.pepper, "Pepper", "I'm great", "12:06 AM"),
            ChatListItem("", R.drawable.sadie, "Sadie", "Hello", "12:01 AM"),
            ChatListItem("", R.drawable.shadow, "Shadow", "I'm fine", "12:04 AM"),
            ChatListItem("", R.drawable.toby, "Toby", "Hi", "12:00 AM"),
            ChatListItem("", R.drawable.thor, "Thor", "Hey", "12:02 AM"),
            ChatListItem("", R.drawable.zeus, "Zeus", "How are you?", "12:03 AM"),
        )
        _chatListItems.value = items
    }
}