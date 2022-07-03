package com.mm.foodmanagment.ui.main.users.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mm.foodmanagment.R
import com.mm.foodmanagment.ui.main.profile.ChatActivity
import com.mm.foodmanagment.ui.main.users.model.UserData
import com.mm.foodmanagment.utils.GlideCircleLoader


import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val context: Context, private val userDataList: ArrayList<UserData>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userDataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userDataList[position]
        holder.txtUserName.text = user.userName

        if (user.userName == "admin"){
            holder.verifiedAdmin.isVisible = true
        }
        GlideCircleLoader(context).loadImage(user.profileImage, holder.imgUser)
//        Glide.with(context).load(user.profileImage).placeholder(R.drawable.profile_image).into(holder.imgUser)

        holder.layoutUser.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("userId",user.userId)
            intent.putExtra("userName",user.userName)
            context.startActivity(intent)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtUserName:TextView = view.findViewById(R.id.userName)
        val txtTemp:TextView = view.findViewById(R.id.temp)
        val imgUser:CircleImageView = view.findViewById(R.id.userImage)
        val layoutUser:LinearLayout = view.findViewById(R.id.layoutUser)
        val verifiedAdmin:ImageView = view.findViewById(R.id.verified_admin)
    }
}