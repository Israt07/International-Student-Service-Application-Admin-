package com.company.issadminpanel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.SingleReviewRatingItemBinding
import com.company.issadminpanel.interfaces.ReviewRatingItemClickListener
import com.company.issadminpanel.model.ReviewRatingModel

class ReviewRatingAdapter(private var itemList: ArrayList<ReviewRatingModel>, private val itemClickListener: ReviewRatingItemClickListener): RecyclerView.Adapter<ReviewRatingAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_review_rating_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleReviewRatingItemBinding.bind(itemView)

        fun onBind(currentItem: ReviewRatingModel) {
            //set details
            binding.userNameTextview.text = currentItem.reviewer_name

            //main item click event
            binding.reviewButton.setOnClickListener {
                itemClickListener.onItemClick(currentItem)
            }
        }

    }
}