package com.mm.foodmanagment.ui.main.postDetails

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mm.foodmanagment.ui.main.DashboardActivity
import com.mm.foodmanagment.ui.main.addPost.models.Comments
import com.mm.foodmanagment.ui.main.addPost.models.Post
import com.mm.foodmanagment.ui.repo.MyRepository

class PostDetailsViewModel() : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private var repository: MyRepository? = MyRepository()
    private val commentsList: MutableLiveData<ArrayList<Comments>> =MutableLiveData<ArrayList<Comments>>() // list to store data
    val postDetailsViewModel: MutableLiveData<Post?>? = null
    var postId = "0"
    var imageType = "0"
    var comments = 0
    var list: ArrayList<Comments> = ArrayList<Comments>()

    fun getOnePost(activityDashboard: DashboardActivity?, root: View) {
        repository?.getPost(activityDashboard,root,postId)
    }
    fun sendComment(
        comment: Comments,
        activity: DashboardActivity,
        root: View
    ) {
        repository?.saveCommentFirestore(comment,activity,root,comments)
    }

    fun getComments(activityDashboard: DashboardActivity?, root: View) {
        repository?.getComments(root,activityDashboard,postId)
    }
}

