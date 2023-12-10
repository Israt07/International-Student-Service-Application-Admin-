package com.company.issadminpanel.view.facilityActivity

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.adapter.FacilityImageAdapter
import com.company.issadminpanel.databinding.DialogAddImageBinding
import com.company.issadminpanel.databinding.FragmentFacilityDetailsBinding
import com.company.issadminpanel.interfaces.FacilityImageItemClickListener
import com.company.issadminpanel.model.FacilityImageModel
import com.company.issadminpanel.model.FacilityModel
import com.company.issadminpanel.repository.FacilityDetailsRepository
import com.company.issadminpanel.utils.KeyboardManager
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.company.issadminpanel.view_model.FacilityDetailsViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FacilityDetailsFragment : Fragment(), FacilityImageItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentFacilityDetailsBinding

    private lateinit var repository: FacilityDetailsRepository
    private lateinit var viewModel: FacilityDetailsViewModel

    private var title = ""
    private var facility: FacilityModel? = null
    private var facilityImageList = ArrayList<FacilityImageModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFacilityDetailsBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        //set details
        title = arguments?.getString("TITLE").toString()

        repository = FacilityDetailsRepository()
        viewModel = ViewModelProvider(this, FacilityDetailsViewModelFactory(repository))[FacilityDetailsViewModel::class.java]

        //request for data
        viewModel.requestFacilityImages(title)

        observerList()

        //hiding button
        binding.updateButton.visibility = View.GONE

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set title text
        binding.titleTextview.text = title

        //set recyclerview adapter
        binding.imageRecyclerview.adapter = FacilityImageAdapter(facilityImageList,  this)

        //add Image Button click event
        binding.addImageButton.setOnClickListener { addImageDialog() }

        //update button click event
        binding.updateButton.setOnClickListener { updateFacilityDetails() }

        return binding.root
    }

    private fun observerList() {
        viewModel.facilityImagesLiveData.observe(viewLifecycleOwner) {
            facilityImageList.clear()
            if (it != null) {
                facilityImageList.addAll(it)

                facilityImageList.reverse()

                binding.imageRecyclerview.adapter?.notifyDataSetChanged()
            }
            //request for data
            viewModel.requestFacilityDetails(title)
        }

        viewModel.facilityDetailsLiveData.observe(viewLifecycleOwner) {
            it?.let {
                facility = it
                binding.detailsEdittext.setText(it.details)
            }
            binding.updateButton.visibility = View.VISIBLE
        }

        viewModel.toastMessageLiveData.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    "Updated" -> {
                        requireContext().showSuccessToast(it)
                    }
                    else -> requireContext().showErrorToast(it)
                }

                viewModel.resetToastMessage()
            }

            LoadingDialog.dismiss()
        }
    }

    private fun addImageDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogAddImageBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()

        //make transparent to default dialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        //add button click event
        dialogBinding.addImageButton.setOnClickListener {
            if (dialogBinding.imageLinkEdittext.text.toString().trim().isEmpty()) {
                dialogBinding.imageLinkEdittext.error = "Enter image link"
                dialogBinding.imageLinkEdittext.requestFocus()
                return@setOnClickListener
            }

            if (NetworkManager.isInternetAvailable(requireContext())) {
                LoadingDialog.setText("Adding Image...")
                LoadingDialog.show()
                KeyboardManager.hideKeyBoard(requireContext(), it)

                val imageId = Firebase.database.reference.child("facilities").child("images").push().key.toString()

                val imageModel = FacilityImageModel(imageId, title, dialogBinding.imageLinkEdittext.text.toString().trim())

                Firebase.database.reference.child("facilities").child("images").child(imageId)
                    .setValue(imageModel).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            requireContext().showSuccessToast("Image added")
                            alertDialog.dismiss()
                        } else {
                            requireContext().showErrorToast("Something wrong.")
                        }
                        LoadingDialog.dismiss()
                    }
            } else {
                requireContext().showWarningToast("No internet available")
            }
        }

        alertDialog.show()
    }

    private fun updateFacilityDetails() {
        if (binding.detailsEdittext.text.toString().trim().isEmpty()) {
            requireContext().showWarningToast("Enter facility details")
            return
        }

        if (facility == null) {
            facility = FacilityModel(title, binding.detailsEdittext.text.toString().trim())
        } else {
            facility?.details = binding.detailsEdittext.text.toString().trim()
        }

        if (NetworkManager.isInternetAvailable(requireContext())) {
            LoadingDialog.setText("Updating Details...")
            LoadingDialog.show()

            viewModel.updateFacilityDetails(facility)
        } else {
            requireContext().showWarningToast("No internet available")
        }
    }

    override fun onImageItemClick(currentItem: FacilityImageModel, clickedOn: String) {
        when(clickedOn) {
            "DELETE_ICON" -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Confirmation")
                builder.setIcon(R.mipmap.ic_launcher)
                builder.setMessage("Do You Really Want To Delete This?")
                builder.setPositiveButton("Yes") {dialog, _ ->
                    if (NetworkManager.isInternetAvailable(requireContext())){
                        LoadingDialog.setText("Deleting...")
                        LoadingDialog.show()

                        Firebase.database.reference.child("facilities").child("images").child(currentItem.image_id.toString()).removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                requireContext().showSuccessToast("Deleted")
                            } else {
                                requireContext().showErrorToast(task.exception?.localizedMessage.toString())
                            }
                            dialog.dismiss()
                            LoadingDialog.dismiss()
                        }
                    } else {
                        requireContext().showErrorToast("No internet connection")
                    }
                }
                builder.setNegativeButton("No") {dialog, _ ->
                    dialog.dismiss()
                }

                val alertDialog = builder.create()
                alertDialog.show()
            }
        }
    }
}



class FacilityDetailsViewModelFactory(private val repository: FacilityDetailsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = FacilityDetailsViewModel(repository) as T
}