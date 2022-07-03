package com.mm.foodmanagment.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mm.foodmanagment.R
import java.io.IOException

class GlideLoader(private val context: Context) {

    fun loadImage(imageURI: Any, imageView: ImageView) {
        try {
            Glide.with(context)
                .load(Uri.parse(imageURI.toString()))
                .centerCrop()
                .timeout(6000)
                .placeholder(R.drawable.common_google_signin_btn_icon_light_normal_background)
                .error(R.drawable.common_google_signin_btn_icon_light_normal_background)
                .into(imageView)
        } catch (e: IOException){
            e.printStackTrace()
        }
    }
}