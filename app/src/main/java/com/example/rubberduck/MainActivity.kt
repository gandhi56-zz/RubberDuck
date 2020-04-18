package com.example.rubberduck

import android.content.Intent.EXTRA_USER
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val user = intent.getSerializableExtra(EXTRA_USER) as User

        val userPic = findViewById<ImageView>(R.id.titlePhoto)
        val handleTxt = findViewById<TextView>(R.id.handleView)
        val rankTxt = findViewById<TextView>(R.id.rankView)

        handleTxt.text = user.getHandle()
        rankTxt.text = user.getRank()
        println(user.getTitlePhoto())
        Picasso.with(this).load(user.getTitlePhoto()).into(userPic)
    }

}
