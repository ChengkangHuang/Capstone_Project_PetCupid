package ca.mohawkcollege.petcupid.chatlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import ca.mohawkcollege.petcupid.databinding.ChatListviewItemBinding

class ChatListAdapter(context: Context, chatListItems: MutableList<ChatListItem>):
    ArrayAdapter<ChatListItem>(context, 0, chatListItems) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ChatListItemViewHolder
        val itemView: View

        if (convertView == null) {
            val binding = ChatListviewItemBinding.inflate(LayoutInflater.from(context), parent, false)
            itemView = binding.root
            viewHolder = ChatListItemViewHolder(binding)
            itemView.tag = viewHolder
        } else {
            itemView = convertView
            viewHolder = itemView.tag as ChatListItemViewHolder
        }

        val chatListItem = getItem(position)
        if (chatListItem != null) {
            viewHolder.bind(chatListItem)
        }

        return itemView
    }

    private class ChatListItemViewHolder(private val binding: ChatListviewItemBinding) {
        fun bind(chatListItem: ChatListItem) {
            binding.chatListItem = chatListItem
        }
    }
}