package com.company.issadminpanel.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.company.issadminpanel.R
import es.dmoral.toasty.Toasty

fun ImageView.loadImage(resource: String?, placeholder: Int = R.drawable.image_placeholder_square) = Glide.with(this.context).load(resource).placeholder(placeholder).error(placeholder).into(this)

fun ImageView.loadImage(resource: Int?, placeholder: Int = R.drawable.image_placeholder_square) = Glide.with(this.context).load(resource).placeholder(placeholder).error(placeholder).into(this)

fun ImageView.loadImage(resource: Uri?, placeholder: Int = R.drawable.image_placeholder_square) = Glide.with(this.context).load(resource).placeholder(placeholder).error(placeholder).into(this)


fun Context.showSuccessToast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) = Toasty.success(this, message, duration, true).show()

fun Context.showErrorToast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) = Toasty.error(this, message, duration, true).show()

fun Context.showWarningToast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) = Toasty.warning(this, message, duration, true).show()

fun Context.showInfoToast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) = Toasty.info(this, message, duration, true).show()