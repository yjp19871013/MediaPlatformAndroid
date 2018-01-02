package com.yjp.mediaplatformandroid.global

import com.google.gson.JsonParser


fun <T> String.jsonToArrayList(clazz: Class<T>): List<T> {

    //Json的解析类对象
    val parser = JsonParser()

    //将JSON的String 转成一个JsonArray对象
    val jsonArray = parser.parse(this).asJsonArray

    val gson = MyApplication.GSON
    val arrayList = mutableListOf<T>()

    //使用GSon，直接转成对象
    jsonArray.forEach {
        val type = gson.fromJson(it, clazz)
        arrayList.add(type)
    }

    return arrayList
}