package com.company.issadminpanel.view.mapActivity

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
import com.company.issadminpanel.adapter.MapAdapter
import com.company.issadminpanel.databinding.DialogAddMapBinding
import com.company.issadminpanel.databinding.FragmentMapBinding
import com.company.issadminpanel.interfaces.MapItemClickListener
import com.company.issadminpanel.model.MapModel
import com.company.issadminpanel.repository.MapRepository
import com.company.issadminpanel.utils.KeyboardManager
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.company.issadminpanel.view_model.MapViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MapFragment : Fragment(), MapItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentMapBinding

    private lateinit var repository: MapRepository
    private lateinit var viewModel: MapViewModel

    private var mapList = ArrayList<MapModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        repository = MapRepository()
        viewModel = ViewModelProvider(this, MapViewModelFactory(repository))[MapViewModel::class.java]

        //request for data
        viewModel.requestMapList()

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.mapRecyclerview.adapter = MapAdapter(mapList,  this@MapFragment)

        //add icon click event
        binding.addMapIcon.setOnClickListener { addMap() }

        return binding.root
    }

    private fun observerList() {
        viewModel.mapListLiveData.observe(viewLifecycleOwner) {
            mapList.clear()
            if (it != null) {
                mapList.addAll(it)

                binding.mapRecyclerview.adapter?.notifyDataSetChanged()

                binding.noMapAvailableTextview.visibility = View.GONE
                binding.mapRecyclerview.visibility = View.VISIBLE
            } else {
                binding.mapRecyclerview.visibility = View.GONE
                binding.noMapAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
            binding.addMapIcon.visibility = View.VISIBLE
        }
    }

    private fun addMap() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogAddMapBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()

        //make transparent to default dialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        //add map button click event
        dialogBinding.addMapButton.setOnClickListener {
            if (dialogBinding.mapTitleEdittext.text.toString().trim().isEmpty()) {
                dialogBinding.mapTitleEdittext.error = "Enter map title"
                dialogBinding.mapTitleEdittext.requestFocus()
                return@setOnClickListener
            }
            if (dialogBinding.mapLinkEdittext.text.toString().trim().isEmpty()) {
                dialogBinding.mapLinkEdittext.error = "Enter google map link"
                dialogBinding.mapLinkEdittext.requestFocus()
                return@setOnClickListener
            }

            if (NetworkManager.isInternetAvailable(requireContext())) {
                LoadingDialog.setText("Adding map...")
                LoadingDialog.show()
                KeyboardManager.hideKeyBoard(requireContext(), it)

                val mapId = Firebase.database.reference.child("maps").push().key.toString()

                val mapModel = MapModel(mapId, dialogBinding.mapTitleEdittext.text.toString().trim(), dialogBinding.mapLinkEdittext.text.toString().trim())

                Firebase.database.reference.child("maps").child(mapId)
                    .setValue(mapModel).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            requireContext().showSuccessToast("Map added")
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

    override fun onItemClick(currentMap: MapModel, clickedOn: String) {
        when(clickedOn) {
            "MAIN_ITEM" -> {
                val bundle = bundleOf(
                    "MAP_ID" to currentMap.map_id,
                    "MAP_TITLE" to currentMap.map_title,
                    "MAP_LINK" to currentMap.map_link
                )
                findNavController().navigate(R.id. action_mapFragment_to_editMapFragment, bundle)
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

                        Firebase.database.reference.child("maps").child(currentMap.map_id.toString()).removeValue()
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



class MapViewModelFactory(private val repository: MapRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = MapViewModel(repository) as T
}