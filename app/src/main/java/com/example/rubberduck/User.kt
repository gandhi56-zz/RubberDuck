package com.example.rubberduck

import org.json.JSONArray
import java.io.Serializable

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
    var submissions = ArrayList<Submission>()
    var verdictStats: HashMap<String, Int> = HashMap<String, Int>()

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
        println("adding verdict ${verdict}")
        if (verdictStats.containsKey(verdict)){
            verdictStats[verdict] = verdictStats[verdict]!! + 1
        }
        else{
            println("${verdict} key not found")
            verdictStats[verdict] = 1
        }
    }

}