package com.mm.foodmanagment.ui.main.profile.chat

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mm.foodmanagment.ui.main.DashboardActivity
import com.mm.foodmanagment.ui.main.addPost.models.Post
import com.mm.foodmanagment.ui.main.profile.ChatActivity
import com.mm.foodmanagment.ui.main.profile.chat.model.Message
import com.mm.foodmanagment.ui.repo.MyRepository

class ChatViewModel() : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private var repository: MyRepository? = MyRepository()
    private val postsList: MutableLiveData<ArrayList<Post>> =MutableLiveData<ArrayList<Post>>() // list to store data
    private var list: LiveData<ArrayList<Post>>? = null


    fun getChat(): MutableLiveData<ArrayList<Post>> {
        return postsList
    }

    fun getPosts(activityDashboard: DashboardActivity?, root: View) {
        postsList.value = repository?.getPosts(activityDashboard,root)
    }

    fun sendMsg(activityChat: ChatActivity?,msg:Message?){
        repository?.sendMessage(activityChat, msg)
    }
    fun getMessages(activityChat: ChatActivity?, receiverId: String){
        repository?.getMessages(activityChat,receiverId)
    }
    fun deleteProduct(id: String?) {
//        repository?.deleteProduct(id)
    }


}

