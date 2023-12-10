package com.company.issadminpanel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.SingleHandbookItemBinding
import com.company.issadminpanel.interfaces.HandbookItemClickListener
import com.company.issadminpanel.model.HandbookModel

class HandbookAdapter(private var itemList: ArrayList<HandbookModel>, private val itemClickListener: HandbookItemClickListener): RecyclerView.Adapter<HandbookAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_handbook_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleHandbookItemBinding.bind(itemView)

        fun onBind(currentItem: HandbookModel) {
            //set details
            binding.programTextview.text = currentItem.program

            //main item click event
            binding.handbookButton.setOnClickListener {
                itemClickListener.onItemClick(currentItem, "MAIN_ITEM")
            }

            //delete button click event
            binding.deleteIcon.setOnClickListener {
                itemClickListener.onItemClick(currentItem, "DELETE_ICON")
            }
        }

    }
}