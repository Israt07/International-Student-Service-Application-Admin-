package com.company.issadminpanel.view.faqActivity.faqQuestionAnswerActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.databinding.FragmentEditFaqQuestionAnswerBinding
import com.company.issadminpanel.model.FaqModel
import com.company.issadminpanel.utils.KeyboardManager
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditFaqQuestionAnswerFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentEditFaqQuestionAnswerBinding

    private var faqId = ""
    private var title = ""
    private var question = ""
    private var answer = ""

    private lateinit var faqModel: FaqModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditFaqQuestionAnswerBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        faqId = arguments?.getString("FAQ_ID").toString()
        title = arguments?.getString("TITLE").toString()
        question = arguments?.getString("QUESTION").toString()
        answer = arguments?.getString("ANSWER").toString()

        faqModel = FaqModel(faqId, title, question, answer)

        //set details
        binding.questionEdittext.setText(question)
        binding.answerEdittext.setText(answer)

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //update button click event
        binding.updateFaqButton.setOnClickListener {
            updateFaq(it)
        }

        return binding.root
    }

    private fun updateFaq(view: View) {
        if (binding.questionEdittext.text.toString().trim().isEmpty()) {
            binding.questionEdittext.error = "Enter Question"
            binding.questionEdittext.requestFocus()
            return
        }
        if (binding.answerEdittext.text.toString().trim().isEmpty()) {
            binding.answerEdittext.error = "Enter answer"
            binding.answerEdittext.requestFocus()
            return
        }

        faqModel.question = binding.questionEdittext.text.toString().trim()
        faqModel.answer = binding.answerEdittext.text.toString().trim()

        if (NetworkManager.isInternetAvailable(requireContext())) {
            LoadingDialog.setText("Updating...")
            LoadingDialog.show()

            Firebase.database.reference.child("faq").child(faqId)
                .setValue(faqModel).addOnCompleteListener { task ->
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