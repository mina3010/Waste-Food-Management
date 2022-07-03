package com.mm.foodmanagment.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mm.foodmanagment.R
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException

class GlideCircleLoader(private val context: Context) {

    fun loadImage(imageURI: Any, imageView: CircleImageView) {
        try {
            Glide.with(context)
                .load(Uri.parse(imageURI.toString()))
                .centerInside()
                .timeout(6000)
                .placeholder(R.drawable.profile_image)
                .error(R.drawable.profile_image)
                .into(imageView)
        } catch (e: IOException){
            e.printStackTrace()
        }
    }
}