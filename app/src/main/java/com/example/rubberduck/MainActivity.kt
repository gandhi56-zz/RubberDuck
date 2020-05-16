package com.example.rubberduck

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.EXTRA_STREAM
import android.content.Intent.EXTRA_USER
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        user = intent.getSerializableExtra(EXTRA_USER) as User
//        problemSet = intent.getSerializableExtra(EXTRA_STREAM) as ArrayList<Problem>

        val userPic = findViewById<ImageView>(R.id.titlePhoto)
        val handleTxt = findViewById<TextView>(R.id.handleView)
        val rankTxt = findViewById<TextView>(R.id.rankView)

        Picasso.with(this).load(user!!.getTitlePhoto()).into(userPic)

        handleTxt.text = user!!.getHandle()
        rankTxt.text = user!!.getRank()
    }

    override fun onBackPressed() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure about logging out?")
        builder.setPositiveButton("Yes"){
                _: DialogInterface?, _: Int ->
            this.finish()
        }

        builder.setNegativeButton("No"){
                _: DialogInterface?, _: Int ->
        }
        val alert = builder.create()
        alert.setCancelable(false)
        alert.show()
    }

    fun startRatingActivity(view: View) {
        val intent = Intent(this, RatingActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.putExtra(Intent.EXTRA_USER, user)
        }
        startActivity(intent)
    }

    fun startSubmissionActivity(view: View) {
        val intent = Intent(this, SubmissionActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.putExtra(Intent.EXTRA_USER, user)
        }
        startActivity(intent)
    }

    fun startCategoriesActivity(view: View) {
        val intent = Intent(this, CategoriesActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.putExtra(Intent.EXTRA_USER, user)
        }
        startActivity(intent)
    }

    fun startCodeActivity(view: View) {
        val intent = Intent(this, CodePond::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.putExtra(Intent.EXTRA_USER, user)
        }
        startActivity(intent)
    }

}
