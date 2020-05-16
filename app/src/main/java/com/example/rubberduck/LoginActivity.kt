package com.example.rubberduck

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_login.*

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

    private fun hideKeyboard(){
        val view: View? = this.currentFocus
        if (view != null){
            val hideMe = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideMe.hideSoftInputFromWindow(view.windowToken, 0)
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
}

