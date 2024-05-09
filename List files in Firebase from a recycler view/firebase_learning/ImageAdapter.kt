package com.example.firebase_learning

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebase_learning.databinding.ActivityMainBinding
//import com.example.firebase_learning.databinding.ActivityMainBinding
import com.example.firebase_learning.databinding.ItemImageBinding
//import com.google.android.material.progressindicator.BaseProgressIndicator
class ImageAdapter(
    val urls: List<String>

):RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    inner class ImageViewHolder (val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {



        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemImageBinding.inflate(layoutInflater,parent,false)

        return ImageViewHolder(binding)




    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val url = urls[position]
        Glide.with(holder.binding.root).load(url).into(holder.binding.ivImage)

    }

    override fun getItemCount(): Int {
        return urls.size

    }
}




/*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_image.view.*

class ImageAdapter(
    val urls: List<String>
): RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    inner class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_image,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return urls.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val url = urls[position]
        Glide.with(holder.itemView).load(url).into(holder.itemView.ivImage)
    }
}
*/








