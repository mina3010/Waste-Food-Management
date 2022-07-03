package com.mm.foodmanagment.ui.repo

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.mm.foodmanagment.R
import com.mm.foodmanagment.ui.auth.model.User
import com.mm.foodmanagment.ui.main.DashboardActivity
import com.mm.foodmanagment.ui.main.addPost.models.Comments
import com.mm.foodmanagment.ui.main.addPost.models.Image
import com.mm.foodmanagment.ui.main.addPost.models.Post
import com.mm.foodmanagment.ui.main.home.adapter.CommentsAdapter
import com.mm.foodmanagment.ui.main.home.adapter.ImagesAdapter
import com.mm.foodmanagment.ui.main.home.adapter.PostsAdapter
import com.mm.foodmanagment.ui.main.postDetails.adapter.MySliderImageAdapter
import com.mm.foodmanagment.ui.main.profile.ChatActivity
import com.mm.foodmanagment.ui.main.profile.chat.adapter.MessagesAdapter
import com.mm.foodmanagment.ui.main.profile.chat.model.Message
import com.mm.foodmanagment.utils.*
import com.mm.foodmanagment.utils.Constants.UPLOADED_FILES
import kotlinx.android.synthetic.main.fragment_add_post.view.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_post_details.view.*
import kotlinx.android.synthetic.main.fragment_post_details.view.userImg
import kotlinx.android.synthetic.main.fragment_post_details.view.username
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.coroutines.DelicateCoroutinesApi
import java.lang.reflect.InvocationTargetException
import javax.inject.Inject
import kotlin.collections.set


class MyRepository : BaseActivity() {
    @Inject //to make dagger using this class ... iam using inject because this my class made be me .. to dagger get data without viewModel
    //private var ordersDao: CakeMakerDao? = null
    private var mUploadTask: StorageTask<*>? = null
    private var firebaseUserMutableLiveData: MutableLiveData<FirebaseUser?>? = MutableLiveData()
    private var firestoreUser: MutableLiveData<User?>? = MutableLiveData()
    private var userLoggedMutableLiveData: MutableLiveData<Boolean?>? = MutableLiveData()
    private var auth: FirebaseAuth? = FirebaseAuth.getInstance()
    var postComments = 0
    var totalImages = ""
    var checkAll = false
    companion object{
        private lateinit var path: Uri
        private lateinit var path2: Uri
    }


    private val firebaseStorage = FirebaseStorage.getInstance().getReference(UPLOADED_FILES)

    /**
     * Uploads a file.
     * @fileUri -> File to be uploaded to Firebase Storage.
     */
    fun upload(fileUri: Uri) = firebaseStorage.child(fileUri.lastPathSegment!!).putFile(fileUri)

    fun savePostFirestore(
        post: Post,
        activity: DashboardActivity,
        url: String,
        root: View,
        images: ArrayList<Uri>,
    ) {
        val db = FirebaseFirestore.getInstance()
        val list = ArrayList<Uri>()
        try {
            val data: MutableMap<String, Any> = HashMap()
            val id: String = db.collection(Constants.POSTS).document().id
            data["postId"] = id
            data["uid"] = activity.getUserData()?.uid!!
            data["username"] = activity?.getUserData()?.username!!
            data["userImg"] = activity?.getUserData()?.userImg!!
            data["userAddress"] = activity?.getUserData()?.address!!
            data["postImg"] = url ?: ""  //url?:""
            data["postImgType"] = post.imageType ?: ""
            data["postText"] = post.postText ?: ""
            data["timestamp"] = post.timestamp ?: ""
            data["comments"] = post.comments ?: "0"
            data["likes"] = post.likes ?: "0"
            db.collection(Constants.POSTS).document(id).set(data).addOnCompleteListener {
                if (it.isComplete) {
                    if (it.isSuccessful) {

                        val adapter = ImagesAdapter(
                            activity.applicationContext!!,
                            list,
                        )
                        root.imagesRV.adapter = adapter
                        adapter.notifyDataSetChanged()
                        root.postBody_txt.setText("")
                        root.loading.isVisible = false
                        root.addPost_btn.isEnabled = true
                        Toast.makeText(activity, "added post successfully!", Toast.LENGTH_SHORT)
                            .show()
//                        onBackPressed()
                    } else {
                        Toast.makeText(activity, "added post Failed!", Toast.LENGTH_SHORT).show()
//                        if (activity.progressLoading != null) {
//                            activity.progressLoading.isVisible = false
//                        }
                    }
                }
            }
        } catch (e: InvocationTargetException) {
            Toast.makeText(activity, "error: $e", Toast.LENGTH_SHORT).show()
        }

    }


    fun getPost(activityDashboard: DashboardActivity?, root: View, postId: String?) {
        val progressDialog = ProgressDialog(activityDashboard)
        progressDialog.setTitle("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        var post = Post()

        val db = FirebaseFirestore.getInstance()
        val docRef: DocumentReference = db.collection(Constants.POSTS).document(postId ?: "")
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.data)
                    post = Post(
                        document.data?.getValue("postId").toString(),
                        document.data?.getValue("uid").toString(),
                        document.data?.getValue("username").toString(),
                        document.data?.getValue("userImg").toString(),
                        document.data?.getValue("userAddress").toString(),
                        document.data?.getValue("postImg").toString(),
                        document.data?.getValue("postText").toString(),
                        document.data?.getValue("postImgType").toString(),
                        document.data?.getValue("timestamp").toString(),
                        document.data?.getValue("comments").toString().toInt(),
                        document.data?.getValue("likes").toString(),
                    )
                    post.let {
                        root.run {
                            userImg.setOnClickListener {

                                it.findNavController().navigate(
                                    R.id.action_navigation_post_details_to_navigation_profile,
                                    Bundle().apply { putString("uid", document.data?.getValue("uid").toString()?: "0") })
                            }
                            if (document.data?.getValue("postImgType").toString() == "1") {
                                root.postImg.isVisible = true
                                root.imageSlider.isVisible = false
                                val paths = document.data?.getValue("postImg").toString()
                                val imageList = paths?.split("@@")
                                GlideLoader(activityDashboard!!.applicationContext).loadImage(
                                    imageList[0],
                                    postImg
                                )

                                root.postImg.setOnClickListener {
                                    val i = Intent(activityDashboard, OpenImageActivity::class.java)
                                    i.putExtra("URL", imageList[0])
                                    i.putExtra("type", "0")
                                    root.context.startActivity(i)
                                }

                            }
                            else if (document.data?.getValue("postImgType").toString() == "2") {
                                root.imageSlider.isVisible = true
                                root.clickedRelative.isVisible = true
                                root.postImg.isVisible = false
                                val paths = document.data?.getValue("postImg").toString()
                                val nList = ArrayList<String>()
                                val imageList = paths.split("@@")
                                for (i in imageList) {
                                    i.replace("@@", "")
                                    if (!i.isNullOrEmpty()) {
                                        nList.add(i.toString())
                                    }

                                }
                                val adapter = MySliderImageAdapter()
                                adapter.renewItems(nList)
                                imageSlider.setSliderAdapter(adapter)
                                imageSlider.isAutoCycle = true
                                imageSlider.startAutoCycle()

                                root.clickedRelative.setOnClickListener {
                                    val i = Intent(activityDashboard, OpenImageActivity::class.java)
                                    i.putExtra("URL", paths)
                                    i.putExtra("type", "1")
                                    root.context.startActivity(i)
                                }
                            }

                            textPost.text = it.postText
                            comments.text = it.comments.toString()
                            username.text = it.username
                            userAddress.text = it.userAddress
                            postTime.text = activityDashboard?.timeAgo(it.timestamp)
                            GlideCircleLoader(activityDashboard!!.applicationContext).loadImage(
                                it.userImg.toString(),
                                userImg
                            )
                            postComments = it.comments!!.toInt()

                        }
                    }

                    progressDialog.dismiss()
                } else {
                    Log.d(TAG, "No such document")
                    progressDialog.dismiss()
                }
            } else {
                Log.d(TAG, "get failed with ", task.exception)
                progressDialog.dismiss()
            }
        }
    }

    fun getPosts(activityDashboard: DashboardActivity?, root: View): ArrayList<Post> {
        val progressDialog = ProgressDialog(activityDashboard)
        progressDialog.setTitle("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        val list: ArrayList<Post> = ArrayList<Post>()
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.POSTS)
            .get()
            .addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    if (!it.result.documents.isNullOrEmpty()) {
                        for (document in it.result!!) {
//                            if (activity?.getUserData()?.uid == "1"){
//                                if (activity?.getUserData()?.uid.toString() == document.data.getValue("uid").toString()){
                            list.add(
                                Post(
                                    document.data.getValue("postId").toString(),
                                    document.data.getValue("uid").toString(),
                                    document.data.getValue("username").toString(),
                                    document.data.getValue("userImg").toString(),
                                    document.data.getValue("userAddress").toString(),
                                    document.data.getValue("postImg").toString(),
                                    document.data.getValue("postText").toString(),
                                    document.data.getValue("postImgType").toString(),
                                    document.data.getValue("timestamp").toString(),
                                    document.data.getValue("comments").toString().toInt(),
                                    document.data.getValue("likes").toString(),
                                ),
                            )
                        }
                    }
                }
                list.sortWith(compareBy { it.timestamp })
                list.reverse()
                val adapter = PostsAdapter(
                    activityDashboard!!.applicationContext,
                    list,
                    activityDashboard.getUserData(),
                    object : PostsAdapter.ClickListener {
                        override fun onItemClick(v: View, model: Post) {
                            v.findNavController().navigate(
                                R.id.action_navigation_home_to_navigation_post_details,
                                Bundle().apply {
                                    putString("postId", model.postID ?: "0")
                                })
                        }

                        override fun onUserClick(v: View, model: Post) {
                            v.findNavController().navigate(
                                R.id.action_navigation_home_to_navigation_profile,
                                Bundle().apply {
                                    putString("uid", model.uid ?: "0")
                                    putString("userId", model.likes ?: "0")
                                    putString("userName", model.username ?: "")
                                })
                        }

                        override fun onOptionsClick(v: View, model: Post) {
                            activityDashboard.showDeleteDialog({},
                            {
                                deletePost(model.postID,activityDashboard)
                            })
                        }

                    })
                root.postsRv.adapter = adapter
                adapter.notifyDataSetChanged()
                root.postsRv.setHasFixedSize(true)
                progressDialog.dismiss()
//                }
            }
        Log.d(ContentValues.TAG, "mina: ${list.size}")
        return list
    }

    fun getComments(root: View, activity: DashboardActivity?, id: String?): ArrayList<Comments> {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        val list: ArrayList<Comments> = ArrayList<Comments>()

        val db = FirebaseFirestore.getInstance()
        db.collection("comments")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (!it.result.documents.isNullOrEmpty()) {
                        for (document in it.result!!) {
                            if (id == document.data.getValue("postID").toString()) {
                                list.add(
                                    Comments(
                                        document.data.getValue("commentID").toString(),
                                        document.data.getValue("uid").toString(),
                                        document.data.getValue("username").toString(),
                                        document.data.getValue("userImg").toString(),
                                        document.data.getValue("userAddress").toString(),
                                        document.data.getValue("postID").toString(),
                                        document.data.getValue("commentImg").toString(),
                                        document.data.getValue("commentText").toString(),
                                        document.data.getValue("timestamp").toString(),
                                        document.data.getValue("likes").toString().toInt(),
                                    ),
                                )
                            }
                        }
                    }
                    val adapter = CommentsAdapter(
                        activity!!.applicationContext,
                        list,
                        activity.getUserData(),id
                    )
                    root.commentsRv.adapter = adapter
                    adapter.notifyDataSetChanged()
                    progressDialog.dismiss()
                }
            }
        return list
    }

    private fun updatePostFirestore(
        postID: String,

        ) {
        val db = FirebaseFirestore.getInstance()
        try {
            val data: MutableMap<String, Any> = HashMap()
            data["comments"] = postComments + 1

            Log.d("TAG", "id :${postID}")
            db.collection(Constants.POSTS).document(postID).update(data).addOnCompleteListener {
                if (it.isComplete) {
                    if (it.isSuccessful) {

                    }
                }
            }
        } catch (e: InvocationTargetException) {
//            Toast.makeText(activity, "error: $e", Toast.LENGTH_SHORT).show()
        }

    }

    fun deletePost(date_id: String?, activity: DashboardActivity?) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Deleting...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.POSTS).document(date_id!!).delete().addOnSuccessListener {
            Toast.makeText(activity, "successfully deleted!", Toast.LENGTH_SHORT)
                .show()
            progressDialog.dismiss()
        }.addOnFailureListener {
            Toast.makeText(activity, "failure deleted!", Toast.LENGTH_SHORT)
                .show()
            progressDialog.dismiss()
        }
    }

    fun deleteComment(date_id: String?, activity: DashboardActivity?) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Deleting...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        val db = FirebaseFirestore.getInstance()
        db.collection("comments").document(date_id!!).delete().addOnSuccessListener {
            Toast.makeText(activity, "successfully deleted!", Toast.LENGTH_SHORT)
                .show()
            progressDialog.dismiss()
        }.addOnFailureListener {
            Toast.makeText(activity, "failure deleted!", Toast.LENGTH_SHORT)
                .show()
            progressDialog.dismiss()
        }
    }


    fun saveCommentFirestore(
        comment: Comments,
        activity: DashboardActivity,
        root: View,
        commentsCount: Int
    ) {
        val db = FirebaseFirestore.getInstance()
        try {
            val data: MutableMap<String, Any> = HashMap()
            val id: String = db.collection("comments").document().id
            data["commentID"] = id
            data["postID"] = comment.postID!!
            data["uid"] = activity.getUserData()?.uid!!
            data["username"] = activity.getUserData()?.username!!
            data["userImg"] = activity.getUserData()?.userImg!!
            data["userAddress"] = activity.getUserData()?.address!!
            data["commentText"] = comment.commentText!!
            data["commentImg"] = comment.commentImg ?: ""
            data["timestamp"] = activity.getCurrentDate()
            data["likes"] = comment.likes!!

            db.collection("comments").document(id).set(data).addOnCompleteListener {
                if (it.isComplete) {
                    if (it.isSuccessful) {
//                        root.comment_ed.setText("")
                        root.commentText.setText("")
                        root.comments.text = (postComments + 1).toString()
                        Toast.makeText(activity, "successfully sent comment!", Toast.LENGTH_SHORT)
                            .show()

                        updatePostFirestore(comment.postID)

                    } else {
                        Toast.makeText(activity, "added comment Failed!", Toast.LENGTH_SHORT).show()
//                        if (activity.progressLoading != null) {
//                            activity.progressLoading.isVisible = false
//                        }
                    }
                }
            }
        } catch (e: InvocationTargetException) {
            Toast.makeText(activity, "error: $e", Toast.LENGTH_SHORT).show()
        }

    }

     fun updateUser(
         activity: DashboardActivity?,
         root: View,
         user: User,
         uId: String,
         path: Uri?,
         path2: Uri?,

         ) {
        val db = FirebaseFirestore.getInstance()
        try {
            val data: MutableMap<String, Any> = HashMap()

            Log.d("mina up","${path?.toString() ?: user.userImg!!},${path2?.toString() ?: user.menu_img!!}")
            data["username"] = user.username!!
            data["email"] = user.email!!
            data["phone"] = user.phone!!
            data["address"] = user.address!!
            data["userImg"] = path?.toString() ?: user.userImg!!
            data["menu_img"] = path2?.toString() ?: user.menu_img!!
            data["type"] = user.type!!
            data["taxNum"] = user.taxNum!!

            Log.d("mina","up $uId")
            db.collection(Constants.USERS).document(uId).update(data).addOnCompleteListener {
                if (it.isComplete) {
                    if (it.isSuccessful) {

                        Toast.makeText(activity, "update profile successfully!", Toast.LENGTH_SHORT)
                            .show()
                        activity?.saveUserData(User(
                            user.uid,
                            user.username,
                            user.email,
                            user.password,
                            user.phone,
                            user.address,
                            user.otherData,
                            path?.toString() ?: user.userImg!!,
                            path2?.toString() ?: user.menu_img!!,
                            user.type,
                            user.taxNum,
                        ),activity)
                        root.findNavController().popBackStack()

                    } else {
                        Toast.makeText(activity, "update profile Failed!", Toast.LENGTH_SHORT).show()
//                        if (activity.progressLoading != null) {
//                            activity.progressLoading.isVisible = false
//                        }
                    }
                }
            }
        } catch (e: InvocationTargetException) {
//            Toast.makeText(activity, "error: $e", Toast.LENGTH_SHORT).show()
        }

    }



    private fun getFileExtension(uri: Uri, activity: DashboardActivity): String? {
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }

    private val mStorageRef = FirebaseStorage.getInstance().getReference("uploads")

    @DelicateCoroutinesApi
    suspend fun uploadFile(
        mUri: Uri?,
        activity: DashboardActivity,
        post: Post,
        root: View,
        actionType: String,
        image: Image,
        images: ArrayList<Uri>
    ) {

        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)

        val nList = ArrayList<String>()
        var totalString = ""
        if (post.imageType == "1") {
            // update without update image
            val fileReference: StorageReference = mStorageRef.child(
                System.currentTimeMillis()
                    .toString() + "." + getFileExtension(Uri.parse(mUri.toString()), activity)
            )
            mUploadTask = fileReference.putFile(Uri.parse(mUri.toString()))
                .addOnSuccessListener { taskSnapshot -> //loadingDialog.show();
                    val handler = Handler()
                    val uri = taskSnapshot.storage.downloadUrl
                    while (!uri.isComplete);
                    val url = uri.result
                    savePostFirestore(post, activity, url.toString(), root, images)
                    progressDialog.dismiss()

                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
        } else {

            var check = 0
            for (i in images) {
                val fileReference: StorageReference = mStorageRef.child(
                    System.currentTimeMillis()
                        .toString() + "." + getFileExtension(Uri.parse(i.toString()), activity)
                )

                mUploadTask = fileReference.putFile(Uri.parse(i.toString()))
                    .addOnSuccessListener { taskSnapshot -> //loadingDialog.show();
                        val handler = Handler()
                        val uri = taskSnapshot.storage.downloadUrl
                        while (!uri.isComplete);
                        val url = uri.result
                        nList.add(url.toString())
                        totalString += "${url.toString()}@@"
                        check++

                        if (check == images.size) {
                            savePostFirestore(post, activity, totalString, root, images)
                            progressDialog.dismiss()
                        }
                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    }
            }


        }
    }

    fun getUser(activityDashboard: DashboardActivity?, root: View, postId: String?) {
        val progressDialog = ProgressDialog(activityDashboard)
        progressDialog.setTitle("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        var user = User()

        val db = FirebaseFirestore.getInstance()
        val docRef: DocumentReference = db.collection(Constants.USERS).document(postId ?: "")
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.data)
                    user = User(
                        document.data?.getValue("uid").toString(),
                        document.data?.getValue("username").toString(),
                        document.data?.getValue("email").toString(),
                        document.data?.getValue("password").toString(),
                        document.data?.getValue("phone").toString(),
                        document.data?.getValue("address").toString(),
                        document.data?.getValue("otherData").toString(),
                        document.data?.getValue("userImg").toString(),
                        document.data?.getValue("menu_img").toString(),
                        document.data?.getValue("type").toString(),
                    )
                    user.let {
                        root.run {
                            GlideCircleLoader(root.context).loadImage(
                                it.userImg.toString(),
                                root.userImg
                            )
                            username.text = it.username
                            userPhone.setText(it.phone).apply { userPhone.isEnabled = false }
                            uAddress.setText(it.address).apply { uAddress.isEnabled = false }
                            uEmail.setText(it.email).apply { uEmail.isEnabled = false }
                        }
                    }

                    progressDialog.dismiss()
                } else {
                    Log.d(TAG, "No such document")
                    progressDialog.dismiss()
                }
            } else {
                Log.d(TAG, "get failed with ", task.exception)
                progressDialog.dismiss()
            }
        }
    }

    fun sendMessage(activityChat: ChatActivity?, msg: Message?) {
        val db = FirebaseFirestore.getInstance()
        try {
            val data: MutableMap<String, Any> = HashMap()
            val id: String = db.collection("comments").document().id
            data["msgId"] = id
            data["senderId"] = msg?.senderId!!
            data["receiverId"] = msg.receiverId!!
            data["msgTxt"] = msg.msgTxt!!
            data["msgImg"] = msg.msgImg!!
            data["msgDate"] = msg.msgDate!!

            db.collection(Constants.MESSAGES).document(id).set(data).addOnCompleteListener {
                if (it.isComplete) {
                    if (it.isSuccessful) {
//                        root.comment_ed.setText("")
                        //commentText.setText("")
//                        val adapter = MessagesAdapter(
//                            activityChat?.applicationContext!!,
//                            list,
//                        )
//                        messagesRecyclerView.adapter = adapter
//                        adapter.notifyDataSetChanged()

                    } else {
                        Toast.makeText(activityChat, "added comment Failed!", Toast.LENGTH_SHORT)
                            .show()
//                        if (activity.progressLoading != null) {
//                            activity.progressLoading.isVisible = false
//                        }
                    }
                }
            }
        } catch (e: InvocationTargetException) {
            Toast.makeText(activityChat, "error: $e", Toast.LENGTH_SHORT).show()
        }
    }

    fun getMessages(activityChat: ChatActivity?, receiverId: String): ArrayList<Message> {
        val list: ArrayList<Message> = ArrayList<Message>()
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.MESSAGES)
            .get()
            .addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    if (!it.result.documents.isNullOrEmpty()) {
                        for (document in it.result!!) {
//                            if (activity?.getUserData()?.uid == "1"){
                            if (activityChat?.getUserData()?.uid.toString() == document.data.getValue(
                                    "senderId"
                                ).toString()
                            ) {

                                list.add(
                                    Message(
                                        document.data.getValue("msgId").toString(),
                                        document.data.getValue("senderId").toString(),
                                        document.data.getValue("receiverId").toString(),
                                        document.data.getValue("msgTxt").toString(),
                                        document.data.getValue("msgImg").toString(),
                                        document.data.getValue("msgDate").toString(),
                                    ),
                                )
                            }
                        }
                    }
                }
                list.sortWith(compareBy { it.msgDate })
                list.reverse()
                val adapter = MessagesAdapter(
                    activityChat!!.applicationContext,
                    list,
                    activityChat.getUserData(),
                    receiverId
                )
                activityChat.messagesRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
                activityChat.messagesRecyclerView.scrollToPosition(list.size - 1)
                activityChat.messagesRecyclerView.setHasFixedSize(true)
            }
        Log.d(ContentValues.TAG, "mina: ${list.size}")
        return list
    }

    fun uploadImg(
        user: User,
        activity: DashboardActivity,
        mUri: Uri?,
        mUri2: Uri?,
        root: View,
        actionType: String,
        authID: String,
    ) {
        Log.d("minaaaaaaaaa","$mUri, $mUri2")

        if (mUri != null ) {
            val progressDialog = ProgressDialog(activity)
            progressDialog.setTitle("loading...")
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
                            uploadImg2(user, activity, mUri2, root, "1",authID)
                        }else{
                            updateUser( activity,root,user, user.uid?:"", path, Uri.parse(activity.getUserData()?.menu_img))
//                            updateUser( activity,root,user, user.uid?:"", , null)
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
                uploadImg2(user, activity, mUri2, root, "2",authID)
            }else{
                updateUser( activity,root,user, user.uid?:"", Uri.parse(activity.getUserData()?.userImg), Uri.parse(activity.getUserData()?.menu_img))
            }
            //updateUser(activity, root, user, user.uid?:"", null, null)
        }

    }

     private fun uploadImg2(
        user: User,
        activity: DashboardActivity,
        mUri: Uri?,
        root: View,
        actionType: String,
        authID: String,
    ) {
        if (mUri != null) {
            val progressDialog = ProgressDialog(activity)
            progressDialog.setTitle("loading...")
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
                    // updateProductFirestore(user, activity, url.toString(), root,actionType)
                    if (actionType =="2") {
                        updateUser(activity, root, user, user.uid ?: "", Uri.parse(activity.getUserData()?.userImg), path2)
                    }else{
                        updateUser(activity, root, user, user.uid ?: "", path, path2)
                    }

                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()

                }


        } else {
            Toast.makeText(activity, "No file selected", Toast.LENGTH_SHORT).show()
        }

    }



}