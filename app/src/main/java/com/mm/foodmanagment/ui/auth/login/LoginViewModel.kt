package com.mm.foodmanagment.ui.auth.login

import android.app.Activity
import android.net.Uri
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.mm.foodmanagment.ui.auth.RegistrationActivity
import com.mm.foodmanagment.ui.auth.Repo.AuthRepository
import com.mm.foodmanagment.ui.auth.model.User

class LoginViewModel : ViewModel() {
    var state = MutableLiveData<String>()
    private var _fileUrl = MutableLiveData<String>()
    private var _2fileUrl = MutableLiveData<String>()
    private var repository: AuthRepository? = AuthRepository()
    private var userData: MutableLiveData<FirebaseUser?>? = repository?.getFirebaseUserMutableLiveData()
    private var loggedStatus: MutableLiveData<Boolean?>? = repository?.getUserLoggedMutableLiveData()

    fun getUserData(): MutableLiveData<FirebaseUser?>? {
        return userData
    }

    fun getLoggedStatus(): MutableLiveData<Boolean?>? {
        return loggedStatus
    }


    fun loginEmail(email: String?, pass: String?,activity:Activity) {
        repository?.checkLogin(User( "","",email, pass,"",""), activity)
    }


    fun onStateSet() {
        state.value = ""
    }

    /**
     * Clears the previous text after clicking a button.
     */
    fun clearText(){
        _fileUrl.value = ""
        _2fileUrl.value = ""
    }


    fun uploadFile(
        uri: Uri,
        activity: RegistrationActivity,
        user: User,
        root: View,
        actionType: String,
    ) {
//        repository?.saveProductFirestore(
//            user,
//            activity,
//            uri.toString(),
//            root,
//        )

    }

}