package com.company.issadminpanel.view

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.ActivityMainBinding
import com.company.issadminpanel.databinding.DialogExitBinding

class MainActivity : AppCompatActivity() {

    //Declaring variables
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var currentFragmentId = R.id.homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        //this will run every time when any fragment changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentFragmentId = destination.id
        }
    }

    override fun onBackPressed() {
        if (currentFragmentId == R.id.homeFragment) {
            val builder = AlertDialog.Builder(this)
            val dialogBinding = DialogExitBinding.inflate(layoutInflater)

            builder.setView(dialogBinding.root)
            builder.setCancelable(true)

            val alertDialog = builder.create()

            //make transparent to default dialog
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

            dialogBinding.noButton.setOnClickListener { alertDialog.dismiss() }

            dialogBinding.yesButton.setOnClickListener {
                alertDialog.dismiss()
                finish()
            }

            alertDialog.show()
        } else {
            super.onBackPressed()
        }
    }
}