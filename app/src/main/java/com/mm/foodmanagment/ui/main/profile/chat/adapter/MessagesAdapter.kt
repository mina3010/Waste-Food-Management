package com.mm.foodmanagment.ui.main.profile.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mm.foodmanagment.R
import com.mm.foodmanagment.ui.auth.model.User
import com.mm.foodmanagment.ui.main.addPost.models.Post
import com.mm.foodmanagment.ui.main.profile.chat.model.Message


class MessagesAdapter(
    private val context: Context,
    private val list: ArrayList<Message>,
    private val userData: User?,
    private val receiverId: String,
    ) :
    RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_message_sent,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//        GlideCircleLoader(context).loadImage(list[position].userImg.toString(), holder.userImg)
//        val paths = list[position].image.toString()
//        val imageList = paths.split("@@")
        if (receiverId == list[position].receiverId && userData?.uid ==  list[position].senderId) {
            when (list[position].senderId) {
                userData?.uid -> {
                    holder.senderMsg.text = list[position].msgTxt
                    holder.timeSenderMsg.text = list[position].msgDate
                    holder.senderMsg.isVisible = true
                    holder.timeSenderMsg.isVisible = true

                }
                else -> {
                    holder.receiverMsg.text = list[position].msgTxt
                    holder.timeReceiverMsg.text = list[position].msgDate
                    holder.receiverMsg.isVisible = true
                    holder.timeReceiverMsg.isVisible = true
                }
            }
            when (list[position].receiverId) {
                receiverId -> {
                    holder.receiverMsg.text = list[position].msgTxt
                    holder.timeReceiverMsg.text = list[position].msgDate
                    holder.receiverMsg.isVisible = true
                    holder.timeReceiverMsg.isVisible = true

                }
                else -> {
                    holder.senderMsg.text = list[position].msgTxt
                    holder.timeSenderMsg.text = list[position].msgDate
                    holder.senderMsg.isVisible = true
                    holder.timeSenderMsg.isVisible = true
                }
            }

        }

//        holder.username.text = list[position].username?:""
//        holder.userAddress.text = list[position].userAddress?:""
//        holder.postTime.text = DashboardActivity().timeAgo(list[position].timestamp)?:"${list[position].timestamp}"
//        holder.comments.text = list[position].comments.toString()?:""
//        holder.textPost.text = list[position].postText.toString()?:""
//        if (userData?.uid == list[position].uid){
//            holder.edit.isVisible = true
//        }
//
//        holder.userImg.setOnClickListener {
//            clickListener?.onUserClick(it,list[position])
//        }
//        holder.comments.setOnClickListener {
//            clickListener?.onItemClick(it,list[position])
//        }

    }

    override fun getItemCount(): Int {
        return list.size
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var userImg = itemView.findViewById<CircleImageView>(R.id.userImg)!!

        var senderMsg = itemView.findViewById<TextView>(R.id.senderMsg)!!
        var receiverMsg = itemView.findViewById<TextView>(R.id.receiverMsg)!!
        var timeReceiverMsg = itemView.findViewById<TextView>(R.id.timeReceiverMsg)!!
        var timeSenderMsg = itemView.findViewById<TextView>(R.id.timeSenderMsg)!!



    }

    interface ClickListener {
        fun onItemClick(v: View, model: Post)
//        fun onSaveClick(v: View, model: PostsResponse.Data, index: Int)
        fun onUserClick(v: View, model: Post)
//        fun onImageClick(v: View, model: PostsResponse.Data, index: Int)
//        fun onLikeClick(v: View, model: PostsResponse.Data, index: Int)
//        fun onLikedByClick(v: View, model: PostsResponse.Data, index: Int)
//        fun onDeleteClick(v: View, model: PostsResponse.Data, index: Int)
//        fun onShareClick(v: View, model: PostsResponse.Data, index: Int)
        fun onShareToAllClick(v: View, model: Post)
    }
}