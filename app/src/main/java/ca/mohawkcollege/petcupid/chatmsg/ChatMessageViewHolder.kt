package ca.mohawkcollege.petcupid.chatmsg

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ca.mohawkcollege.petcupid.databinding.ChatMessageViewBinding

class ChatMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val TAG = "====ChatMessageViewHolder===="
    private val binding = ChatMessageViewBinding.bind(itemView)

    fun bind(chatMessage: ChatMessage, currentUserUid: String) {
        if (chatMessage.senderUid == currentUserUid) {
            binding.userLayout.visibility = View.VISIBLE
            binding.sysLayout.visibility = View.GONE
            binding.messageSend = chatMessage
        } else {
            binding.userLayout.visibility = View.GONE
            binding.sysLayout.visibility = View.VISIBLE
            binding.messageReceive = chatMessage
        }
        binding.executePendingBindings()
    }
}