package com.naestem.petnet.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.naestem.petnet.R

class ImageListAdapter(val context: Context, private val imageList: ArrayList<Uri>) :
    RecyclerView.Adapter<ImageListAdapter.ImageListViewHolder>() {
    inner class ImageListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.petImageIV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListViewHolder {
        return ImageListViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageListViewHolder, position: Int) {
        holder.image.setImageURI(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}