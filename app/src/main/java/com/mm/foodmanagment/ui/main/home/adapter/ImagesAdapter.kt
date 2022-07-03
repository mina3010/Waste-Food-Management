package com.mm.foodmanagment.ui.main.home.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.mm.foodmanagment.R
import com.mm.foodmanagment.ui.main.home.HomeViewModel
import com.mm.foodmanagment.utils.GlideLoader


class ImagesAdapter(
    val context: Context,
    private val list: ArrayList<Uri>,
    ) :
    RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {
    private var viewModel: HomeViewModel? = HomeViewModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.image_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        GlideLoader(context).loadImage(list[position], holder.image)
         holder.close.setOnClickListener {
             notifyItemRemoved(position)
             list.removeAt(position)
             notifyDataSetChanged()

         }

    }

    override fun getItemCount(): Int {
        return list.size
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image = itemView.findViewById<ImageView>(R.id.imgView)!!
        var close = itemView.findViewById<ImageView>(R.id.closeImg)!!


    }


}