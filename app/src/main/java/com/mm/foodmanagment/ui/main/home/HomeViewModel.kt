package com.mm.foodmanagment.ui.main.home

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mm.foodmanagment.ui.main.DashboardActivity
import com.mm.foodmanagment.ui.main.addPost.models.Post
import com.mm.foodmanagment.ui.repo.MyRepository

class HomeViewModel() : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private var repository: MyRepository? = MyRepository()
    private val postsList: MutableLiveData<ArrayList<Post>> =MutableLiveData<ArrayList<Post>>() // list to store data
    private var list: LiveData<ArrayList<Post>>? = null


    fun getPostsList(): MutableLiveData<ArrayList<Post>> {
        return postsList
    }

    fun getPosts(activityDashboard: DashboardActivity?, root: View) {
        postsList.value = repository?.getPosts(activityDashboard,root)
    }

    fun deleteProduct(id: String?) {
//        repository?.deleteProduct(id)
    }


}

