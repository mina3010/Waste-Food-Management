package com.mm.foodmanagment.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.ParseException
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.mm.foodmanagment.R
import com.mm.foodmanagment.ui.auth.model.User
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
open class BaseActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false

    lateinit var filepath: Uri


    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return df.format(Calendar.getInstance().time)
    }
    @SuppressLint("SimpleDateFormat")
    fun timeAgo(t: String?):String{
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        sdf.timeZone = TimeZone.getTimeZone(TimeZone.getDefault().toString())
        try {
            val time: Long = sdf.parse(t).time
            val now = System.currentTimeMillis()
            val ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)

            return ago.toString()
//            val prettyTime = PrettyTime(Locale.getDefault())
//            val ago2 = prettyTime.format(Date(time))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return "error time"
    }

    fun showSnackBar(message: String, errorMessage: Boolean) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)

        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.red
                )
            )
        } else {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.green
                )
            )
        }
        snackBar.show()
    }

    fun savePrefsData(t: Boolean) {
        val pref = baseContext?.getSharedPreferences(
            Constants.APP_PREFERENCES,
            MODE_PRIVATE
        )
        val editor = pref?.edit()
        editor?.putBoolean(Constants.APP_PREFERENCES_SAVE, t)
        editor?.commit()
    }

    fun checkPrefData(): Boolean? {
        val pref = baseContext?.getSharedPreferences(
            Constants.APP_PREFERENCES,
            MODE_PRIVATE
        )
        return pref?.getBoolean(Constants.APP_PREFERENCES_SAVE, false)
    }

    fun getUserData(): User? {
        val appSharedPrefs = baseContext?.getSharedPreferences(
            Constants.USER_DATA,
            MODE_PRIVATE
        )
        val gson = Gson()
        val json = appSharedPrefs?.getString(Constants.USER_DATA, "")
        return gson.fromJson(json, User::class.java)
    }
    fun saveUserData(user: User?, act: Activity){
        val pref = act.getSharedPreferences(
            Constants.USER_DATA,
            MODE_PRIVATE
        )
        val prefsEditor = pref?.edit()
        val gson = Gson()
        val json = gson.toJson(user)
        prefsEditor?.putString(Constants.USER_DATA, json)
        prefsEditor?.commit()
    }
    fun saveUserFirstName(token: String, activity: Activity) {
        val pref = activity.getSharedPreferences(
            Constants.USERNAME,
            MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putString(Constants.USERNAME, token)
        editor.commit()
    }

    fun getUserFirstName(): String? {
        val pref = getSharedPreferences(
            Constants.USERNAME,
            MODE_PRIVATE
        )
        return pref.getString(Constants.USERNAME, "").toString()
    }

    fun saveUserSecondName(token: String) {
        val pref = applicationContext.getSharedPreferences(
            Constants.USER_FACULTY_NAME,
            MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putString(Constants.USER_FACULTY_NAME, token)
        editor.commit()
    }

    fun getUserSecondName(): String? {
        val pref = applicationContext.getSharedPreferences(
            Constants.USER_FACULTY_NAME,
            MODE_PRIVATE
        )
        return pref.getString(Constants.USER_FACULTY_NAME, "").toString()
    }

    fun saveUserLastName(token: String, activity: Activity) {
        val pref = activity.applicationContext.getSharedPreferences(
            Constants.USER_FULLNAME,
            MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putString(Constants.USER_FULLNAME, token)
        editor.commit()
    }

    fun getUserLastName(activity: Activity): String? {
        val pref = activity.applicationContext.getSharedPreferences(
            Constants.USER_FULLNAME,
            MODE_PRIVATE
        )
        return pref.getString(Constants.USER_FULLNAME, "").toString()
    }

    fun saveUserPhone(token: String, activity: Activity) {
        val pref = activity.applicationContext.getSharedPreferences(
            Constants.USER_PHONE_NUMBER,
            MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putString(Constants.USER_PHONE_NUMBER, token)
        editor.commit()
    }

    fun getUserPhone(): String? {
        val pref = applicationContext.getSharedPreferences(
            Constants.USER_PHONE_NUMBER,
            MODE_PRIVATE
        )
        return pref.getString(Constants.USER_PHONE_NUMBER, "").toString()
    }

    fun saveUserID(token: String, activity: Activity) {
        val pref = activity.getSharedPreferences(
            Constants.USER_TOKEN,
            MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putString(Constants.USER_TOKEN, token)
        editor.commit()
    }

    fun getUserID(): String? {
        val pref = applicationContext.getSharedPreferences(
            Constants.USER_TOKEN,
            MODE_PRIVATE
        )
        return pref.getString(Constants.USER_TOKEN, "").toString()
    }


    fun saveUserType(type: String, activity: Activity) {
        val pref = activity.getSharedPreferences(
            Constants.USER_FULLNAME,
            MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putString(Constants.USER_FULLNAME, type)
        editor.commit()
    }

    fun getUserType(): String? {
        val pref = applicationContext.getSharedPreferences(
            Constants.USER_FULLNAME,
            MODE_PRIVATE
        )
        return pref.getString(Constants.USER_FULLNAME, "").toString()
    }

    fun saveUserIdNum(type: String, activity: Activity) {
        val pref = activity.getSharedPreferences(
            Constants.ID_NUM,
            MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putString(Constants.ID_NUM, type)
        editor.commit()
    }

    fun getUserIdNum(): String? {
        val pref = applicationContext.getSharedPreferences(
            Constants.ID_NUM,
            MODE_PRIVATE
        )
        return pref.getString(Constants.ID_NUM, "").toString()
    }

    fun saveUserImg(type: String, activity: Activity) {
        val pref = activity.getSharedPreferences(
            Constants.USER_IMAGE,
            MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putString(Constants.USER_IMAGE, type)
        editor.commit()
    }

    fun getUseImg(): String? {
        val pref = applicationContext.getSharedPreferences(
            Constants.USER_IMAGE,
            MODE_PRIVATE
        )
        return pref.getString(Constants.USER_IMAGE, "").toString()
    }

    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }

    }

    fun validateUsername(pass: String?): Boolean {
        val p = Pattern.compile("[^A-Za-z0-9]");
        return p.matcher(pass).find()
    }

    fun validatePhone(pass: String?): Boolean {
        val p = Pattern.compile("[^0-9]");
        return p.matcher(pass).find()
    }

    fun validateName(name: String?): Boolean {
        val high = Pattern.compile("[^A-Z]")
        val low = Pattern.compile("[^a-z]")
        val ar = Pattern.compile("[^ء-ي ]")
        val digitCase: Pattern = Pattern.compile("[^0-9]")

        return low.matcher(name).find() && high.matcher(name).find() && ar.matcher(name)
            .find() && digitCase.matcher(name).find()
    }

    fun validatePassword(pass: String?): Boolean {
        val upCase: Pattern = Pattern.compile("[A-Za-z]")
        val lowerCase: Pattern = Pattern.compile("[a-z]")
        val digitCase: Pattern = Pattern.compile("[0-9]")

        return lowerCase.matcher(pass).find() && upCase.matcher(pass).find() && digitCase.matcher(
            pass
        ).find()
    }

    open fun onViewCreated(view: View, savedInstanceState: Bundle?) {}

}

//
//
//    private fun getUploadResult() {
//        viewModel.state.observe(this, Observer { state ->
//            when (state) {
//                UPLOADED -> {
//                    Toast.makeText(this, getString(R.string.success_message), Toast.LENGTH_SHORT)
//                        .show()
//                    viewModel.onStateSet()
//                }
//                FAILED -> {
//                    Toast.makeText(
//                        this, getString(R.string.error_message), Toast.LENGTH_SHORT
//                    ).show()
//                    viewModel.onStateSet()
//                }
//            }
//        })
//    }
//
//    private fun initComponents() {
//
//        // ViewModel
//        val repository = MainRepository()
//        val factory =
//            MainViewModelFactory(repository)
//        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
//
//        // ClickListeners
//        binding.buttonDocx.setOnClickListener { uploadDocx() }
//        binding.buttonImage.setOnClickListener { uploadImage() }
//        binding.buttonMusic.setOnClickListener { uploadAudio() }
//        binding.buttonPdf.setOnClickListener { uploadPdf() }
//        binding.buttonVideo.setOnClickListener { uploadVideo() }
//
//        // This is used so that databinding can observe the LiveData
//        binding.lifecycleOwner = this
//        binding.viewModel = viewModel
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            when (requestCode) {
//                PDF -> uploadData(data)
//                DOCX -> uploadData(data)
//                AUDIO -> uploadData(data)
//                VIDEO -> uploadData(data)
//                IMAGE -> uploadData(data)
//            }
//        }
//    }
//
//    private fun uploadData(data: Intent?) {
//        data?.let {
//            viewModel.upload(data.data!!)
//        }
//    }
//
//    private fun uploadPdf() {
//        viewModel.clearText()
//        Intent(Intent.ACTION_GET_CONTENT).apply {
//            type = "application/pdf"
//            startActivityForResult(
//                Intent.createChooser(this, "Select a PDF to upload"),
//                PDF
//            )
//        }
//    }
//
//    private fun uploadImage() {
//        viewModel.clearText()
//        Intent(Intent.ACTION_GET_CONTENT).apply {
//            type = "image/*"
//            startActivityForResult(
//                Intent.createChooser(this, "Select an image to upload"),
//                IMAGE
//            )
//        }
//    }
//
//    private fun uploadVideo() {
//        viewModel.clearText()
//        Intent(Intent.ACTION_GET_CONTENT).apply {
//            type = "video/*"
//            startActivityForResult(
//                Intent.createChooser(this, "Select a video to upload"),
//                VIDEO
//            )
//        }
//    }
//
//    private fun uploadAudio() {
//        viewModel.clearText()
//        Intent(Intent.ACTION_GET_CONTENT).apply {
//            type = "audio/*"
//            startActivityForResult(
//                Intent.createChooser(this, "Select an audio to upload"),
//                AUDIO
//            )
//        }
//    }
//
//    private fun uploadDocx() {
//        viewModel.clearText()
//        Intent(Intent.ACTION_GET_CONTENT).apply {
//            type = "docx/*"
//            startActivityForResult(
//                Intent.createChooser(
//                    this,
//                    "Select a document (.docx) to upload"
//                ), DOCX
//            )
//        }
//    }
