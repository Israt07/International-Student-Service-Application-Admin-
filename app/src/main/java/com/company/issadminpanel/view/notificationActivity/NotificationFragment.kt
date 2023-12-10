package com.company.issadminpanel.view.notificationActivity

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
import com.company.issadminpanel.adapter.NotificationAdapter
import com.company.issadminpanel.databinding.DialogAddNotificationBinding
import com.company.issadminpanel.databinding.FragmentNotificationBinding
import com.company.issadminpanel.interfaces.NotificationItemClickListener
import com.company.issadminpanel.model.NotificationModel
import com.company.issadminpanel.repository.NotificationRepository
import com.company.issadminpanel.utils.KeyboardManager
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.company.issadminpanel.view_model.NotificationViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NotificationFragment : Fragment(), NotificationItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentNotificationBinding

    private lateinit var repository: NotificationRepository
    private lateinit var viewModel: NotificationViewModel

    private var notificationList = ArrayList<NotificationModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        repository = NotificationRepository()
        viewModel = ViewModelProvider(this, NotificationViewModelFactory(repository))[NotificationViewModel::class.java]

        //request for data
        viewModel.requestNotificationList()

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.notificationRecyclerview.adapter = NotificationAdapter(notificationList,  this@NotificationFragment)

        //add icon click event
        binding.addNotificationIcon.setOnClickListener { addNotification() }

        return binding.root
    }

    private fun observerList() {
        viewModel.notificationListLiveData.observe(viewLifecycleOwner) {
            notificationList.clear()
            if (it != null) {
                notificationList.addAll(it)
                notificationList.reverse()

                binding.notificationRecyclerview.adapter?.notifyDataSetChanged()

                binding.noNotificationAvailableTextview.visibility = View.GONE
                binding.notificationRecyclerview.visibility = View.VISIBLE
            } else {
                binding.notificationRecyclerview.visibility = View.GONE
                binding.noNotificationAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
            binding.addNotificationIcon.visibility = View.VISIBLE
        }
    }

    private fun addNotification() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogAddNotificationBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()

        //make transparent to default dialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        //add notification button click event
        dialogBinding.addNotificationButton.setOnClickListener {
            if (dialogBinding.imageLinkEdittext.text.toString().trim().isEmpty()) {
                dialogBinding.imageLinkEdittext.error = "Enter notification image link"
                dialogBinding.imageLinkEdittext.requestFocus()
                return@setOnClickListener
            }

            if (NetworkManager.isInternetAvailable(requireContext())) {
                LoadingDialog.setText("Adding notification...")
                LoadingDialog.show()
                KeyboardManager.hideKeyBoard(requireContext(), it)

                val notificationId = Firebase.database.reference.child("notifications").push().key.toString()

                val notificationModel = NotificationModel(notificationId, dialogBinding.imageLinkEdittext.text.toString().trim())

                Firebase.database.reference.child("notifications").child(notificationId)
                    .setValue(notificationModel).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            requireContext().showSuccessToast("Notification added")
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

    override fun onItemClick(currentItem: NotificationModel, clickedOn: String) {
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

                        Firebase.database.reference.child("notifications").child(currentItem.notification_id.toString()).removeValue()
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



class NotificationViewModelFactory(private val repository: NotificationRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = NotificationViewModel(repository) as T
}