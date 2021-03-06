package com.mm.foodmanagment.ui.main.users

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mm.foodmanagment.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mm.foodmanagment.ui.main.profile.ProfileFragment
import com.mm.foodmanagment.ui.main.users.adapter.UserAdapter
import com.mm.foodmanagment.ui.main.users.model.UserData
import com.mm.foodmanagment.ui.main.users.service.FirebaseService
import com.mm.foodmanagment.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_users.*
import java.io.File

class UsersActivity : BaseActivity() {
    var userList = ArrayList<UserData>()
    private lateinit var auth:FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var uid:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_users)
        auth= FirebaseAuth.getInstance()
        uid=auth.currentUser?.uid.toString()
        getuserprofile()

        FirebaseService.sharedPref = getSharedPreferences("sharedPref",Context.MODE_PRIVATE)


        userRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        imgBack.setOnClickListener {
            onBackPressed()
        }

//        imgProfile.setOnClickListener {
//
////            val fragment = ProfileFragment()
////            val arguments = Bundle()
////            arguments.putString("uid", document.data?.getValue("uid").toString() ?: "0")
////            fragment.arguments = arguments
////            supportFragmentManager.beginTransaction().replace(R.id.navigation_home,fragment).commit()
//            val intent = Intent(
//                this@UsersActivity,
//                ProfileActivity::class.java
//            )
//            startActivity(intent)
//        }
        getUsersList()
    }

    private fun getuserprofile() {
        storageReference=FirebaseStorage.getInstance().reference.child("image/$uid")
        val localFile= File.createTempFile("tempIMage","jpg")
        storageReference.getFile(localFile).addOnSuccessListener {

            val bitmap=BitmapFactory.decodeFile(localFile.absolutePath)
            imgProfile.setImageBitmap(bitmap)

        }
    }

    fun getUsersList() {
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        var userid = firebase.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")


        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users")


        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val currentUser = snapshot.getValue(UserData::class.java)
                if (getUserData()?.type == "3"){
                    imgProfile.setImageResource(R.drawable.logo_finalll)
                }else {
                    Glide.with(this@UsersActivity).load(getUserData()?.userImg).into(imgProfile)
                }

                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(UserData::class.java)

                    if (!user!!.userId.equals(firebase.uid)) {

                        userList.add(user)
                    }
                }

                val userAdapter = UserAdapter(this@UsersActivity, userList)

                userRecyclerView.adapter = userAdapter
            }

        })
    }
}