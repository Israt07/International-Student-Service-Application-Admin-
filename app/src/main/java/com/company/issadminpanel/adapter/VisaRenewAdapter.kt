package com.company.issadminpanel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.SingleVisaRenewItemBinding
import com.company.issadminpanel.interfaces.VisaRenewItemClickListener
import com.company.issadminpanel.model.VisaRenewModel

class VisaRenewAdapter(private var itemList: ArrayList<VisaRenewModel>, private val itemClickListener: VisaRenewItemClickListener): RecyclerView.Adapter<VisaRenewAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_visa_renew_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleVisaRenewItemBinding.bind(itemView)

        fun onBind(currentItem: VisaRenewModel) {
            //set details
            binding.userNameTextview.text = currentItem.full_name

            //main item click event
            binding.formButton.setOnClickListener {
                itemClickListener.onItemClick(currentItem)
            }
        }

    }
}