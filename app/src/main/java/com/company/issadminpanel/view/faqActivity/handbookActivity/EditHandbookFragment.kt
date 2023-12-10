package com.company.issadminpanel.view.faqActivity.handbookActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.databinding.FragmentEditHandbookBinding
import com.company.issadminpanel.model.HandbookModel
import com.company.issadminpanel.utils.KeyboardManager
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditHandbookFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentEditHandbookBinding

    private var handbookId = ""
    private var faculty = ""
    private var program = ""
    private var fileLink = ""

    private lateinit var handbookModel: HandbookModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditHandbookBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        handbookId = arguments?.getString("HANDBOOK_ID").toString()
        faculty = arguments?.getString("FACULTY").toString()
        program = arguments?.getString("PROGRAM").toString()
        fileLink = arguments?.getString("FILE_LINK").toString()

        handbookModel = HandbookModel(handbookId, faculty, program, fileLink)

        //set details
        binding.programEdittext.setText(program)
        binding.fileLinkEdittext.setText(fileLink)

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //update button click event
        binding.updateHandbookButton.setOnClickListener { updateHandbook(it) }

        return binding.root
    }

    private fun updateHandbook(view: View) {
        if (binding.programEdittext.text.toString().trim().isEmpty()) {
            binding.programEdittext.error = "Enter program"
            binding.programEdittext.requestFocus()
            return
        }
        if (binding.fileLinkEdittext.text.toString().trim().isEmpty()) {
            binding.fileLinkEdittext.error = "Enter file link"
            binding.fileLinkEdittext.requestFocus()
            return
        }

        handbookModel.program = binding.programEdittext.text.toString().trim().uppercase()
        handbookModel.file_link = binding.fileLinkEdittext.text.toString().trim()

        if (NetworkManager.isInternetAvailable(requireContext())) {
            LoadingDialog.setText("Updating...")
            LoadingDialog.show()

            Firebase.database.reference.child("faq").child("handbook").child(handbookId)
                .setValue(handbookModel).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        KeyboardManager.hideKeyBoard(requireContext(), view)
                        requireContext().showSuccessToast("Update successful")
                        findNavController().popBackStack()
                    } else {
                        requireContext().showErrorToast("Something wrong.")
                    }
                    LoadingDialog.dismiss()
                }
        } else {
            requireContext().showWarningToast("No internet available")
        }
    }
}