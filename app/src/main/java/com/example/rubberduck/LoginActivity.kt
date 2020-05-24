package com.example.rubberduck

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_login.*

data class UserData(
    var id: String = "",
    var handle: String? = "",
    var email: String? = ""
)

@Suppress("UNREACHABLE_CODE")
class LoginActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val fragmentAdapter = TabLayoutAdapter(supportFragmentManager)
        viewPager.adapter = fragmentAdapter
        tabLayout.setupWithViewPager(viewPager)
        progressBar.visibility = View.INVISIBLE
    }
}

