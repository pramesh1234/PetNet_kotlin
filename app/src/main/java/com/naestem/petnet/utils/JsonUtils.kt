package com.naestem.petnet.utils

import com.google.gson.GsonBuilder
import org.json.JSONArray

object JsonUtils {

        inline fun <reified T> parseJson(strng : String): T {
            val gson = GsonBuilder().serializeNulls().create()
            return gson.fromJson(strng, T::class.java)
        }

        inline fun <reified T> parseJsonToList(strng : String): ArrayList<T> {
            val gson = GsonBuilder().serializeNulls().create()
            val jsonArray = JSONArray(strng)
            val list = ArrayList<T>()
            for(i in 0 until jsonArray.length()){
                val item =   gson.fromJson(jsonArray[i].toString(), T::class.java)
                list.add(item)
            }
            return list
        }

        fun <T> toJson(jObj : T): String {
            val gson = GsonBuilder().serializeNulls().create()
            return gson.toJson(jObj)
        }

        fun <T> toJson(jObj : ArrayList<T>): String {
            val gson = GsonBuilder().serializeNulls().create()
            return gson.toJson(jObj)
        }


}