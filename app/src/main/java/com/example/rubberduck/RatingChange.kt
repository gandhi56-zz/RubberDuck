package com.example.rubberduck

import java.io.Serializable

class RatingChange: Serializable {
    var contestId = 0
    var contestName = ""
    var rank = 0
    var newRating = 0
}