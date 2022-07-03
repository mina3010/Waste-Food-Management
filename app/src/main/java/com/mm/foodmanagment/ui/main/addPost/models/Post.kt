package com.mm.foodmanagment.ui.main.addPost.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class Post(
    val postID: String? = null,
    val uid: String? = null,
    val username: String? = null,
    val userImg: String? = null,
    val userAddress: String? = null,
    val image: String? = null,
    val postText: String? = null,
    val imageType: String? = null,
    val timestamp :String?= null,
    val comments :Int?= null,
    val likes :String?= null
): Parcelable, Serializable