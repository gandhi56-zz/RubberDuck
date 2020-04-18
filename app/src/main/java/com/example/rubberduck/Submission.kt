package com.example.rubberduck

import java.io.Serializable

class Submission: Serializable {
    var id = 0
    var problem = Problem()
    var verdict: String = ""

    fun Submission(){

    }
}