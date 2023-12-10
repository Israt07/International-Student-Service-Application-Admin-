package com.company.issadminpanel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.SingleCommunityItemBinding
import com.company.issadminpanel.interfaces.CommunityItemClickListener
import com.company.issadminpanel.model.CommunityModel

class CommunityAdapter(private var itemList: ArrayList<CommunityModel>, private val itemClickListener: CommunityItemClickListener):
    RecyclerView.Adapter<CommunityAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_community_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleCommunityItemBinding.bind(itemView)

        fun onBind(currentItem: CommunityModel) {
            //set details
            binding.communityTitle.text = currentItem.title

            //main item click event
            binding.communityButton.setOnClickListener {
                itemClickListener.onItemClick(currentItem, "MAIN_ITEM")
            }

            //delete button click event
            binding.deleteIcon.setOnClickListener {
                itemClickListener.onItemClick(currentItem, "DELETE_ICON")
            }
        }

    }
}