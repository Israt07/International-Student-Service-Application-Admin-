package com.company.issadminpanel.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.SinglePaymentItemBinding
import com.company.issadminpanel.interfaces.PaymentItemClickListener
import com.company.issadminpanel.model.PaymentModel

class PaymentsAdapter(private var itemList: ArrayList<PaymentModel>, private val listener: PaymentItemClickListener):
    RecyclerView.Adapter<PaymentsAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_payment_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SinglePaymentItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun onBind(currentItem: PaymentModel) {
            //set details
            binding.debitTextview.text = currentItem.debit_rm
            binding.creditTextview.text = currentItem.credit_rm
            binding.statusTextview.text = currentItem.status

            itemView.setOnClickListener {
                listener.onItemClick(currentItem)
            }
        }

    }
}