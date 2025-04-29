package com.st10254797.smartbudgetting

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ImageViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val imageView = findViewById<ImageView>(R.id.fullScreenImageView)

        // Get the image URL from the Intent extras
        val imageUrl = intent.getStringExtra("imageUrl")

        // Load the image using Glide
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(imageView)
        }
    }
}
