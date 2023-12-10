package com.company.issadminpanel.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.adapter.HomeMenuAdapter
import com.company.issadminpanel.databinding.FragmentHomeBinding
import com.company.issadminpanel.interfaces.HomeMenuItemClickListener
import com.company.issadminpanel.model.HomeMenuModel
import com.company.issadminpanel.repository.HomeRepository
import com.company.issadminpanel.utils.SharedPref
import com.company.issadminpanel.utils.loadImage
import com.company.issadminpanel.view_model.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment(), HomeMenuItemClickListener {
    //Declaring variables
    private lateinit var binding: FragmentHomeBinding

    private lateinit var repository: HomeRepository
    private lateinit var viewModel: HomeViewModel

    private var homeMenuList = ArrayList<HomeMenuModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())

        //set details
        updateView()

        repository = HomeRepository()
        viewModel = ViewModelProvider(this, HomeViewModelFactory(repository))[HomeViewModel::class.java]

        //request for data
        viewModel.requestUserDetails(FirebaseAuth.getInstance().currentUser!!.uid)

        observerList()

        loadMenu()

        //set recyclerview adapter
        binding.homeMenuRecyclerview.adapter = HomeMenuAdapter(homeMenuList, this)

        return binding.root
    }

    private fun observerList() {
        viewModel.userDetailsLiveData.observe(viewLifecycleOwner) {
            it?.let {
                if (it.profile_pic_url != null) {
                    binding.profilePicImageview.loadImage(it.profile_pic_url)
                    SharedPref.write("PROFILE_PIC_URL", it.profile_pic_url!!)
                } else {
                    binding.profilePicImageview.loadImage(R.drawable.user_profile_pic_placeholder_image)
                }
                binding.userNameTextview.text = it.name
                SharedPref.write("USER_NAME", it.name.toString())
                SharedPref.write("USER_TYPE", it.user_type.toString())
                SharedPref.write("USER_ID", it.user_id.toString())
                loadMenu()
                binding.homeMenuRecyclerview.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun loadMenu() {
        homeMenuList.clear()
        when(SharedPref.read("USER_TYPE", "")) {
            "ADMIN" -> {
                homeMenuList.add(HomeMenuModel(R.drawable.menu_profile_icon, "Profile"))
                homeMenuList.add(HomeMenuModel(R.drawable.menu_add_user_icon, "Register"))
                homeMenuList.add(HomeMenuModel(R.drawable.menu_user_management_icon, "Users"))
                homeMenuList.add(HomeMenuModel(R.drawable.menu_map_icon, "Map"))
                homeMenuList.add(HomeMenuModel(R.drawable.menu_service_icon, "Services"))
                homeMenuList.add(HomeMenuModel(R.drawable.menu_facilities_icon, "Facilities"))
                homeMenuList.add(HomeMenuModel(R.drawable.menu_faq_icon, "FAQ"))
                homeMenuList.add(HomeMenuModel(R.drawable.menu_notification_icon, "Notification"))
                homeMenuList.add(HomeMenuModel(R.drawable.menu_payment_management_icon, "Payments"))
                homeMenuList.add(HomeMenuModel(R.drawable.menu_rating_icon, "Ratings"))
            }
            "LECTURER" -> {
                homeMenuList.add(HomeMenuModel(R.drawable.menu_profile_icon, "Profile"))
                homeMenuList.add(HomeMenuModel(R.drawable.menu_appointment_icon, "Appointment"))
                homeMenuList.add(HomeMenuModel(R.drawable.menu_notification_icon, "Notification"))
            }
            "DOCTOR" -> {
                homeMenuList.add(HomeMenuModel(R.drawable.menu_profile_icon, "Profile"))
                homeMenuList.add(HomeMenuModel(R.drawable.menu_chat_icon, "Chat"))
                homeMenuList.add(HomeMenuModel(R.drawable.menu_notification_icon, "Notification"))
                homeMenuList.add(HomeMenuModel(R.drawable.menu_report_icon, "Report"))
            }
        }
    }

    private fun updateView() {
        if (SharedPref.read("PROFILE_PIC_URL", "") == "") {
            binding.profilePicImageview.loadImage(R.drawable.user_profile_pic_placeholder_image)
        } else {
            binding.profilePicImageview.loadImage(SharedPref.read("PROFILE_PIC_URL", ""))
        }
        binding.userNameTextview.text = SharedPref.read("USER_NAME", "")
    }

    override fun onItemClick(currentItem: HomeMenuModel) {
        when(currentItem.menu_title) {
            "Profile" -> findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            "Register" -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(R.string.app_name)
                builder.setIcon(R.mipmap.ic_launcher)
                builder.setMessage("Do you really want to logout and register new account")
                builder.setPositiveButton("Yes") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    findNavController().navigate(R.id.action_homeFragment_to_registerFragment)
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

                val alertDialog = builder.create()
                alertDialog.show()
            }
            "Users" -> { findNavController().navigate(R.id.action_homeFragment_to_usersFragment) }
            "Map" -> findNavController().navigate(R.id.action_homeFragment_to_mapFragment)
            "Services" -> findNavController().navigate(R.id.action_homeFragment_to_servicesFragment)
            "Facilities" -> findNavController().navigate(R.id.action_homeFragment_to_facilitiesFragment)
            "Appointment" -> findNavController().navigate(R.id.action_homeFragment_to_appointmentsFragment)
            "FAQ" -> findNavController().navigate(R.id.action_homeFragment_to_faqFragment)
            "Notification" -> findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
            "Payments" -> findNavController().navigate(R.id.action_homeFragment_to_studentListFragment)
            "Ratings" -> findNavController().navigate(R.id.action_homeFragment_to_reviewRatingFragment)
            "Chat" -> findNavController().navigate(R.id.action_homeFragment_to_chatListFragment)
            "Report" -> findNavController().navigate(R.id.action_homeFragment_to_reportFragment)
        }
    }
}



class HomeViewModelFactory(private val repository: HomeRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(repository) as T
}