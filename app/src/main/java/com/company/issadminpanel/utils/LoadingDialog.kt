package com.company.issadminpanel.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.TextView
import com.company.issadminpanel.R


object LoadingDialog {

    private lateinit var loadingAlertDialog: AlertDialog
    private var loadingText: TextView? = null

    fun init(context: Context?) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_loading_layout, null)

        loadingText = view.findViewById(R.id.loadingTextTextview)

        builder.setView(view)
        builder.setCancelable(false)
        loadingAlertDialog = builder.create()

        //make transparent to default dialog
        loadingAlertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
    }


    fun show() {
        loadingAlertDialog.show()
    }

    fun dismiss() {
        loadingAlertDialog.dismiss()
    }

    fun setText(text: String) {
        loadingText?.text = text
    }

}