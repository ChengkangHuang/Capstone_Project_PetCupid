package ca.mohawkcollege.petcupid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.mohawkcollege.petcupid.chatlist.ChatListAdapter
import ca.mohawkcollege.petcupid.chatlist.ChatListViewModel
import ca.mohawkcollege.petcupid.databinding.FragmentChatListBinding
import com.google.firebase.auth.FirebaseAuth

/**
 * An ChatListFragment subclass.
 */
class ChatListFragment : Fragment() {

    private val TAG = "====ChatListFragment===="

    private lateinit var mAuth: FirebaseAuth
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatListViewModel: ChatListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        chatListViewModel = ViewModelProvider(this)[ChatListViewModel::class.java]
        val adapter = context?.let {
            ChatListAdapter(it, mutableListOf())
        }
        binding.chatListView.adapter = adapter
        binding.chatListView.setOnItemClickListener { _, _, position, _ ->
            val chatListItem = adapter?.getItem(position)
            Log.d(TAG, "onViewCreated -> onChatListItemClick: $chatListItem")
            Intent(context, ChatActivity::class.java).apply {
                putExtra("chatTo", chatListItem)
                startActivity(this)
            }
        }

        binding.chatListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            // Hide bottom navigation bar when scrolling
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                Log.d(TAG, "onScrollStateChanged: $scrollState")
                when (scrollState) {
                    AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL -> {
                        (activity as MainActivity).hideNavBottomSheet()
                    }
                    AbsListView.OnScrollListener.SCROLL_STATE_FLING -> {
                        (activity as MainActivity).hideNavBottomSheet()
                    }
                    AbsListView.OnScrollListener.SCROLL_STATE_IDLE -> {
                        (activity as MainActivity).showNavBottomSheet()
                    }
                }
            }

            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {}
        })

        chatListViewModel.chatListItems.observe(viewLifecycleOwner) { chatListItem ->
            adapter?.clear()
            adapter?.addAll(chatListItem)
            adapter?.notifyDataSetChanged()
            Log.d(TAG, "onChatListViewCreated -> chatListItem: $chatListItem")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leak
    }
}