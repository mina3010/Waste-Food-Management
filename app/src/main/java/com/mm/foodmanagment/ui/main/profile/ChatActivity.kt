package com.mm.foodmanagment.ui.main.profile

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mm.foodmanagment.ui.main.users.model.NotificationData
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import com.mm.foodmanagment.utils.BaseActivity
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import com.mm.foodmanagment.R
import com.mm.foodmanagment.ui.main.users.*
import com.mm.foodmanagment.ui.main.users.adapter.ChatAdapter
import com.mm.foodmanagment.ui.main.users.model.Chat
import com.mm.foodmanagment.ui.main.users.model.PushNotification
import com.mm.foodmanagment.ui.main.users.model.UserData
import com.mm.foodmanagment.ui.main.users.remote.RetrofitInstance
import com.mm.foodmanagment.ui.main.users.service.FirebaseService
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.imgBack
import kotlinx.android.synthetic.main.activity_chat.imgProfile
import kotlinx.android.synthetic.main.activity_users.*


class ChatActivity : BaseActivity() {
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var chatList = ArrayList<Chat>()
    var topic = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)


// this might be helpful

        var intent = getIntent()
        var userId = intent.getStringExtra("userId")
        var userName = intent.getStringExtra("userName")


        imgBack.setOnClickListener {
            onBackPressed()
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)




        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.getValue(UserData::class.java)
                tvUserName.text = user!!.userName
                if (getUserData()?.type == "3"){
                    imgProfile.setImageResource(R.drawable.logo_food)
                }else {
                    Glide.with(this@ChatActivity).load(getUserData()?.userImg).into(imgProfile)
                }
            }
        })

        btnSendMessage.setOnClickListener {
            var message: String = etMessage.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(applicationContext, "message is empty", Toast.LENGTH_SHORT).show()
                etMessage.setText("")
            } else {
                Log.d("mina","${FirebaseService.token},$topic,${firebaseUser!!.uid} $userId")
                sendMessage(firebaseUser!!.uid, userId, message)
                etMessage.setText("")
                topic = "/topics/$userId"
                PushNotification(
                    NotificationData( userName?:"",message),
                    topic).also {
                    sendNotification(it)
                }

            }
        }

        readMessage(firebaseUser!!.uid, userId)
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        var reference: DatabaseReference? = FirebaseDatabase.getInstance().reference
        var hashMap: HashMap<String, String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message
        reference!!.child("Chat").push().setValue(hashMap)
    }
    fun readMessage(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)
                    if (chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId) ||
                        chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }
                val chatAdapter = ChatAdapter(this@ChatActivity, chatList)
                chatRecyclerView.adapter = chatAdapter
                chatRecyclerView.scrollToPosition(chatList.size - 1)
            }
        })
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d("TAG", "Response: ${Gson().toJson(response)}")
            } else {
                Log.e("TAG", response.errorBody()!!.string())
            }
        } catch(e: Exception) {
            Log.e("TAG", e.toString())
        }
    }

}
//    private var viewModel: ChatViewModel? = ChatViewModel()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_chat)
//
//        setListener()
//        chatObservers()
//    }
//
//    private fun setListener() {
//
//            sendBtn.setOnClickListener {
//                if (editTextMessage.text.toString().isNotEmpty()) {
//                viewModel?.run {
//                    sendMsg(
//                        this@ChatActivity, Message(
//                            "",
//                            getUserData()?.uid,
//                            intent.getStringExtra("uid") ?: "",
//                            editTextMessage.text.toString(),
//                            "",
//                            Date().toString()
//                        )
//                    )
//                }
//                editTextMessage.setText("")
//                chatObservers()
//                }
//        }
//    }
//    private fun chatObservers() {
//        viewModel!!.getMessages(this,intent.getStringExtra("uid") ?: "")
//    }
//}