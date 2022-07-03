package com.mm.foodmanagment.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val BASE_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY ="AAAAPLOzB_A:APA91bGiPxqYqrr2W5JtPiIhkBl2jJB93V7EBsGNaEPFte1U13w2ER17kLk_w_9o3fxqfaPfpyYowYpMKsK_lzZuSGdmtnR2JHlYag0lAuNxZ85gns1j2nApacq6AGwYU3e6xtg626qY"
    // get firebase server key from firebase project setting
    const val CONTENT_TYPE = "application/json"

    const val USERS: String = "users"
    const val MESSAGES: String = "messages"
    const val ID_NUM: String = "id_number"
    const val USER_IMAGE: String = "user_image"
    const val APP: String = "appData"
    const val USER_DATA: String = "userData"
    const val USERNAME: String = "username"
    const val USER_FULLNAME: String = "fullname"
    const val USER_PHONE_NUMBER: String = "phone_number"
    const val USER_TOKEN: String = "user_token"
    const val USER_FACULTY_NAME: String = "user_faculty_name"
    const val APP_PREFERENCES: String = "appPrefs"
    const val APP_PREFERENCES_SAVE: String = "appPrefs_save"
    const val PICK_IMAGE_REQUEST_CODE = 1
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    const val PICK_FILE_REQUEST = 3
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val PDF = 0
    const val DOCX = 1
    const val AUDIO = 2
    const val VIDEO = 3
    const val IMAGE = 4
    const val IMAGE2 = 5
    const val IMAGES = "images"
    const val UPLOADED_FILES = "Uploaded_files"
    const val FAILED = "Failed"
    const val UPLOADED = "Uploaded"
    const val UPLOADING = "Uploading..."
    const val ERROR = "Error"
    const val POSTS = "posts"
    const val ORDERS = "orders"


    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        if (uri != null) {
            return MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
        }
        return " "
    }
}