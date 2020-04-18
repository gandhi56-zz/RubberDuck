package com.example.rubberduck

import java.io.Serializable

class User: Serializable {
    private var handle: String? = null
    private var titlePhoto: String? = null
    private var rank: String? = null

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
}