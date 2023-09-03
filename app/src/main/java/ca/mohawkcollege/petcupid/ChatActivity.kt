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

    private fun isInitDataTransfer(): Boolean {
        val chatTo = intent.getParcelableExtra<ChatListItem>("chatTo") ?: return false
        senderUid = currentUser.uid
        receiver = chatTo.name
        receiverUid = chatTo.receiverUid
        chatUid = getChatUid(currentUser.uid, receiverUid)
        return true
    }

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
                holder.bind(model, currentUser.uid)
            }
        }

        binding.msgRecyclerView.adapter = adapter
        layoutManager = LinearLayoutManager(this)
        binding.msgRecyclerView.layoutManager = layoutManager
    }

    private fun getChatUid(thisUid: String, thatUid: String): String {
        return if (thisUid > thatUid) thisUid + thatUid else thatUid + thisUid
    }
}