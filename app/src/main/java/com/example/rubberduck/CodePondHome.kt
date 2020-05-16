package com.example.rubberduck

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi

class CodePondHome : AppCompatActivity() {
    var user: User? = null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_pond_home)
        user = intent.getSerializableExtra(Intent.EXTRA_USER) as User
    }

    fun beginCoding(view: View) {
        val intent = Intent(this, CodeActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.putExtra(Intent.EXTRA_USER, user)
        }
        startActivity(intent)
    }
}
