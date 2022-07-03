package com.mm.foodmanagment.ui.main.profile

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mm.foodmanagment.ui.auth.Repo.AuthRepository
import com.mm.foodmanagment.ui.auth.model.User
import com.mm.foodmanagment.ui.main.DashboardActivity
import com.mm.foodmanagment.ui.main.addPost.models.Post
import com.mm.foodmanagment.ui.repo.MyRepository

class ProfileViewModel() : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private var repository: MyRepository = MyRepository()

    @SuppressLint("StaticFieldLeak")
    private var authRepository: AuthRepository = AuthRepository()
    private val postsList: MutableLiveData<ArrayList<Post>> =
        MutableLiveData<ArrayList<Post>>() // list to store data
    private var list: LiveData<ArrayList<Post>>? = null
    var uId = ""
    var userId = ""
    var userName = ""
    var state = MutableLiveData<String>()
    private var _fileUrl = MutableLiveData<String>()
    private var _2fileUrl = MutableLiveData<String>()

    fun onStateSet() {
        state.value = ""
    }

    /**
     * Clears the previous text after clicking a button.
     */
    fun clearText() {
        _fileUrl.value = ""
        _2fileUrl.value = ""
    }

    fun getUserDetails(activityDashboard: DashboardActivity?, root: View) {
        Log.d("minoo", "$uId")
        repository.getUser(activityDashboard, root, uId)
    }

     fun updateUser(
        activityDashboard: DashboardActivity?,
        root: View,
        user: User,
        type: String
    ) {
        Log.d("minoo update", "${type}")
//        authRepository.updateUser(activityDashboard,root,user,uId)
        when (type) {
            "updateProfileAndMenu_1" -> { repository.uploadImg(
                    user,
                    activityDashboard!!,
                    Uri.parse(user.userImg),
                    Uri.parse(user.menu_img),
                    root, "1", user.otherData.toString()
                )
            }
            "updateProfile_2" -> { repository.uploadImg(
                user,
                activityDashboard!!,
                Uri.parse(user.userImg),
                null,
                root, "1", user.otherData.toString()
            )
            }
            "updateMenu_3" -> { repository.uploadImg(
                user,
                activityDashboard!!,
                null,
                Uri.parse(user.menu_img),
                root, "1", user.otherData.toString()
            )
            }
            "updateNoImg_4" -> {
                repository.updateUser(activityDashboard, root, user, user.uid.toString(), null, null)
            }
        }
    }

    fun getPostsList(): MutableLiveData<ArrayList<Post>> {
        return postsList
    }

    fun getPosts(activityDashboard: DashboardActivity?, root: View) {
        postsList.value = repository?.getPosts(activityDashboard, root)
    }

    fun deleteProduct(id: String?) {
//        repository?.deleteProduct(id)
    }


}

