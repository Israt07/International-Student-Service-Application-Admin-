package com.company.issadminpanel.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.SingleManageAppointmentItemBinding
import com.company.issadminpanel.interfaces.ManageAppointmentItemClickListener
import com.company.issadminpanel.model.AppointmentModel

class ManageAppointmentAdapter(private var itemList: ArrayList<AppointmentModel>, private val itemClickListener: ManageAppointmentItemClickListener):
    RecyclerView.Adapter<ManageAppointmentAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_manage_appointment_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleManageAppointmentItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun onBind(currentItem: AppointmentModel) {
            //set details
            binding.dateTextview.text = currentItem.date
            binding.timeTextview.text = currentItem.time

            //main item click event
            binding.viewButton.setOnClickListener {
                itemClickListener.onViewButtonClick(currentItem)
            }
        }

    }
}