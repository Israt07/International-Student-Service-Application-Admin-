package com.company.issadminpanel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.SingleChatListItemBinding
import com.company.issadminpanel.databinding.SingleMapItemBinding
import com.company.issadminpanel.interfaces.ChatItemClickListener
import com.company.issadminpanel.interfaces.MapItemClickListener
import com.company.issadminpanel.model.ChatModel
import com.company.issadminpanel.model.MapModel

class ChatListAdapter(private var itemList: ArrayList<ChatModel>, private val itemClickListener: ChatItemClickListener):
    RecyclerView.Adapter<ChatListAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_chat_list_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleChatListItemBinding.bind(itemView)

        fun onBind(currentItem: ChatModel) {
            //set details
            binding.studentNameTextview.text = currentItem.student_name

            itemView.setOnClickListener { itemClickListener.onItemClick(currentItem) }
        }

    }
}