package com.company.issadminpanel.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.SingleManageBookingItemBinding
import com.company.issadminpanel.interfaces.ManageBookingItemClickListener
import com.company.issadminpanel.model.HousingBookingModel

class ManageBookingAdapter(private var itemList: ArrayList<HousingBookingModel>, private val itemClickListener: ManageBookingItemClickListener):
    RecyclerView.Adapter<ManageBookingAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_manage_booking_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleManageBookingItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun onBind(currentItem: HousingBookingModel) {
            //set details
            binding.housingTypeTextview.text = currentItem.housing_type
            binding.durationTextview.text = currentItem.duration_of_stay

            //main item click event
            binding.viewButton.setOnClickListener {
                itemClickListener.onViewButtonClick(currentItem)
            }
        }

    }
}