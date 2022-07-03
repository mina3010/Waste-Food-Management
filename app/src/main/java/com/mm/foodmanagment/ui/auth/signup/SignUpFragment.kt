package com.mm.foodmanagment.ui.auth.signup

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mm.foodmanagment.R
import com.mm.foodmanagment.ui.auth.RegistrationActivity
import com.mm.foodmanagment.ui.auth.model.User
import com.mm.foodmanagment.utils.GlideLoader
import com.mm.foodmanagment.utils.hideSoftKeyboard
import com.mm.foodmanagment.utils.Constants.FAILED
import com.mm.foodmanagment.utils.Constants.IMAGE
import com.mm.foodmanagment.utils.Constants.IMAGE2
import com.mm.foodmanagment.utils.Constants.UPLOADED
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.android.synthetic.main.fragment_signup.view.*

class SignUpFragment : Fragment() {
    private var url: Uri? = null
    private var url2: Uri? = null
    private var isRest: String? = "1"
    private var viewModel: SignupViewModel? = SignupViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val root = inflater.inflate(R.layout.fragment_signup, container, false)
        val activity: RegistrationActivity? = activity as RegistrationActivity?
        checkPermission()
        getUploadResult(activity!!)

        root.removeImg.setOnClickListener {
            url = null
            root.conImgID.isVisible = false
            viewModel!!.clearText()
        }
        root.removeImgProfile.setOnClickListener {
            url2 = null
            root.conProfileImg.isVisible = false
            viewModel!!.clearText()
        }

        root.rbGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.yesBtn) {
                root.yesBtn.setBackgroundResource(R.drawable.button4)
                root.noBtn.setBackgroundResource(R.drawable.button5)
                root.yesBtn.setTextColor(Color.WHITE)
                root.noBtn.setTextColor(Color.BLACK)
                root.addID_btn.isVisible = true
//                root.line1.isVisible = true
                root.n.isVisible = true
                root.taxNumber.isVisible = true
                isRest = "1"
            }
            if (checkedId == R.id.noBtn) {
                root.noBtn.setBackgroundResource(R.drawable.button4)
                root.yesBtn.setBackgroundResource(R.drawable.button5)
                root.noBtn.setTextColor(Color.WHITE)
                root.yesBtn.setTextColor(Color.BLACK)
                root.addID_btn.isVisible = false
//                root.line1.isVisible = false
                root.n.isVisible = false
                root.taxNumber.isVisible = false
                url = null
                viewModel!!.clearText()
                isRest = "0"
            }
        }


        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().application)
        )[SignupViewModel::class.java]

        viewModel!!.getUserData()?.observe(requireActivity()
        ) { firebaseUser ->
            if (firebaseUser != null) {

            }
        }


        root.signUpBtn.setOnClickListener {
            if (checkData()) {
                    if (Patterns.EMAIL_ADDRESS.matcher(root.email.text.toString().trim()).matches()) {
                        if (
//                            activity!!.validatePassword(
//                                root.password.text.toString().trim()) &&
                            root.password.text.toString().length > 6
                        ) {
//                            if (url != null && url2 != null) {
                                viewModel!!.registerEmail(
                                    User(
                                        "",
                                        root.username.text.toString().trim(),
                                        root.email.text.toString().trim(),
                                        root.password.text.toString().trim(),
                                        root.phone.text.toString().trim(),
                                        root.address.text.toString().trim(),
                                        root.bankNum.text.toString().trim(),
                                        "", "",
                                        isRest, root.tax_number.text.toString().trim()
                                    ),
                                    activity, url,url2, root
                                )
                            Log.d("TAGBtn","$url || $url2")

                            it?.hideSoftKeyboard()

                                root.signUpBtn.isEnabled = false
                                @Suppress("DEPRECATION")
                                Handler().postDelayed(
                                    {
                                        root.signUpBtn.isEnabled = true
                                    }, 4000
                                )
//                            } else{
//                            Toast.makeText(
//                                requireContext(),
//                                "empty pictures",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
                    } else {
                        root.password.error = resources.getString(R.string.password)
                        root.password.requestFocus()
                    }
                } else {
                root.email.error = resources.getString(R.string.email)
                root.email.requestFocus()

                }
            }

        }

        root.addID_btn.setOnClickListener {
            uploadImage(1)
        }
        root.addImg_btn.setOnClickListener {
            uploadImage(2)
        }
            root.loginTxt.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_signup_to_navigation_login)
            }

            return root
        }

    private fun checkData(): Boolean {
            if (username.text.isNullOrEmpty()) {
                username.error = "empty "+resources.getString(R.string.username)
                username.requestFocus()
                return false
            }
            if (email.text.isNullOrEmpty()) {
                email.error = "empty "+resources.getString(R.string.email)
                email.requestFocus()
                return false
            }
            if (password.text.isNullOrEmpty()) {
                password.error = "empty "+resources.getString(R.string.password)
                password.requestFocus()
                return false
            }
            if (phone.text.isNullOrEmpty()) {
                phone.error = "empty "+resources.getString(R.string.mobile)
                phone.requestFocus()
                return false
            }
            if (address.text.isNullOrEmpty()) {
                address.error = "empty "+resources.getString(R.string.address)
                address.requestFocus()
                return false
            }
            if (isRest == "1"){
                if (tax_number.text.isNullOrEmpty()) {
                    tax_number.error = "empty "+resources.getString(R.string.tax_record)
                    tax_number.requestFocus()
                    return false
                }
            }
            return true
        }

    private fun uploadImage(type:Int) {
        viewModel!!.clearText()
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (type == 1) {
            startActivityForResult(intent, IMAGE)
        }else{
            startActivityForResult(intent, IMAGE2)
        }
    }

    private fun getUploadResult(activity: RegistrationActivity) {
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
//            if ( url != null) {
//                    Log.d(TAG, "url : ${url.toString()}")
            when (requestCode) {
                // IMAGE -> uploadData(data)
                IMAGE -> {
                    url = data?.data
                    conImgID.isVisible = true
                    GlideLoader(requireContext()).loadImage(url!!, imageID)
                    Log.d(TAG, "im1 url : ${url.toString()}")
                    // Log.d(TAG, "im1 url2 : ${url2.toString()}")
                }
                IMAGE2 -> {
                    url2 = data?.data
                    conProfileImg.isVisible = true
                    GlideLoader(requireContext()).loadImage(url2!!, imageProfile)
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
                IMAGE
            )
        }
    }
}
