package com.company.issadminpanel.view.mapActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.databinding.FragmentEditMapBinding
import com.company.issadminpanel.model.MapModel
import com.company.issadminpanel.utils.KeyboardManager
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditMapFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentEditMapBinding

    private var mapId = ""
    private var mapTitle = ""
    private var mapLink = ""

    private lateinit var mapModel: MapModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditMapBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        mapId = arguments?.getString("MAP_ID").toString()
        mapTitle = arguments?.getString("MAP_TITLE").toString()
        mapLink = arguments?.getString("MAP_LINK").toString()

        mapModel = MapModel(mapId, mapTitle, mapLink)

        //set details
        binding.mapTitleEdittext.setText(mapTitle)
        binding.mapLinkEdittext.setText(mapLink)

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //update button click event
        binding.updateMapButton.setOnClickListener { updateMap(it) }

        return binding.root
    }

    private fun updateMap(view: View) {
        if (binding.mapTitleEdittext.text.toString().trim().isEmpty()) {
            binding.mapTitleEdittext.error = "Enter Map Title"
            binding.mapTitleEdittext.requestFocus()
            return
        }
        if (binding.mapLinkEdittext.text.toString().trim().isEmpty()) {
            binding.mapLinkEdittext.error = "Enter Google Map Link"
            binding.mapLinkEdittext.requestFocus()
            return
        }

        mapModel.map_title = binding.mapTitleEdittext.text.toString().trim()
        mapModel.map_link = binding.mapLinkEdittext.text.toString().trim()

        if (NetworkManager.isInternetAvailable(requireContext())) {
            LoadingDialog.setText("Updating...")
            LoadingDialog.show()

            Firebase.database.reference.child("maps").child(mapId)
                .setValue(mapModel).addOnCompleteListener { task ->
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