package com.company.issadminpanel.view.faqActivity.handbookActivity

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.adapter.HandbookAdapter
import com.company.issadminpanel.databinding.DialogAddHandbookBinding
import com.company.issadminpanel.databinding.FragmentHandbookBinding
import com.company.issadminpanel.interfaces.HandbookItemClickListener
import com.company.issadminpanel.model.HandbookModel
import com.company.issadminpanel.repository.HandbookRepository
import com.company.issadminpanel.utils.KeyboardManager
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.company.issadminpanel.view_model.HandbookViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HandbookFragment : Fragment(), HandbookItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentHandbookBinding

    private lateinit var repository: HandbookRepository
    private lateinit var viewModel: HandbookViewModel

    private var handbookList = ArrayList<HandbookModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHandbookBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        repository = HandbookRepository()
        viewModel = ViewModelProvider(this, HandbookViewModelFactory(repository))[HandbookViewModel::class.java]

        //request for data
        viewModel.requestHandbookList()

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.handbookRecyclerview.adapter = HandbookAdapter(handbookList,  this)

        //add icon click event
        binding.addIcon.setOnClickListener { addHandbook() }

        return binding.root
    }

    private fun observerList() {
        viewModel.handbookListLiveData.observe(viewLifecycleOwner) {
            handbookList.clear()
            if (it != null) {
                handbookList.addAll(it)

                binding.handbookRecyclerview.adapter?.notifyDataSetChanged()

                binding.noHandbookAvailableTextview.visibility = View.GONE
                binding.handbookRecyclerview.visibility = View.VISIBLE
            } else {
                binding.handbookRecyclerview.visibility = View.GONE
                binding.noHandbookAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
            binding.addIcon.visibility = View.VISIBLE
        }
    }

    private fun addHandbook() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogAddHandbookBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()

        //make transparent to default dialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        //set details
        val facultyList = arrayOf("FKE", "FKEKK", "FKP", "FPTT", "FTKEE", "FTMK")
        var selectedFaculty = ""
        val facultyItemAdapter = ArrayAdapter(requireContext(), R.layout.single_dropdown_item, facultyList)
        dialogBinding.facultySelectAutoCompleteTextview.setAdapter(facultyItemAdapter)
        dialogBinding.facultySelectAutoCompleteTextview.setOnClickListener {
            dialogBinding.facultySelectAutoCompleteTextview.setText("")
        }
        dialogBinding.facultySelectAutoCompleteTextview.doOnTextChanged { text, _, _, _ ->
            selectedFaculty = text.toString()
        }

        //add handbook button click event
        dialogBinding.addHandbookButton.setOnClickListener {
            if (selectedFaculty.isEmpty()) {
                requireContext().showWarningToast("Select faculty")
                return@setOnClickListener
            }
            if (!facultyList.contains(selectedFaculty)) {
                requireContext().showWarningToast("Select valid faculty")
                return@setOnClickListener
            }
            if (dialogBinding.programEdittext.text.toString().trim().isEmpty()) {
                dialogBinding.programEdittext.error = "Enter program"
                dialogBinding.programEdittext.requestFocus()
                return@setOnClickListener
            }
            if (dialogBinding.fileLinkEdittext.text.toString().trim().isEmpty()) {
                dialogBinding.fileLinkEdittext.error = "Enter file link"
                dialogBinding.fileLinkEdittext.requestFocus()
                return@setOnClickListener
            }

            if (NetworkManager.isInternetAvailable(requireContext())) {
                LoadingDialog.setText("Booking Now...")
                LoadingDialog.show()
                KeyboardManager.hideKeyBoard(requireContext(), it)

                val handbookId = Firebase.database.reference.child("faq").child("handbook").push().key.toString()

                val handbookModel = HandbookModel(handbookId, selectedFaculty,
                    dialogBinding.programEdittext.text.toString().trim().uppercase(), dialogBinding.fileLinkEdittext.text.toString().trim())

                Firebase.database.reference.child("faq").child("handbook").child(handbookId)
                    .setValue(handbookModel).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            requireContext().showSuccessToast("successful")
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

    override fun onItemClick(currentItem: HandbookModel, clickedOn: String) {
        when(clickedOn) {
            "MAIN_ITEM" -> {
                val bundle = bundleOf(
                    "HANDBOOK_ID" to currentItem.handbook_id,
                    "FACULTY" to currentItem.faculty,
                    "PROGRAM" to currentItem.program,
                    "FILE_LINK" to currentItem.file_link
                )
                findNavController().navigate(R.id. action_handbookFragment_to_editHandbookFragment, bundle)
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

                        Firebase.database.reference.child("faq").child("handbook").child(currentItem.handbook_id.toString()).removeValue().addOnCompleteListener { task ->
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



class HandbookViewModelFactory(private val repository: HandbookRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = HandbookViewModel(repository) as T
}