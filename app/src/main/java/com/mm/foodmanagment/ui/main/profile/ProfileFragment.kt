package com.mm.foodmanagment.ui.main.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mm.foodmanagment.R
import com.mm.foodmanagment.ui.auth.RegistrationActivity
import com.mm.foodmanagment.ui.auth.model.User
import com.mm.foodmanagment.ui.main.DashboardActivity
import com.mm.foodmanagment.utils.Constants
import com.mm.foodmanagment.utils.GlideCircleLoader
import com.mm.foodmanagment.utils.GlideLoader
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.userImg
import kotlinx.android.synthetic.main.fragment_profile.view.username
import kotlinx.android.synthetic.main.fragment_signup.username


class ProfileFragment : Fragment() {
    private var viewModel: ProfileViewModel? = ProfileViewModel()
    private var url: Uri? = null
    private var url2: Uri? = null
    private var checkProfile = 0
    private var checkMenu = 0
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        val activityDashboard: DashboardActivity? = activity as DashboardActivity?

        getArgs(root)
        userObservers(activityDashboard,root)
        root.run {
            Log.d("mina1","${viewModel?.userName}, ${viewModel?.uId}")
            if (viewModel?.uId != "0") {
                sendMsg.isVisible = true
            }
//            if (viewModel?.userName != "admin") {
//                sendMsg.isVisible = true
//            }
            activityDashboard?.getUserData().let {
                if (it?.type == "3"){
                    GlideCircleLoader(requireContext()).loadImage(it.userImg.toString(), root.userImg)
                }else{
                    GlideCircleLoader(requireContext()).loadImage(it?.userImg.toString(), root.userImg)
                }
                username.text = it?.username.apply { username.isEnabled = false }
                userPhone.setText(it?.phone).apply { userPhone.isEnabled = false }
                uAddress.setText(it?.address).apply { uAddress.isEnabled = false }
                if (!it?.taxNum.isNullOrEmpty()) {
                    tax_number.setText(it?.taxNum).apply { tax_number.isEnabled = false }
                }else{
                    tv3.isVisible = false
                    tax_number.isVisible = false
                }
                uEmail.setText(it?.email).apply { uEmail.isEnabled = false }

                if (it?.type == "1" || it?.type == "3") {
                    if (!it.menu_img.toString().isNullOrEmpty()) {
                        root.menu_title.isVisible = true
                        root.menuImg.isVisible = true
                        GlideLoader(activityDashboard!!.applicationContext).loadImage(
                            it.menu_img.toString(),
                            menuImg
                        )
                    }
                }
            }


            editProfile.setOnClickListener {
                userObservers(activityDashboard,root)
                checkProfile = 1
                checkMenu = 1
                update_btn.isVisible = true
                tv0.isVisible = true
                tv0.text = activityDashboard?.getUserData()?.email.toString()
                tv.isVisible = false
                uEmail.isVisible = false
                pass.isVisible = false
                userPhone.isEnabled = true
                uAddress.isEnabled = true
                tax_number.isEnabled = true
                GlideCircleLoader(requireContext()).loadImage(resources.getDrawable(R.drawable.ic_baseline_edit_24), root.userImg)
                activityDashboard?.getUserData().let {
                    Log.d("mina","${it?.type}")
                    if (it?.type == "3") {
                        usernameEdit.isVisible = false
                        tv0.isVisible = false
                        username.isVisible = true
                        userImg.isEnabled = false
                        GlideCircleLoader(requireContext()).loadImage(it.userImg.toString(), root.userImg)
                    }else if (it?.type == "1") {
                        if (!it.menu_img.toString().isNullOrEmpty()) {
                            root.menu_title.isVisible = true
                            root.menuImg.isVisible = true
                            GlideLoader(activityDashboard!!.applicationContext).loadImage(
                                resources.getDrawable(R.drawable.ic_baseline_edit_24),
                                menuImg
                            )
                        }
                    }
                }
            }

            userImg.setOnClickListener {
                if (checkProfile == 1){
                    uploadImage(1)
                }
            }
            menuImg.setOnClickListener {
                if (checkMenu == 1){
                    uploadImage(2)
                }
            }

            update_btn.setOnClickListener {
                Log.d("minaaaaaaaaaaaaaa","$url,$url2")
                // if is user image will be update
               if (url != null){
                   // if is user image and menu image will be update
                   if (url2 != null){
                       //check data is not empty
                           updateProfile(activityDashboard,root,
                               User(
                               activityDashboard?.getUserData()?.uid,
                                   username.text.toString(),
                                   uEmail.text.toString(),
                                   activityDashboard?.getUserData()?.password,
                                   userPhone.text.toString(),
                                   uAddress.text.toString(),
                                   activityDashboard?.getUserData()?.otherData,
                                   url.toString(),
                                   url2.toString(),
                                   activityDashboard?.getUserData()?.type,
                                   tax_number.text.toString(),
                           ),"updateProfileAndMenu_1")
                   }
                   else{
                       //profile only
                       updateProfile(activityDashboard,root,
                           User(
                               activityDashboard?.getUserData()?.uid,
                               username.text.toString(),
                               uEmail.text.toString(),
                               activityDashboard?.getUserData()?.password,
                               userPhone.text.toString(),
                               uAddress.text.toString(),
                               activityDashboard?.getUserData()?.otherData,
                               url.toString(),
                               activityDashboard?.getUserData()?.menu_img,
                               activityDashboard?.getUserData()?.type,
                               tax_number.text.toString(),
                           ),"updateProfile_2")
                   }
               }else{
                   if (url2 != null){
                       //check data is not empty
//                       if (checkData()){
                           updateProfile(activityDashboard,root,
                               User(
                                   activityDashboard?.getUserData()?.uid,
                                   username.text.toString(),
                                   uEmail.text.toString(),
                                   activityDashboard?.getUserData()?.password,
                                   userPhone.text.toString(),
                                   uAddress.text.toString(),
                                   activityDashboard?.getUserData()?.otherData,
                                   activityDashboard?.getUserData()?.userImg,
                                   url2.toString(),
                                   activityDashboard?.getUserData()?.type,
                                   tax_number.text.toString(),
                               ),"updateMenu_3")
//                       }
                   }
                   else{
                       updateProfile(activityDashboard,root,
                           User(
                               activityDashboard?.getUserData()?.uid,
                               username.text.toString(),
                               uEmail.text.toString(),
                               activityDashboard?.getUserData()?.password,
                               userPhone.text.toString(),
                               uAddress.text.toString(),
                               activityDashboard?.getUserData()?.otherData,
                               activityDashboard?.getUserData()?.userImg,
                               activityDashboard?.getUserData()?.menu_img,
                               activityDashboard?.getUserData()?.type,
                               tax_number.text.toString(),
                           ),"updateNoImg_4")
                   }
               }
                userObservers(activityDashboard,root)
            }

            sendMsg.setOnClickListener {
                val i = Intent(requireContext(), ChatActivity::class.java)
                i.putExtra("userId", viewModel?.userId)
                i.putExtra("userName", viewModel?.userName)
                requireContext().startActivity(i)
            }

            if (viewModel?.uId != activityDashboard?.getUserData()?.uid  && viewModel?.uId != "0"){
                root.editProfile.isVisible = false
            }else{
                root.sendMsg.isVisible = false
            }
        }
        return root
    }

    private fun getArgs(root: View?) {
        viewModel?.run {
            uId = arguments?.getString("uid") ?: "0"
            userId = arguments?.getString("userId") ?: "0"
            userName = arguments?.getString("userName") ?: "0"
            Log.d("mino","$uId")
        }
    }

    private fun userObservers(activityDashboard: DashboardActivity?, root: View) {
        viewModel!!.getUserDetails(activityDashboard,root)
    }

    private fun updateProfile(
        activityDashboard: DashboardActivity?,
        root: View,
        user: User,
        type: String
    ){
        viewModel!!.updateUser(activityDashboard,root,user,type)
    }

    private fun checkData(): Boolean {
//        if (usernameEdit.text.isNullOrEmpty()) {
//            usernameEdit.error = "empty "+resources.getString(R.string.username)
//            usernameEdit.requestFocus()
//            return false
//        }
        if (uEmail.text.isNullOrEmpty()) {
            uEmail.error = "empty "+resources.getString(R.string.email)
            uEmail.requestFocus()
            return false
        }
        if (tax_number.text.isNullOrEmpty()) {
            tax_number.error = "empty "+resources.getString(R.string.tax_record)
            tax_number.requestFocus()
            return false
        }
        if (userPhone.text.isNullOrEmpty()) {
            userPhone.error = "empty "+resources.getString(R.string.mobile)
            userPhone.requestFocus()
            return false
        }
        if (uAddress.text.isNullOrEmpty()) {
            uAddress.error = "empty "+resources.getString(R.string.address)
            uAddress.requestFocus()
            return false
        }
        return true
    }

    private fun uploadImage(type:Int) {
        viewModel!!.clearText()
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (type == 1) {
            startActivityForResult(intent, Constants.IMAGE)
        }else{
            startActivityForResult(intent, Constants.IMAGE2)
        }
    }

    private fun getUploadResult(activity: RegistrationActivity) {
        viewModel!!.state.observe(activity, Observer { state ->
            when (state) {
                Constants.UPLOADED -> {
                    Toast.makeText(
                        activity,
                        getString(R.string.success_message),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    viewModel!!.onStateSet()
                }
                Constants.FAILED -> {
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
//            if ( url != null) {
//                    Log.d(TAG, "url : ${url.toString()}")
            when (requestCode) {
                // IMAGE -> uploadData(data)
                Constants.IMAGE -> {
                    url = data?.data
                    GlideLoader(requireContext()).loadImage(url!!, userImg)
                    Log.d(TAG, "im1 url : ${url.toString()}")
                    // Log.d(TAG, "im1 url2 : ${url2.toString()}")
                }
                Constants.IMAGE2 -> {
                    url2 = data?.data
                    GlideLoader(requireContext()).loadImage(url2!!, menuImg)
                    Log.d(TAG, "im2 url : ${url2.toString()}")
                    //  Log.d(TAG, "im2 url2 : ${url2.toString()}")
                }

//            }

            }
        }

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
                Constants.IMAGE
            )
        }
    }



}
