package com.example.rubberduck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        item ->
        when(item.itemId){
            R.id.stats->{
                println("Stats selected")
                replaceFragment(StatsFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.contest->{
                println("Contest selected")
                replaceFragment(ContestFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.play->{
                println("Play selected")
                replaceFragment(PlayFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.blog->{
                println("Blog selected")
                replaceFragment(BlogFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.user->{
                println("User selected")
                replaceFragment(UserFragment())
                return@OnNavigationItemSelectedListener true
            }
            else -> {
                return@OnNavigationItemSelectedListener false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        replaceFragment(UserFragment()) // default fragment on screen
        bottomNavigation.menu.findItem(R.id.user).setChecked(true)
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }
}
