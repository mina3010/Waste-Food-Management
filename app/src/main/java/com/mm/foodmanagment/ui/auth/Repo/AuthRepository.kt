package com.mm.foodmanagment.ui.auth.Repo

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.mm.foodmanagment.ui.auth.RegistrationActivity
import com.mm.foodmanagment.ui.auth.model.User
import com.mm.foodmanagment.ui.main.DashboardActivity
import com.mm.foodmanagment.utils.BaseActivity
import com.mm.foodmanagment.utils.Constants
import com.mm.foodmanagment.utils.Constants.UPLOADED_FILES
import java.lang.reflect.InvocationTargetException
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set

class AuthRepository : BaseActivity() {
    private var mUploadTask: StorageTask<*>? = null
    private var mUploadTask2: StorageTask<*>? = null
    private var activity: RegistrationActivity? = RegistrationActivity()
//    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    companion object{
        private lateinit var path: Uri
        private lateinit var path2: Uri
    }

    private var firebaseUserMutableLiveData: MutableLiveData<FirebaseUser?>? = MutableLiveData()
    private var firestoreUser: MutableLiveData<User?>? = MutableLiveData()
    private var userLoggedMutableLiveData: MutableLiveData<Boolean?>? = MutableLiveData()
    private var auth: FirebaseAuth? = FirebaseAuth.getInstance()

    fun getFirebaseUserMutableLiveData(): MutableLiveData<FirebaseUser?>? {
        if (auth!!.currentUser != null) {
            firebaseUserMutableLiveData!!.postValue(auth!!.currentUser)
        }
        return firebaseUserMutableLiveData
    }

    fun getUserLoggedMutableLiveData(): MutableLiveData<Boolean?>? {
        if (auth!!.currentUser != null) {
            firebaseUserMutableLiveData!!.postValue(auth!!.currentUser)
        }
        return userLoggedMutableLiveData
    }

    private fun register(
        user: User,
        activity: RegistrationActivity,
        url: String?,
        url2: String?,
        root: View,
        typeCheck: String
    ) {
//        val progressDialog = ProgressDialog(activity)
//        progressDialog.setTitle("Loading...")
//        progressDialog.show()
//        progressDialog.setCancelable(false)


        auth!!.createUserWithEmailAndPassword(user.email!!, user.password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseUserMutableLiveData!!.postValue(auth!!.currentUser)

                    val userData: FirebaseUser? = auth?.currentUser

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userData!!.uid)

                    val hashMap:HashMap<String,String> = HashMap()
                    hashMap["userId"] = userData.uid
                    hashMap["userName"] = user.username!!
                    hashMap["profileImage"] = url?:""

                    databaseReference.setValue(hashMap).addOnCompleteListener(this){
                        Log.d("minaa realtime"," success: ${userData.uid}\n $url, $url2")

                    }.addOnFailureListener {
                        Log.d("minaa realtime"," failure: ${it.message}")
                    }


                    Log.d("minaa reg","${typeCheck}$url, $url2")

                    when (typeCheck){
                        "0" -> {
                            saveUserFirestore(user, activity, "", "", root,userData.uid)
                        }
                        "1" -> {
                            uploadFile(user, activity,Uri.parse(url) , null, root, "1",userData.uid)
                        }
                        "2" -> {
                            uploadFile(user, activity,null,  Uri.parse(url2), root, "1",userData.uid)
                        }
                        "3" -> {
                            uploadFile(user, activity, Uri.parse(url), Uri.parse(url2), root, "1",userData.uid)
                        }
                    }

//                    if (typeCheck == "0"){
//                        saveUserFirestore(user, activity, "", "", root)
//                    }else {
//                        if (typeCheck == "1") {
//                            uploadFile(user, activity, null, Uri.parse(url2), root, "1")
//                        }else if (typeCheck == "2") {
//                            uploadFile(user, activity, Uri.parse(url), null, root, "1")
//                        }
//                        }
//                    progressDialog.dismiss()
                } else {
                    Toast.makeText(activity, task.exception!!.message, Toast.LENGTH_SHORT).show()
//                    progressDialog.dismiss()
                }
            }
    }

    private fun saveUserFirestore(
        user: User,
        activity: Activity,
        url: String,
        url2: String,
        root: View,
        authID: String
    ) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Waiting...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        val db = FirebaseFirestore.getInstance()
        try {
            val data: MutableMap<String, Any> = HashMap()
            val id: String = db.collection(Constants.USERS).document().id
            data["uid"] = id
            data["username"] = user.username!!
            data["email"] = user.email!!
            data["password"] = user.password!!
            data["phone"] = user.phone!!
            data["address"] = user.address!!
            data["otherData"] = authID
            data["userImg"] = url
            data["menu_img"] = url2
            data["type"] = user.type!!
            data["taxNum"] = user.taxNum!!
            db.collection(Constants.USERS).document(id).set(data).addOnCompleteListener {
                if (it.isComplete) {
                    if (it.isSuccessful) {
                         saveUserData(
                             User(id,user.username,user.email,user.password,user.phone,user.address,user.otherData,url,url2,user.type,user.taxNum),
                             activity
                         )
                        Toast.makeText(activity, "sign up successfully!", Toast.LENGTH_SHORT).show()
                        activity.startActivity(Intent(activity, DashboardActivity::class.java))
                        activity.finish()
                        progressDialog.dismiss()

                    } else {
                        Toast.makeText(activity, "sign up Failed!", Toast.LENGTH_SHORT).show()

                        progressDialog.dismiss()
                    }
                }
            }
        } catch (e: InvocationTargetException) {
            Toast.makeText(activity, "error: $e", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
        }

    }

    fun checkUser(
        user: User,
        activity: RegistrationActivity,
        url: String?,
        url2: String?,
        root: View,
        typeCheck: String
    ) {
        val list: ArrayList<User> = ArrayList<User>()
        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.USERS)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (!it.result?.documents.isNullOrEmpty()) {
                        for (document in it.result!!) {
                            list.add(
                                User(
                                    document.data.getValue("uid").toString(),
                                    document.data.getValue("username").toString(),
                                    document.data.getValue("email").toString(),
                                    document.data.getValue("password").toString(),
                                    document.data.getValue("phone").toString(),
                                    document.data.getValue("address").toString(),
                                    document.data.getValue("otherData").toString(),
                                    document.data.getValue("userImg").toString(),
                                    document.data.getValue("menu_img").toString(),
                                    document.data.getValue("type").toString(),
                                    document.data.getValue("taxNum").toString(),
                                ),
                            )
                        }
                    }
                    var check = false
                    var check2 = false
                    for (i in list) {
                        if (user.email == i.email) {
                            check = true
                            break
                        }
                        if (user.username == i.username) {
                            check2 = true
                            break
                        }
                    }

                    if (!check) {
                        if (!check2) {
                            register(user, activity,url,url2,root,typeCheck)
                            progressDialog.dismiss()

                        } else {
                            Toast.makeText(
                                activity,
                                "this username is already used, please change it!",
                                Toast.LENGTH_SHORT
                            ).show()
                            progressDialog.dismiss()
                        }
                    } else {
                        Toast.makeText(
                            activity,
                            "this email is already used, please change it!",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressDialog.dismiss()
                    }

                }else{ progressDialog.dismiss()}
            }
    }

    fun checkLogin(user: User, activity: Activity) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        val list: ArrayList<User> = ArrayList<User>()
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.USERS)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (!it.result?.documents.isNullOrEmpty()) {
                        for (document in it.result!!) {
                            list.add(
                                User(
                                    document.data.getValue("uid").toString(),
                                    document.data.getValue("username").toString(),
                                    document.data.getValue("email").toString(),
                                    document.data.getValue("password").toString(),
                                    document.data.getValue("phone").toString(),
                                    document.data.getValue("address").toString(),
                                    document.data.getValue("otherData").toString(),
                                    document.data.getValue("userImg").toString(),
                                    document.data.getValue("menu_img").toString(),
                                    document.data.getValue("type").toString(),
                                    document.data.getValue("taxNum").toString(),
                                ),
                            )
                        }
                    }

                    var email = ""
                    var password = ""
                    var username = ""
                    var userType = ""
                    var check = false
                    var check2 = false
                    var uid = ""
                    var userData: User = User()

                    for (i in list) {
                        if (user.email == i.email) {
                            if (user.password== i.password) {
                                check = true
                                email = i.email.toString()
                                password = i.password.toString()
                                username = i.username.toString()
                                uid = i.uid.toString()
                                userData = User(
                                    i.uid,
                                    i.username,
                                    i.email,
                                    i.password,
                                    i.phone,
                                    i.address,
                                    i.otherData,
                                    i.userImg,
                                    i.menu_img,
                                    i.type,
                                    i.taxNum
                                )

                                break
                            }
                        }
                        if (user.email == i.username) {
                            if (user.password== i.password) {
                                check2 = true
                                email = i.email.toString()
                                password = i.password.toString()
                                username = i.username.toString()
                                uid = i.uid.toString()
                                userData = User(
                                    i.uid,
                                    i.username,
                                    i.email,
                                    i.password,
                                    i.phone,
                                    i.address,
                                    i.otherData,
                                    i.userImg,
                                    i.menu_img,
                                    i.type,
                                    i.taxNum
                                )
                                break
                            }
                        }
                    }

                    if (check) {
                        Log.d("TAG","r:$username")
                        login(email, password, activity,username,userType,userData)
                        progressDialog.dismiss()
                    } else {
                        if (check2) {
                            Log.d("TAG","r:$username")
                            login(email, password, activity, username, userType,userData)
                            progressDialog.dismiss()
                        } else {
                            Toast.makeText(
                                activity,
                                "this username or email is not exisited, please try again!",
                                Toast.LENGTH_SHORT
                            ).show()
                            progressDialog.dismiss()
                        }
                    }


                }
            }
    }

    private fun login(
        email: String,
        pass: String,
        act: Activity,
        username: String,
        userType: String,
        user: User
    ) {
        val progressDialog = ProgressDialog(act)
        progressDialog.setTitle("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        auth!!.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseUserMutableLiveData!!.postValue(auth!!.currentUser)
                Log.d(ContentValues.TAG,"auth: ${user.uid}")
                activity?.saveUserData(user,act)

                act.startActivity(Intent(act, DashboardActivity::class.java))
                act.finish()
                progressDialog.dismiss()
            } else {
                Toast.makeText(activity, task.exception!!.message, Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        }
    }

    fun signOut() {
        auth!!.signOut()
        savePrefsData(false)
        userLoggedMutableLiveData!!.postValue(true)
    }


    private val firebaseStorage = FirebaseStorage.getInstance().getReference(UPLOADED_FILES)


    private fun getFileExtension(uri: Uri, activity: RegistrationActivity): String? {
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }
    private val mStorageRef = FirebaseStorage.getInstance().getReference("uploads")
    fun uploadFile(
        user: User,
        activity: RegistrationActivity,
        mUri: Uri?,
        mUri2: Uri?,
        root: View,
        actionType: String,
        authID: String,
    ) {
            if (mUri != null ) {
                Log.d("minaa","$mUri, $mUri2")
                val progressDialog = ProgressDialog(activity)
                when (actionType) {
                    "1" -> {
                        progressDialog.setTitle("loading...")
                    }
                    "2" -> {
                        progressDialog.setTitle("waiting...")
                    }
                    else -> {
                        progressDialog.setTitle("loading...")
                    }
                }
                progressDialog.show()
                progressDialog.setCancelable(false)
                val fileReference: StorageReference = mStorageRef.child(
                    System.currentTimeMillis()
                        .toString() + "." + getFileExtension(mUri, activity)
                )

                mUploadTask = fileReference.putFile(mUri)
                    .addOnSuccessListener { taskSnapshot ->
                        val uri = taskSnapshot.storage.downloadUrl
                        if (actionType == "1") {
                            while (!uri.isComplete );
                                path = uri.result!!
                                progressDialog.dismiss()
                            if (mUri2 != null && mUri2.toString() != "") {
                                uploadFile2(user, activity, mUri2, root, "1",authID)
                            }else{
                                saveUserFirestore(user, activity, path.toString()?:"", "", root, authID)
                            }
                        }
                            // updateProductFirestore(user, activity, url.toString(), root,actionType)

                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()

                    }


            } else {
                if (mUri2 != null && mUri2.toString() != "") {
                    uploadFile2(user, activity, mUri2, root, "2",authID)
                }else{
                    saveUserFirestore(user, activity, "", "", root, authID)
                }
            }

    }

    private fun uploadFile2(
        user: User,
        activity: RegistrationActivity,
        mUri: Uri?,
        root: View,
        actionType: String,
        authID: String,
    ) {
        if (mUri != null) {
            val progressDialog = ProgressDialog(activity)
            if (actionType == "1") {
                progressDialog.setTitle("loading...")
            }else if(actionType == "2"){
                progressDialog.setTitle("waiting...")
            }
            progressDialog.show()
            progressDialog.setCancelable(false)
            val fileReference: StorageReference = mStorageRef.child(
                System.currentTimeMillis()
                    .toString() + "." + getFileExtension(mUri, activity)
            )

            mUploadTask = fileReference.putFile(mUri)
                .addOnSuccessListener { taskSnapshot ->
                    val uri = taskSnapshot.storage.downloadUrl
                        while (!uri.isComplete );
                            path2 = uri.result!!
                            progressDialog.dismiss()

                    if (actionType == "2") {
                        saveUserFirestore(
                            user,
                            activity,
                            "",
                            path2.toString(),
                            root,
                            authID
                        )
                    }else{
                        saveUserFirestore(
                            user,
                            activity,
                            path.toString(),
                            path2.toString(),
                            root,
                            authID
                        )
                    }



                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()

                }


        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }

    }

}