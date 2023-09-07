package ca.mohawkcollege.petcupid.searchcontact

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import ca.mohawkcollege.petcupid.databinding.SearchContactItemBinding

class SearchContactResultAdapter(context: Context, searchContactItems: MutableList<SearchContactItem>):
    ArrayAdapter<SearchContactItem>(context, 0, searchContactItems) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: SearchContactItemViewHolder
        val itemView: View

        if (convertView == null) {
            val binding = SearchContactItemBinding.inflate(LayoutInflater.from(context), parent, false)
            itemView = binding.root
            viewHolder = SearchContactItemViewHolder(binding)
            itemView.tag = viewHolder
        } else {
            itemView = convertView
            viewHolder = itemView.tag as SearchContactItemViewHolder
        }

        val searchContactItem = getItem(position)
        if (searchContactItem != null) {
            viewHolder.bind(searchContactItem)
        }

        return itemView
    }

    private class SearchContactItemViewHolder(private val binding: SearchContactItemBinding) {
        fun bind(searchContactItem: SearchContactItem) {
            binding.searchContactItem = searchContactItem
        }
    }
}