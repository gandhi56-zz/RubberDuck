package com.example.rubberduck

import android.content.Intent
import android.content.Intent.EXTRA_USER
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        user = intent.getSerializableExtra(EXTRA_USER) as User

        val userPic = findViewById<ImageView>(R.id.titlePhoto)
        val handleTxt = findViewById<TextView>(R.id.handleView)
        val rankTxt = findViewById<TextView>(R.id.rankView)

        val ratingsBtn = findViewById<Button>(R.id.ratingBtn)
        val solvedProblemsBtn = findViewById<Button>(R.id.solvedProblemsBtn)
        val strengthsBtn = findViewById<Button>(R.id.strengthsBtn)

        Picasso.with(this).load(user!!.getTitlePhoto()).into(userPic)

        handleTxt.text = user!!.getHandle()
        rankTxt.text = user!!.getRank()
        solvedProblemsBtn.text = "Attempted ${user!!.submissions.size}"
        ratingsBtn.text = user!!.ratingList[user!!.ratingList.size-1].toString()
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

}
