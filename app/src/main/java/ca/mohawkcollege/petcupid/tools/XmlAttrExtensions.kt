package ca.mohawkcollege.petcupid.tools

import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import ca.mohawkcollege.petcupid.chatmsg.Appointment
import ca.mohawkcollege.petcupid.chatmsg.ChatMessage
import ca.mohawkcollege.petcupid.chatmsg.ChatRepository
import ca.mohawkcollege.petcupid.searchcontact.SearchContactItem
import coil.load
import coil.transform.RoundedCornersTransformation

/**
 * The XmlAttrExtensions class.
 */
class XmlAttrExtensions {

    /**
     * The companion object.
     * Contains the functions that can be called from XML.
     */
    companion object {

        private const val TAG = "====XmlAttrExtensions===="

        /**
         * Loads an image into an ImageView.
         * Allows the function to be called from XML.
         * @param imageView The ImageView to load the image into.
         * @param url The URL of the image to load.
         * @param @JvmStatic @BindingAdapter("imageLoader")
         */
        @JvmStatic
        @BindingAdapter("imageLoader")
        fun setImageLoader(imageView: ImageView, url: String?) {
            Log.d(TAG, "xmlImageLoader: $url")
            imageView.load(url) {
                transformations(RoundedCornersTransformation(16f))
            }
        }

        /**
         * Set a onClickListener for appointment confirm button.
         * Allows the function to be called from XML.
         * @param button The button to set the onClickListener for.
         * @param messageData The chat message data.
         */
        @JvmStatic
        @BindingAdapter("onAppointmentConfirm")
        fun setOnAppointmentConfirm(button: Button, messageData: ChatMessage?) {
            button.setOnClickListener {
                if (messageData != null) {
                    val appointment = Appointment(messageData.message.content as HashMap<String, Long>)
                    appointment.appointmentConfirmation = 1
                    Log.d(TAG, "setOnAppointmentConfirm: $appointment")
                    val chatUid = ChatUtils.getChatUid(messageData.senderUid, messageData.receiverUid)
                    ChatRepository().updateAppointment(chatUid, messageData.messageId, appointment)
                }
            }
        }

        @JvmStatic
        @BindingAdapter("onAddNewFriend")
        fun setOnAddNewFriend(button: Button, searchContactItem: SearchContactItem?) {
            button.setOnClickListener {
                if (searchContactItem != null) {
                    Log.d(TAG, "setOnAddNewFriend: $searchContactItem")
                }
            }
        }
    }
}
