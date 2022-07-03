package com.mm.foodmanagment.ui.main.addPost

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mm.foodmanagment.ui.main.addPost.models.Image
import com.mm.foodmanagment.ui.main.addPost.models.Post
import com.mm.foodmanagment.ui.main.DashboardActivity
import com.mm.foodmanagment.ui.repo.MyRepository
import com.mm.foodmanagment.utils.Constants.ERROR
import com.mm.foodmanagment.utils.Constants.FAILED
import com.mm.foodmanagment.utils.Constants.UPLOADED
import com.mm.foodmanagment.utils.Constants.UPLOADING
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

class AddPostViewModel(repository: MyRepository) : ViewModel() {
    var state = MutableLiveData<String>()
    @SuppressLint("StaticFieldLeak")
    private var repository: MyRepository? = MyRepository()
    private var _fileUrl = MutableLiveData<String>()
    val fileUrl: LiveData<String>
        get() = _fileUrl

    private var _fileUri = MutableLiveData<String>()
    val fileUri: LiveData<String>
        get() = _fileUri

    /**
     * Method to upload a file to Firebase Storage
     * @fileUri -> File to be uploaded to Firebase Storage.
     */
    fun upload(fileUri: Uri) {
        _fileUri.value = fileUri.toString()
        _fileUrl.value = UPLOADING
        repository!!.upload(fileUri).addOnSuccessListener { it ->
            it?.let {
                val url = it.uploadSessionUri
                state.value = UPLOADED
                Log.d("ViewModel", "Your file was successful uploaded $url")
                _fileUrl.value = url.toString()
            }
        }.addOnFailureListener {
            state.value = FAILED
            _fileUrl.value = ERROR
            Log.d("ViewModel", "Upload failed error: ${it.message}")
        }
    }

    /**
     * Returns the state to empty to avoid leaks
     */
    fun onStateSet() {
        state.value = ""
    }

    /**
     * Clears the previous text after clicking a button.
     */
    fun clearText(){
        _fileUrl.value = ""
        _fileUri.value = ""
    }


    @DelicateCoroutinesApi
    suspend fun uploadFile(
        uri: Uri?,
        activity: DashboardActivity,
        post: Post,
        root: View,
        actionType: String,
        images: ArrayList<Uri>
    ) {
        when (post.imageType) {
            "0" -> {
                viewModelScope.launch {
                    repository?.savePostFirestore(
                        post,
                        activity,
                        uri.toString(),
                        root,
                        images
                    )
                }
            }
            "1" -> {
                viewModelScope.launch {
                    repository?.uploadFile(
                        uri,
                        activity,
                        post,
                        root,
                        actionType,
                        Image("", "", uri.toString()),
                        images
                    )
                }
            }
            "2" -> {
                viewModelScope.launch {
                    repository?.uploadFile(
                        uri,
                        activity,
                        post,
                        root,
                        actionType,
                        Image("", "", uri.toString()),
                        images
                    )
                }
            }
        }

    }

}

