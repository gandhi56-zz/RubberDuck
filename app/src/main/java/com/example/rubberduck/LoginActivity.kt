package com.example.rubberduck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

const val EMAIL_MESSAGE = "com.example.rubberduck.EMAIL_MESSAGE"

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun startMainActivity(view: View){
        val emailTxt = findViewById<EditText>(R.id.emailText)
        val passwordTxt = findViewById<EditText>(R.id.passwordText)
        val email = emailTxt.text.toString()
        val password = passwordTxt.text.toString()
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(EMAIL_MESSAGE, email)
        }
        startActivity(intent)
    }
}
