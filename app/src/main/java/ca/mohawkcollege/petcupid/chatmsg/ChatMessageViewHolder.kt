package ca.mohawkcollege.petcupid.chatmsg

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ca.mohawkcollege.petcupid.databinding.ChatMessageViewBinding

/**
 * The view holder for the chat message.
 * Show or hide the current user's message and the other user's message.
 * Display message content (Including text, image, video, audio, appointment)
 * @param itemView The view
 */
class ChatMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val TAG = "====ChatMessageViewHolder===="
    private val binding = ChatMessageViewBinding.bind(itemView)
    private var isMessageSender = true

    /**
     * Sets whether the current user is the sender
     * @param isMessageSender Whether the current user is the sender
     */
    fun setIsSender(isMessageSender: Boolean) {
        this.isMessageSender = isMessageSender
    }

    /**
     * Binds the chat message to the view holder
     * @param chatMessage The chat message to bind
     */
    fun bind(chatMessage: ChatMessage) {
        binding.layoutVisibility = this.isMessageSender
        binding.isMsgSender = this.isMessageSender
        Log.d(TAG, "bind: ${chatMessage.message}")
        when (chatMessage.message.type) {
            MessageType.TEXT -> {
                binding.messageData = chatMessage
                handleVisibility(binding.txvMsgUser, binding.txvMsgOther, this.isMessageSender)
            }
            MessageType.IMAGE -> {
                binding.imgMessage = chatMessage.message.content as String
                handleVisibility(binding.imgUser, binding.imgOther, this.isMessageSender)
            }
            MessageType.APPOINTMENT -> {
                binding.messageData = chatMessage
                binding.appointmentMessage = Appointment(chatMessage.message.content as HashMap<String, Long>)
                handleVisibility(binding.dateUser, binding.dateOther, this.isMessageSender)
            }
            else -> {}
        }
        binding.executePendingBindings()
    }

    /**
     * Handles the visibility of the views.
     * @param userView The view of the current user
     * @param otherView The view of the other user
     * @param isMessageSender Whether the current user is the sender
     */
    private fun handleVisibility(userView: View, otherView: View, isMessageSender: Boolean) {
        if (isMessageSender) suspendUnusedViews(userView) else suspendUnusedViews(otherView)
    }

    /**
     * Suspends the unused views.
     * @param visibleView The view to be visible
     */
    private fun suspendUnusedViews(visibleView: View) {
        listOf(
            binding.txvMsgUser,
            binding.txvMsgOther,
            binding.imgUser,
            binding.imgOther,
            binding.dateUser,
            binding.dateOther
        ).forEach {
            if (it == visibleView) it.visibility = View.VISIBLE else it.visibility = View.GONE
        }
    }
}
