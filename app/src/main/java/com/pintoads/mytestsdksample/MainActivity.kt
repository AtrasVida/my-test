package com.pintoads.mytestsdksample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pintoads.mylibrary.ImageLoaderHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ImageLoaderHelper.displayCircleImage(
            this, imageViewX,
            "https://regmedia.co.uk/2016/04/19/billy_the_puppet.jpg?x=442&y=293&crop=1"
        )

    }
}
