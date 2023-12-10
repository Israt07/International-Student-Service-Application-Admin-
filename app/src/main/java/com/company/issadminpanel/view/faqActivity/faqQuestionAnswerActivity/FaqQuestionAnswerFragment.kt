package com.company.issadminpanel.view.faqActivity.faqQuestionAnswerActivity

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
import com.company.issadminpanel.adapter.FaqQuestionAnswerAdapter
import com.company.issadminpanel.databinding.DialogAddFaqBinding
import com.company.issadminpanel.databinding.FragmentFaqQuestionAnswerBinding
import com.company.issadminpanel.interfaces.FaqQuestionAnswerItemClickListener
import com.company.issadminpanel.model.FaqModel
import com.company.issadminpanel.repository.FaqQuestionAnswerRepository
import com.company.issadminpanel.utils.KeyboardManager
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.company.issadminpanel.view_model.FaqQuestionAnswerViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FaqQuestionAnswerFragment : Fragment(), FaqQuestionAnswerItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentFaqQuestionAnswerBinding

    private lateinit var repository: FaqQuestionAnswerRepository
    private lateinit var viewModel: FaqQuestionAnswerViewModel

    private var title = ""
    private var faqList = ArrayList<FaqModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFaqQuestionAnswerBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        //set details
        title = arguments?.getString("TITLE").toString()

        repository = FaqQuestionAnswerRepository()
        viewModel = ViewModelProvider(this, FaqQuestionAnswerViewModelFactory(repository))[FaqQuestionAnswerViewModel::class.java]

        //request for data
        viewModel.requestFaqList(title)

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set title text
        binding.titleTextview.text = title

        //add icon click event
        binding.addFaqIcon.setOnClickListener { addFaq() }

        //set recyclerview adapter
        binding.faqRecyclerview.adapter = FaqQuestionAnswerAdapter(faqList,  this)

        return binding.root
    }

    private fun observerList() {
        viewModel.faqListLiveData.observe(viewLifecycleOwner) {
            faqList.clear()
            if (it != null) {
                faqList.addAll(it)

                binding.faqRecyclerview.adapter?.notifyDataSetChanged()

                binding.noFaqAvailableTextview.visibility = View.GONE
                binding.faqRecyclerview.visibility = View.VISIBLE
            } else {
                binding.faqRecyclerview.visibility = View.GONE
                binding.noFaqAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
            binding.addFaqIcon.visibility = View.VISIBLE
        }
    }

    private fun addFaq() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogAddFaqBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()

        //make transparent to default dialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        //add button click event
        dialogBinding.addFaqButton.setOnClickListener {
            if (dialogBinding.questionEdittext.text.toString().trim().isEmpty()) {
                dialogBinding.questionEdittext.error = "Enter question"
                dialogBinding.questionEdittext.requestFocus()
                return@setOnClickListener
            }
            if (dialogBinding.answerEdittext.text.toString().trim().isEmpty()) {
                dialogBinding.answerEdittext.error = "Enter answer"
                dialogBinding.answerEdittext.requestFocus()
                return@setOnClickListener
            }

            if (NetworkManager.isInternetAvailable(requireContext())) {
                LoadingDialog.setText("Adding faq...")
                LoadingDialog.show()
                KeyboardManager.hideKeyBoard(requireContext(), it)

                val faqId = Firebase.database.reference.child("faq").push().key.toString()

                val faqModel = FaqModel(faqId, title, dialogBinding.questionEdittext.text.toString().trim(), dialogBinding.answerEdittext.text.toString().trim())

                Firebase.database.reference.child("faq").child(faqId)
                    .setValue(faqModel).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            requireContext().showSuccessToast("Faq added")
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

    override fun onItemClick(currentItem: FaqModel, clickedOn: String) {
        when(clickedOn) {
            "MAIN_ITEM" -> {
                val bundle = bundleOf(
                    "FAQ_ID" to currentItem.faq_id,
                    "TITLE" to currentItem.title,
                    "QUESTION" to currentItem.question,
                    "ANSWER" to currentItem.answer
                )
                findNavController().navigate(R.id. action_faqQuestionAnswerFragment_to_editFaqQuestionAnswerFragment, bundle)
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

                        Firebase.database.reference.child("faq").child(currentItem.faq_id.toString()).removeValue().addOnCompleteListener { task ->
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



class FaqQuestionAnswerViewModelFactory(private val repository: FaqQuestionAnswerRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = FaqQuestionAnswerViewModel(repository) as T
}