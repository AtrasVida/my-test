package com.pintoads.mylibrary

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


object ImageLoaderHelper {
    fun displayCircleImage(context: Context, imageView: ImageView, imageUrl: String) {
        Glide.with(context)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

            .into(imageView)
    }

}