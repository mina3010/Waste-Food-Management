package com.mm.foodmanagment.utils

import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.mm.foodmanagment.R
import com.mm.foodmanagment.ui.main.postDetails.adapter.MySliderImageAdapter
import kotlinx.android.synthetic.main.activity_open_image.*
import kotlinx.android.synthetic.main.fragment_post_details.view.*

class OpenImageActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_image)

        val imgUrl = intent.getStringExtra("URL")
        val type = intent.getStringExtra("type")
        if (type == "1"){
            open_img.isVisible = false
            imageSlider.isVisible = true
            val nList = ArrayList<String>()
            val imageList = imgUrl!!.split("@@")
            for (i in imageList) {
                i.replace("@@", "")
                if (!i.isNullOrEmpty()) {
                    nList.add(i.toString())
                }

            }
            val adapter = MySliderImageAdapter()
            adapter.renewItems(nList)
            imageSlider.setSliderAdapter(adapter)
            imageSlider.isAutoCycle = false
            imageSlider.startAutoCycle()

        }
        if (imgUrl != null) {
            Glide.with(this)
                .load(Uri.parse(imgUrl))
                .fitCenter()
                .placeholder(R.drawable.load_image)
                .into(open_img)
        }
    }
}