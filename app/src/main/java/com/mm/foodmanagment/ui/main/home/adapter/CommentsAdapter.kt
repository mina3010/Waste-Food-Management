package com.mm.foodmanagment.ui.main.home.adapter

import android.app.ProgressDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mm.foodmanagment.R
import com.mm.foodmanagment.ui.auth.model.User
import com.mm.foodmanagment.ui.main.DashboardActivity
import com.mm.foodmanagment.ui.main.addPost.models.Comments
import com.mm.foodmanagment.ui.main.addPost.models.Post
import com.mm.foodmanagment.ui.main.home.HomeViewModel
import com.mm.foodmanagment.utils.Constants
import com.mm.foodmanagment.utils.GlideCircleLoader
import com.mm.foodmanagment.utils.GlideLoader
import com.mm.foodmanagment.utils.showDeleteDialog
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.reflect.InvocationTargetException


class CommentsAdapter(
    val context: Context,
    private val list: ArrayList<Comments>,
    private val userData: User?,
    private val post_id: String?,
    ) :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    private var viewModel: HomeViewModel? = HomeViewModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.comment_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        GlideCircleLoader(context).loadImage(list[position].userImg.toString(), holder.userImg)
        val paths = list[position].commentImg.toString()
        //val imageList = paths.split("@@")
        GlideLoader(context).loadImage(list[position].commentImg.toString(), holder.postImg1)


        holder.username.text = list[position].username?:""
        holder.userAddress.text = list[position].userAddress?:""
        if (!list[position].timestamp.isNullOrEmpty()) {
            holder.postTime.text = DashboardActivity().timeAgo(list[position].timestamp)
        }
        holder.textPost.text = list[position].commentText.toString()?:""
        if (userData?.uid == list[position].uid){
            holder.edit.isVisible = true
        }
        if (userData?.username == "admin"){
            holder.edit.isVisible = true
        }

        holder.edit.setOnClickListener {
            holder.deleteComment(list[position].commentID,position,list,post_id)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userImg = itemView.findViewById<CircleImageView>(R.id.userImg)!!
        var edit = itemView.findViewById<ImageView>(R.id.edit)!!
        var username = itemView.findViewById<TextView>(R.id.username)!!
        var userAddress = itemView.findViewById<TextView>(R.id.userAddress)!!
        var postTime = itemView.findViewById<TextView>(R.id.postTime)!!
        var textPost = itemView.findViewById<TextView>(R.id.textPost)!!
//        var comments = itemView.findViewById<TextView>(R.id.comments)!!
        var postImg = itemView.findViewById<ImageView>(R.id.postImg)!!
        var postImg1 = itemView.findViewById<ImageView>(R.id.postImg1)!!
        var imagesContainer = itemView.findViewById<LinearLayout>(R.id.linear)!!


        fun deleteComment(
            commentID: String?,
            position: Int,
            list: ArrayList<Comments>,
            post_id: String?
        ) {

            itemView.context.showDeleteDialog({},
                {
                    val progressDialog = ProgressDialog(itemView.context)
                    progressDialog.setTitle("Deleting...")
                    progressDialog.show()
                    progressDialog.setCancelable(false)
                    val db = FirebaseFirestore.getInstance()
                    commentID?.let {
                        db.collection("comments").document(it).delete().addOnSuccessListener {
                            Toast.makeText(itemView.context, "successfully deleted!", Toast.LENGTH_SHORT)
                                .show()
                            if (list.size > 0) {
                                updatePostComments(list, post_id)
                            }
                            list.removeAt(position)
                            progressDialog.dismiss()
                        }.addOnFailureListener {
                            Toast.makeText(itemView.context, "failure deleted!", Toast.LENGTH_SHORT)
                                .show()
                            progressDialog.dismiss()
                        }
                    }
                })
        }

        private fun updatePostComments(list: ArrayList<Comments>, post_id: String?) {
            val db = FirebaseFirestore.getInstance()
            try {
                val data: MutableMap<String, Any> = HashMap()
                data["comments"] = list.size - 1

                db.collection(Constants.POSTS).document(post_id?:"").update(data).addOnCompleteListener {
                    if (it.isComplete) {
                        if (it.isSuccessful) {

                        }
                    }
                }
            } catch (e: InvocationTargetException) {
//            Toast.makeText(activity, "error: $e", Toast.LENGTH_SHORT).show()
            }
        }
    }

    interface ClickListener {
        fun onItemClick(v: View, model: Post)
//        fun onSaveClick(v: View, model: PostsResponse.Data, index: Int)
//        fun onUserClick(v: View, model: PostsResponse.Data)
//        fun onImageClick(v: View, model: PostsResponse.Data, index: Int)
//        fun onLikeClick(v: View, model: PostsResponse.Data, index: Int)
//        fun onLikedByClick(v: View, model: PostsResponse.Data, index: Int)
//        fun onDeleteClick(v: View, model: PostsResponse.Data, index: Int)
//        fun onShareClick(v: View, model: PostsResponse.Data, index: Int)
        fun onShareToAllClick(v: View, model: Post)
    }
}