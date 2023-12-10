package com.company.issadminpanel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.SingleImageItemBinding
import com.company.issadminpanel.interfaces.CommunityImageItemClickListener
import com.company.issadminpanel.model.CommunityImageModel
import com.company.issadminpanel.utils.loadImage

class CommunityImageAdapter(private var itemList: ArrayList<CommunityImageModel>, private val itemClickListener: CommunityImageItemClickListener): RecyclerView.Adapter<CommunityImageAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_image_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleImageItemBinding.bind(itemView)

        fun onBind(currentItem: CommunityImageModel) {
            //set details
            binding.image.loadImage(currentItem.image_link)

            //delete button click event
            binding.deleteButton.setOnClickListener {
                itemClickListener.onImageItemClick(currentItem, "DELETE_ICON")
            }
        }

    }
}