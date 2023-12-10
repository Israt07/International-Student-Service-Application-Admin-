package com.company.issadminpanel.view

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.adapter.ReviewRatingAdapter
import com.company.issadminpanel.databinding.DialogViewReviewRatingBinding
import com.company.issadminpanel.databinding.FragmentReviewRatingBinding
import com.company.issadminpanel.interfaces.ReviewRatingItemClickListener
import com.company.issadminpanel.model.ReviewRatingModel
import com.company.issadminpanel.repository.ReviewRatingRepository
import com.company.issadminpanel.utils.loadImage
import com.company.issadminpanel.view_model.ReviewRatingViewModel

class ReviewRatingFragment : Fragment(), ReviewRatingItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentReviewRatingBinding

    private lateinit var repository: ReviewRatingRepository
    private lateinit var viewModel: ReviewRatingViewModel

    private var reviewList = ArrayList<ReviewRatingModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReviewRatingBinding.inflate(inflater, container, false)

        repository = ReviewRatingRepository()
        viewModel = ViewModelProvider(this, ReviewRatingViewModelFactory(repository))[ReviewRatingViewModel::class.java]

        //request for data
        viewModel.requestReviewList()

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.reviewsRecyclerview.adapter = ReviewRatingAdapter(reviewList,  this)

        return binding.root
    }

    private fun observerList() {
        viewModel.reviewListLiveData.observe(viewLifecycleOwner) {
            reviewList.clear()
            if (it != null) {
                reviewList.addAll(it)

                binding.reviewsRecyclerview.adapter?.notifyDataSetChanged()

                binding.noReviewAvailableTextview.visibility = View.GONE
                binding.reviewsRecyclerview.visibility = View.VISIBLE
            } else {
                binding.reviewsRecyclerview.visibility = View.GONE
                binding.noReviewAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
        }
    }

    override fun onItemClick(currentItem: ReviewRatingModel) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogViewReviewRatingBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()

        //make transparent to default dialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        //set details
        when(currentItem.rating) {
            "0.0" -> dialogBinding.ratingStarImageview.loadImage(R.drawable.rating_star_blank)
            "1.0" -> dialogBinding.ratingStarImageview.loadImage(R.drawable.rating_star_1)
            "2.0" -> dialogBinding.ratingStarImageview.loadImage(R.drawable.rating_star_2)
            "3.0" -> dialogBinding.ratingStarImageview.loadImage(R.drawable.rating_star_3)
            "4.0" -> dialogBinding.ratingStarImageview.loadImage(R.drawable.rating_star_4)
            "5.0" -> dialogBinding.ratingStarImageview.loadImage(R.drawable.rating_star_5)
        }
        dialogBinding.fullNameTextview.text = currentItem.reviewer_name
        dialogBinding.reviewTextview.text = currentItem.review

        //view student info button click event
        dialogBinding.viewStudentInfoButton.setOnClickListener {
            val bundle = bundleOf(
                "USER_ID" to currentItem.reviewer_id
            )
            findNavController().navigate(R.id.action_reviewRatingFragment_to_userDetailsFragment, bundle)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}



class ReviewRatingViewModelFactory(private val repository: ReviewRatingRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ReviewRatingViewModel(repository) as T
}