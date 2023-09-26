package ca.mohawkcollege.petcupid.chatmsg

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ca.mohawkcollege.petcupid.databinding.ChatMessageViewBinding
import coil.load

class ChatMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val TAG = "====ChatMessageViewHolder===="
    private val binding = ChatMessageViewBinding.bind(itemView)

    fun bind(chatMessage: ChatMessage, currentUserUid: String) {
        if (chatMessage.senderUid == currentUserUid) {
            binding.userLayout.visibility = View.VISIBLE
            binding.sysLayout.visibility = View.GONE
            binding.messageSend = chatMessage
            if (chatMessage.messageType == MessageType.IMAGE) {
                binding.txvMsgUser.visibility = View.GONE
                binding.imgUser.visibility = View.VISIBLE
                binding.imgUser.load("https://firebasestorage.googleapis.com/v0/b/capstone-test-20e12.appspot.com/o/animal_avatar_3.jpg?alt=media&token=363c636e-58f0-4ad6-9e49-a0645f1b3294")
            }
        } else {
            binding.userLayout.visibility = View.GONE
            binding.sysLayout.visibility = View.VISIBLE
            binding.messageReceive = chatMessage
            if (chatMessage.messageType == MessageType.IMAGE) {
                binding.txvMsgOther.visibility = View.GONE
                binding.imgOther.visibility = View.VISIBLE
                binding.imgOther.load("https://firebasestorage.googleapis.com/v0/b/capstone-test-20e12.appspot.com/o/animal_avatar_3.jpg?alt=media&token=363c636e-58f0-4ad6-9e49-a0645f1b3294")
            }
        }
        binding.executePendingBindings()
    }
}