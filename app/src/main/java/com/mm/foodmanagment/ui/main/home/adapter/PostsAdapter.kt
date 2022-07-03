package com.mm.foodmanagment.ui.main.home.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mm.foodmanagment.R
import com.mm.foodmanagment.ui.auth.model.User
import com.mm.foodmanagment.ui.main.DashboardActivity
import com.mm.foodmanagment.ui.main.addPost.models.Post
import com.mm.foodmanagment.ui.main.home.HomeViewModel
import com.mm.foodmanagment.utils.GlideCircleLoader
import com.mm.foodmanagment.utils.GlideLoader
import de.hdodenhof.circleimageview.CircleImageView


class PostsAdapter(
    val context: Context,
    private val list: ArrayList<Post>,
    private val userData: User?,
    private val clickListener: ClickListener?,
    ) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {
    private var viewModel: HomeViewModel? = HomeViewModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.post_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (list[position].username == context.getString(R.string.admin)){
            GlideCircleLoader(context).loadImage(Uri.parse(list[position].userImg.toString()), holder.userImg)
            holder.verifiedAdmin.isVisible = true
            holder.userAddress.isVisible = false
        }else{
            GlideCircleLoader(context).loadImage(Uri.parse(list[position].userImg.toString()), holder.userImg)
            Log.d("mina img","${list[position].userImg}")
        }
        val paths = list[position].image.toString()
        val imageList = paths.split("@@")
        when (list[position].imageType) {
            "0" -> {
                holder.postImg.isVisible = false
                holder.imagesContainer.isVisible = false

            }
            "1" -> {
                holder.postImg.isVisible = true
                holder.imagesContainer.isVisible = false
                GlideLoader(context).loadImage(imageList[0], holder.postImg)
                Log.d("mina 1","${imageList[0]}")
            }
            else -> {
                holder.postImg.isVisible = false
                holder.imagesContainer.isVisible = true
                GlideLoader(context).loadImage(imageList[0], holder.postImg1)
                holder.postImgMoreTxt.text = "+${imageList.size-2}"
                Log.d("mina 2","${imageList.size}")
            }
        }

        holder.username.text = list[position].username?:""
        holder.userAddress.text = list[position].userAddress?:""
        holder.postTime.text = DashboardActivity().timeAgo(list[position].timestamp)?:"${list[position].timestamp}"
        holder.comments.text = list[position].comments.toString()?:""
        holder.textPost.text = list[position].postText.toString()?:""
        if (userData?.uid == list[position].uid){
            holder.edit.isVisible = true
        }
        if (userData?.type == "3"){
            holder.edit.isVisible = true
        }
        holder.userImg.setOnClickListener {
            clickListener?.onUserClick(it,list[position])
        }
        holder.username.setOnClickListener {
            clickListener?.onUserClick(it,list[position])
        }
        holder.comments.setOnClickListener {
            clickListener?.onItemClick(it,list[position])
        }
        holder.textPost.setOnClickListener {
            clickListener?.onItemClick(it,list[position])
        }

        holder.postImg.setOnClickListener {
            clickListener?.onItemClick(it,list[position])
        }
        holder.imagesContainer.setOnClickListener {
            clickListener?.onItemClick(it,list[position])
        }

        holder.edit.setOnClickListener {
            clickListener?.onOptionsClick(it,list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userImg = itemView.findViewById<CircleImageView>(R.id.userImg)!!
        var sharePost = itemView.findViewById<ImageView>(R.id.sharePost)!!
        var edit = itemView.findViewById<ImageView>(R.id.edit)!!
        var username = itemView.findViewById<TextView>(R.id.username)!!
        var userAddress = itemView.findViewById<TextView>(R.id.userAddress)!!
        var postTime = itemView.findViewById<TextView>(R.id.postTime)!!
        var textPost = itemView.findViewById<TextView>(R.id.textPost)!!
        var comments = itemView.findViewById<TextView>(R.id.comments)!!
        var postImgMoreTxt = itemView.findViewById<TextView>(R.id.postImgMore)!!
        var postImg = itemView.findViewById<ImageView>(R.id.postImg)!!
        var postImg1 = itemView.findViewById<ImageView>(R.id.postImg1)!!
        var verifiedAdmin = itemView.findViewById<ImageView>(R.id.verified_admin)!!
        var imagesContainer = itemView.findViewById<LinearLayout>(R.id.linear)!!


    }

    interface ClickListener {
        fun onItemClick(v: View, model: Post)
        fun onUserClick(v: View, model: Post)
        fun onOptionsClick(v: View, model: Post)
    }
}