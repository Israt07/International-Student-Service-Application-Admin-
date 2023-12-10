package com.company.issadminpanel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.SingleUserItemBinding
import com.company.issadminpanel.interfaces.UserItemClickListener
import com.company.issadminpanel.model.UserModel

class UserAdapter(private var itemList: ArrayList<UserModel>, private val itemClickListener: UserItemClickListener):
    RecyclerView.Adapter<UserAdapter.ItemViewHolder>() {

    fun submitList(itemList: ArrayList<UserModel>) {
        this.itemList = itemList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_user_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleUserItemBinding.bind(itemView)

        fun onBind(currentItem: UserModel) {
            //set details
            binding.userNameTextview.text = currentItem.name

            //main item click event
            binding.userButton.setOnClickListener {
                itemClickListener.onItemClick(currentItem)
            }
        }

    }
}