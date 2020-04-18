package com.example.rubberduck

import java.io.Serializable

class Problem : Serializable{
    var contestId:Int = 0
    var index: String = ""
    var name: String = ""
    var rating: Int = 0
    var tags = ArrayList<String>()
    fun Problem(){

    }
}