package com.company.issadminpanel.view.facilityActivity.communityActivity

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
import com.company.issadminpanel.adapter.CommunityAdapter
import com.company.issadminpanel.databinding.DialogAddCommunityBinding
import com.company.issadminpanel.databinding.FragmentCommunityBinding
import com.company.issadminpanel.interfaces.CommunityItemClickListener
import com.company.issadminpanel.model.CommunityModel
import com.company.issadminpanel.repository.CommunityRepository
import com.company.issadminpanel.utils.KeyboardManager
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.company.issadminpanel.view_model.CommunityViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CommunityFragment : Fragment(), CommunityItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentCommunityBinding

    private lateinit var repository: CommunityRepository
    private lateinit var viewModel: CommunityViewModel

    private var communityList = ArrayList<CommunityModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCommunityBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        repository = CommunityRepository()
        viewModel = ViewModelProvider(this, CommunityViewModelFactory(repository))[CommunityViewModel::class.java]

        //request for data
        viewModel.requestCommunityList()

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.communityRecyclerview.adapter = CommunityAdapter(communityList,  this)

        //add icon click event
        binding.addCommunityIcon.setOnClickListener { addCommunity() }

        return binding.root
    }

    private fun observerList() {
        viewModel.communityListLiveData.observe(viewLifecycleOwner) {
            communityList.clear()
            if (it != null) {
                communityList.addAll(it)

                binding.communityRecyclerview.adapter?.notifyDataSetChanged()

                binding.noCommunityAvailableTextview.visibility = View.GONE
                binding.communityRecyclerview.visibility = View.VISIBLE
            } else {
                binding.communityRecyclerview.visibility = View.GONE
                binding.noCommunityAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
            binding.addCommunityIcon.visibility = View.VISIBLE
        }
    }

    private fun addCommunity() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogAddCommunityBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()

        //make transparent to default dialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        //add country button click event
        dialogBinding.addCountryButton.setOnClickListener {
            if (dialogBinding.countryNameEdittext.text.toString().trim().isEmpty()) {
                dialogBinding.countryNameEdittext.error = "Enter country name"
                dialogBinding.countryNameEdittext.requestFocus()
                return@setOnClickListener
            }

            if (NetworkManager.isInternetAvailable(requireContext())) {
                LoadingDialog.setText("Adding country...")
                LoadingDialog.show()
                KeyboardManager.hideKeyBoard(requireContext(), it)

                val communityModel = CommunityModel(dialogBinding.countryNameEdittext.text.toString().trim(), "")

                Firebase.database.reference.child("facilities").child("community").child(dialogBinding.countryNameEdittext.text.toString().trim())
                    .setValue(communityModel).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            requireContext().showSuccessToast("country added")
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

    override fun onItemClick(currentItem: CommunityModel, clickedOn: String) {
        when(clickedOn) {
            "MAIN_ITEM" -> {
                val bundle = bundleOf(
                    "TITLE" to currentItem.title
                )
                findNavController().navigate(R.id.action_communityFragment_to_communityDetailsFragment, bundle)
            }
            "DELETE_ICON" -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Confirmation")
                builder.setIcon(R.mipmap.ic_launcher)
                builder.setMessage("Do You Really Want To Delete This?")
                builder.setPositiveButton("Yes") {dialog, _ ->
                    if (NetworkManager.isInternetAvailable(requireContext())){
                        LoadingDialog.setText("Deleting...")
                        LoadingDialog.show()

                        Firebase.database.reference.child("facilities").child("community").child(currentItem.title.toString()).removeValue()
                            .addOnCompleteListener { task ->
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



class CommunityViewModelFactory(private val repository: CommunityRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = CommunityViewModel(repository) as T
}