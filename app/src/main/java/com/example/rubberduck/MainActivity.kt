package com.example.rubberduck

import android.content.Intent.EXTRA_USER
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val user = intent.getSerializableExtra(EXTRA_USER) as User

        val userPic = findViewById<ImageView>(R.id.imageView)
        val handleTxt = findViewById<TextView>(R.id.handleView)
        val rankTxt = findViewById<TextView>(R.id.rankView)

        handleTxt.text = user.getHandle()
        rankTxt.text = user.getRank()
        Picasso.with(this).load(user.getTitlePhoto()).into(userPic)
    }

}
