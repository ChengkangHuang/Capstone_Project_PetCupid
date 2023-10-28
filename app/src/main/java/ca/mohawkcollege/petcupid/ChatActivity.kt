package ca.mohawkcollege.petcupid

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import ca.mohawkcollege.petcupid.chatlist.ChatListItem
import ca.mohawkcollege.petcupid.chatmsg.ChatMessage
import ca.mohawkcollege.petcupid.chatmsg.ChatMessageViewHolder
import ca.mohawkcollege.petcupid.chatmsg.ChatViewModel
import ca.mohawkcollege.petcupid.databinding.ActivityChatBinding
import ca.mohawkcollege.petcupid.tools.ChatUtils
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

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
        var receiver: String = ""
        var receiverUid: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!

        if (isInitDataTransfer()) {
            setupRecyclerView()
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
        val chatTo = intent.getParcelableExtra<ChatListItem>("chatTo") ?: return false
        senderUid = currentUser.uid
        receiver = chatTo.name
        receiverUid = chatTo.receiverUid
        chatUid = ChatUtils.getChatUid(currentUser.uid, receiverUid)
        return true
    }

    /**
     * Setup the recycler view for chat messages
     */
    private fun setupRecyclerView() {
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
                holder.bind(model)
            }
        }

        binding.msgRecyclerView.adapter = adapter
        layoutManager = LinearLayoutManager(this)
        binding.msgRecyclerView.layoutManager = layoutManager
    }
}