package ca.mohawkcollege.petcupid.chatmsg

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ca.mohawkcollege.petcupid.ChatActivity
import ca.mohawkcollege.petcupid.databinding.ChatMessageViewBinding
import ca.mohawkcollege.petcupid.tools.AudioUtils
import ca.mohawkcollege.petcupid.tools.ChatUtils
import com.google.android.material.snackbar.Snackbar

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
    private val audioUtils = AudioUtils()
    private var isAudioPlaying = false

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
    fun bind(context: Context, chatMessage: ChatMessage) {
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
            MessageType.AUDIO -> {
                binding.audioMessage = chatMessage.message.content as String
                handleVisibility(binding.voiceUser, binding.voiceOther, this.isMessageSender)
                handleAudioPlayback(context, chatMessage, this.isMessageSender)
            }
            MessageType.APPOINTMENT -> {
                binding.messageData = chatMessage
                val appointment = Appointment(chatMessage.message.content as HashMap<String, Long>)
                binding.appointmentMessage = appointment
                handleVisibility(binding.dateUser, binding.dateOther, this.isMessageSender)
                handleAppointmentConfirmation(context, chatMessage, appointment)
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
            binding.voiceUser,
            binding.voiceOther,
            binding.dateUser,
            binding.dateOther
        ).forEach {
            if (it == visibleView) it.visibility = View.VISIBLE else it.visibility = View.GONE
        }
    }

    /**
     * Handles the appointment confirmation.
     * @param context The context
     * @param chatMessage The chat message
     * @param appointment The appointment
     */
    private fun handleAppointmentConfirmation(context: Context, chatMessage: ChatMessage, appointment: Appointment) {
        if (checkCalendarPermission(context)) {
            binding.confirmBtnOther.setOnClickListener {
                appointment.appointmentConfirmation = 1
                Log.d(TAG, "setOnAppointmentConfirm: $appointment")
                val chatUid = ChatUtils.getChatUid(chatMessage.senderUid, chatMessage.receiverUid)
                ChatActivity.insertCalendarEvent(context, appointment, "PetCupid Appointment", "Appointment with ${chatMessage.sender}")
                ChatRepository().updateAppointment(chatUid, chatMessage.messageId, appointment)
            }
        } else {
            binding.confirmBtnOther.setOnClickListener {
                Snackbar.make(binding.root, "Please grant calendar permission", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Check calendar permission
     * @param context The context
     * @return Whether the calendar permission is granted
     */
    private fun checkCalendarPermission(context: Context): Boolean {
        val readCalendarPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
        val writeCalendarPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)

        return readCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                writeCalendarPermission == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Handles the audio playback.
     * @param context The context
     * @param chatMessage The chat message
     * @param isMessageSender Whether the current user is the sender
     */
    private fun handleAudioPlayback(context: Context, chatMessage: ChatMessage, isMessageSender: Boolean) {
        val audioView = if (isMessageSender) binding.voiceUser else binding.voiceOther
        audioView.setOnClickListener {
            if (isAudioPlaying) {
                audioUtils.stopPlaybackAudio()
                Log.d(TAG, "handleAudioPlayback: isAudioPlaying: $isAudioPlaying")
            } else {
                audioUtils.startPlaybackAudio(
                    context, Uri.parse(chatMessage.message.content.toString()),
                    callback = { secDuration ->
                        Log.d(TAG, "handleAudioPlayback: duration: $secDuration \"")
                        binding.audioDuration = secDuration.toString() + "\""
                    },
                    onCompletionListener = {
                        Log.d(TAG, "handleAudioPlayback: onAudioPlayingCompletionListener -> $isAudioPlaying")
                        isAudioPlaying = false
                    }
                )
                Log.d(TAG, "handleAudioPlayback: isAudioPlaying: $isAudioPlaying")
            }
            isAudioPlaying = !isAudioPlaying
        }
    }
}
