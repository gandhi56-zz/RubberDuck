package com.example.rubberduck

import java.io.Serializable

class Submission: Serializable {
    var id = 0
    lateinit var verdict: String
    var problem = Problem()
}