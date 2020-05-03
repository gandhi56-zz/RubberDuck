package com.example.rubberduck

import org.json.JSONArray
import java.io.Serializable
import java.math.BigInteger

val verdicts = arrayOf("FAILED",
    "OK", "PARTIAL", "COMPILATION_ERROR", "RUNTIME_ERROR",
    "WRONG_ANSWER", "PRESENTATION_ERROR", "TIME_LIMIT_EXCEEDED",
    "MEMORY_LIMIT_EXCEEDED", "IDLENESS_LIMIT_EXCEEDED",
    "SECURITY_VIOLATED", "CRASHED", "INPUT_PREPARATION_CRASHED",
    "CHALLENGED", "SKIPPED", "TESTING", "REJECTED")

class User: Serializable {
    private var handle: String? = null
    private var titlePhoto: String? = null
    private var rank: String? = null
    var ratingChangeList = ArrayList<RatingChange>()
    var verdictStats: HashMap<String, Int> = HashMap<String, Int>()
    var classStats: HashMap<String, Int> = HashMap<String, Int>()
    var lastSubmId = -1
    var subm = HashMap<String, ArrayList<Submission>>()

    fun User(){
        handle = ""
        titlePhoto = ""
        rank = ""
    }

    fun setHandle(handleValue: String){
        handle = handleValue
    }
    fun getHandle(): String{
        return handle.toString()
    }
    fun setTitlePhoto(titlePhotoValue: String){
        titlePhoto = titlePhotoValue
    }
    fun getTitlePhoto(): String{
        return titlePhoto.toString()
    }
    fun setRank(rankValue: String){
        rank = rankValue
    }

    fun getRank(): String{
        return rank.toString()
    }

    fun addVerdict(verdict: String){
        if (verdictStats.containsKey(verdict)){
            verdictStats[verdict] = verdictStats[verdict]!! + 1
        }
        else{
            verdictStats[verdict] = 1
        }
    }

    fun addClass(tag: String){
        if (classStats.containsKey(tag)){
            classStats[tag] = classStats[tag]!! + 1
        }
        else{
            classStats[tag] = 1
        }
    }

    fun addSubmission(key: String, sub: Submission){
        if (!subm.containsKey(key)){
            subm[key] = ArrayList<Submission>()
        }
        subm[key]!!.add(sub)
    }

}