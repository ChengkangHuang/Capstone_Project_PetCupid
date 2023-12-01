package ca.mohawkcollege.petcupid

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import ca.mohawkcollege.petcupid.chatlist.ChatListItem
import ca.mohawkcollege.petcupid.chatmsg.Appointment
import ca.mohawkcollege.petcupid.chatmsg.ChatMessage
import ca.mohawkcollege.petcupid.chatmsg.ChatMessageViewHolder
import ca.mohawkcollege.petcupid.chatmsg.ChatViewModel
import ca.mohawkcollege.petcupid.databinding.ActivityChatBinding
import ca.mohawkcollege.petcupid.tools.ChatUtils
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.Calendar

class ChatActivity : AppCompatActivity() {

    private val TAG = "====ChatActivity===="
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var layoutManager: LayoutManager
    private lateinit var adapter: FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder>
    private val chatViewModel: ChatViewModel by viewModels()

    private lateinit var binding: ActivityChatBinding

    // Static variables for data transfer between activities
    companion object {
        var chatUid: String = ""
        var senderUid: String = ""
        var sender: String = ""
        var receiverUid: String = ""
        var receiver: String = ""

        /**
         * Insert an appointment event to the calendar
         * @param context The context
         * @param appointment The appointment
         * @param title The title of the appointment
         * @param description The description of the appointment
         */
        fun insertCalendarEvent(context: Context, appointment: Appointment, title: String, description: String) {
            // set event
            val calID: Long = checkCalendarAccount(context).toLong()
            val startMillis: Long = Calendar.getInstance().run {
                set(appointment.year.toInt(), appointment.month.toInt(), appointment.day.toInt(), appointment.hour.toInt(), appointment.minute.toInt())
                timeInMillis
            }
            val endMillis: Long = Calendar.getInstance().run {
                set(appointment.year.toInt(), appointment.month.toInt(), appointment.day.toInt(), appointment.hour.toInt() + 1, appointment.minute.toInt())
                timeInMillis
            }
            val events = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.TITLE, title)
                put(CalendarContract.Events.DESCRIPTION, description)
                put(CalendarContract.Events.CALENDAR_ID, calID)
                put(CalendarContract.Events.EVENT_TIMEZONE, "Canada/Toronto")
            }
            val eventsUri: Uri? = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, events)

            // set reminder
            val eventID: Long = eventsUri?.lastPathSegment?.toLong() ?: 0
            val reminders = ContentValues().apply {
                put(CalendarContract.Reminders.MINUTES, 15)
                put(CalendarContract.Reminders.EVENT_ID, eventID)
                put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
            }
            val remindersUri: Uri? = context.contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminders)
            if (remindersUri == null) {
                Log.d("====ChatActivity====", "insertCalendarEvent: failure")
                Snackbar.make((context as ChatActivity).binding.root, "Failed to save appointment", Snackbar.LENGTH_SHORT).show()
            } else {
                Log.d("====ChatActivity====", "insertCalendarEvent: success")
                Snackbar.make((context as ChatActivity).binding.root, "Appointment saved", Snackbar.LENGTH_SHORT).show()
            }
        }

        /**
         * Check if there is a calendar account
         * @param context The context
         * @return The ID of the calendar account
         */
        private fun checkCalendarAccount(context: Context): Int {
            val projection = arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.ACCOUNT_TYPE
            )
            val cursor = context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                CalendarContract.Calendars.VISIBLE + " = 1 AND " + CalendarContract.Calendars.IS_PRIMARY + "=1",
                null,
                CalendarContract.Calendars._ID + " ASC"
            )
            return if ((cursor?.count ?: 0) > 0) {
                cursor?.moveToFirst()
                cursor?.getInt(0) ?: 0
            } else {
                -1
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!

        if (isInitDataTransfer()) {
            setupRecyclerView(this)
            binding.receiverTextView.text = receiver
            binding.back2MainBtn.setOnClickListener { finish() }
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
        chatViewModel.chatMessageLiveData.observe(this) {
            binding.msgRecyclerView.scrollToPosition(adapter.itemCount - 1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }

    /**
     * Check if the data transfer from previous activity is successful
     * @return true if the data transfer is successful, otherwise false
     */
    private fun isInitDataTransfer(): Boolean {
        val chatTo = intent.getSerializableExtra("chatTo")
        return if (chatTo is ChatListItem) {
            senderUid = currentUser.uid
            sender = currentUser.email.toString()
            receiverUid = chatTo.receiverUid
            receiver = chatTo.receiverNickname
            chatUid = ChatUtils.getChatUid(currentUser.uid, receiverUid)
            true
        } else {
            false
        }
    }

    /**
     * Setup the recycler view for chat messages
     */
    private fun setupRecyclerView(context: Context) {
        val options = FirebaseRecyclerOptions.Builder<ChatMessage>()
            .setQuery(chatViewModel.query, ChatMessage::class.java)
            .build()

        adapter = object : FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder>(options) {
            override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ChatMessageViewHolder {
                val view = layoutInflater.inflate(R.layout.chat_message_view, parent, false)
                return ChatMessageViewHolder(view)
            }

            override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int, model: ChatMessage) {
                if (model.senderUid == currentUser.uid) holder.setIsSender(true) else holder.setIsSender(false)
                holder.bind(context, model)
            }
        }

        binding.msgRecyclerView.adapter = adapter
        layoutManager = LinearLayoutManager(this)
        binding.msgRecyclerView.layoutManager = layoutManager
    }
}