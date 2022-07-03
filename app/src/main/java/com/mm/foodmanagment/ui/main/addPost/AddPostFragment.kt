package com.mm.foodmanagment.ui.main.addPost

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.mm.foodmanagment.ui.main.addPost.models.Image
import com.mm.foodmanagment.ui.main.addPost.models.Post
import com.mm.foodmanagment.R
import com.mm.foodmanagment.ui.main.DashboardActivity
import com.mm.foodmanagment.ui.main.home.adapter.ImagesAdapter
import com.mm.foodmanagment.ui.repo.MyRepository
import com.mm.foodmanagment.utils.Constants.FAILED
import com.mm.foodmanagment.utils.Constants.IMAGE
import com.mm.foodmanagment.utils.Constants.UPLOADED
import kotlinx.android.synthetic.main.fragment_add_post.*
import kotlinx.android.synthetic.main.fragment_add_post.view.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList


@DelicateCoroutinesApi
class AddPostFragment : Fragment() {
    private var viewModel: AddPostViewModel? = AddPostViewModel(MyRepository())

    private var url: Uri? = null
    private var post: Post = Post()
    private val imageList = ArrayList<Uri>()
    private var imageListSaved: String? = ""
    private val images = ArrayList<Image>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_post, container, false)
        val activity: DashboardActivity? = activity as DashboardActivity?
        checkPermission()
        getUploadResult(activity!!)

        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        root.back.setOnClickListener { activity?.onBackPressed() }

        root.imagesRV.layoutManager = GridLayoutManager(requireContext(), 3)

//        post = arguments?.getSerializable("post") as Post

//        root.cakeName_ed.setText(post.name)
//        root.price_ed.setText(post.details)
//        root.cakeDescription_ed.setText(post.description)
        if (!post.postID.isNullOrEmpty()) {
            root.linear.isVisible = false
            root.itemName.text = "Update Product"
        }


        root.selectImg.setOnClickListener {
            imageList.clear()
            imageListSaved = ""
            images.clear()
            uploadImage()
        }


        Log.d("TAG", "date ${Date().toString()}")

        root.addPost_btn.setOnClickListener {
            var type = "0"
            for (i in imageList) {
                images.add(Image("", post.postID, i.toString()))
            }
            if (!root.postBody_txt.text.isNullOrEmpty()) {
                Log.d("minoo", "${imageList.size}")
                if (imageList.size != 0) {
                    //add post
                    if (imageList.size == 1) {
                        runBlocking {
                            root.addPost_btn.isEnabled = false
                            root.loading.isVisible = true
                            viewModel!!.uploadFile(
                                imageList[0], activity, Post(
                                    "", "",
                                    "",
                                    "",
                                    "",
                                    imageListSaved, root.postBody_txt.text.toString(), "1",
                                    activity.getCurrentDate(), 0, activity.getUserData()?.otherData
                                ), root, "1", imageList
                            )
                        }
                    } else if (imageList.size > 1) {
                        runBlocking {
                            root.addPost_btn.isEnabled = false
                            root.loading.isVisible = true
                        viewModel!!.uploadFile(
                            imageList[0], activity, Post(
                                "", "",
                                "",
                                "",
                                "",
                                imageListSaved, root.postBody_txt.text.toString(), "2",
                                activity.getCurrentDate(), 0, activity.getUserData()?.otherData
                            ), root, "1", imageList
                        )
                    }}
                } else {
                    runBlocking {
                        root.addPost_btn.isEnabled = false
                        root.loading.isVisible = true
                        viewModel!!.uploadFile(
                            null, activity, Post(
                                "", "",
                                "",
                                activity.getUserData()?.userImg,
                                "",
                                "", root.postBody_txt.text.toString(), "0",
                                activity.getCurrentDate(), 0, activity.getUserData()?.otherData
                            ), root, "1", imageList
                        )
                    }
                    Log.d("TAG", "${imageList.size} || ${images.size}")
                }
            }

        }
        return root
    }


    private fun getUploadResult(activity: DashboardActivity) {
        viewModel!!.state.observe(activity, Observer { state ->
            when (state) {
                UPLOADED -> {
                    Toast.makeText(
                        activity,
                        getString(R.string.success_message),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    viewModel!!.onStateSet()
                }
                FAILED -> {
                    Toast.makeText(
                        activity, getString(R.string.error_message), Toast.LENGTH_SHORT
                    ).show()
                    viewModel!!.onStateSet()
                }
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            url = data!!.data
            if (data!!.clipData != null) {
                //imagesRV.isVisible = true
                val count = data.clipData!!.itemCount
                var currentImageSelect = 0

                while (currentImageSelect < count) {
                    val imageUri = data.clipData!!.getItemAt(currentImageSelect).uri
                    val imageString = "${data.clipData!!.getItemAt(currentImageSelect).uri}@@"
                    imageList.add(imageUri)
                    imageListSaved += imageString
                    currentImageSelect += 1

                }

            } else {
                if (url != null) {
                    Log.d(TAG, "url : ${url.toString()}")
                    imageList.add(url!!)
                    imageListSaved += "${url}"
                    imagesRV.isVisible = true
                }
            }
            when (requestCode) {
                // IMAGE -> uploadData(data)
                IMAGE -> {
                    val adapter = ImagesAdapter(
                        activity?.applicationContext!!,
                        imageList,
                    )
                    imagesRV.adapter = adapter
                    imagesRV.isVisible = true
                    adapter.notifyDataSetChanged()
                }
            }
        }
//        }

    }


    private fun uploadImage() {
        viewModel!!.clearText()
        //we will pick images
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, IMAGE)
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                context?.applicationContext!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                IMAGE
            )
        }
    }

}