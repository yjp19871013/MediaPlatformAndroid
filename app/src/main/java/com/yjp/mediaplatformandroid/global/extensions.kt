package com.yjp.mediaplatformandroid.global

import com.google.gson.Gson
import com.google.gson.JsonParser


fun <T> Gson.jsonToArrayList(json: String, clazz: Class<T>): List<T> {

    //Json的解析类对象
    val parser = JsonParser()

    //将JSON的String 转成一个JsonArray对象
    val jsonArray = parser.parse(json).asJsonArray

    val arrayList = mutableListOf<T>()

    //使用GSon，直接转成对象
    jsonArray.forEach {
        val type = this.fromJson(it, clazz)
        arrayList.add(type)
    }

    return arrayList
}