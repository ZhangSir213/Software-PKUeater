package com.example.psycho.data

import com.google.gson.Gson
import java.io.File


data class DietLog(var meal: Int, var foodName:String)

data class User(
    var username: String,
    var password: String,
    var weight: Double,
    var height: Int,
    var Login: Boolean,
    var gender:Int,
    var birthday:String,
    var canteenCount: IntArray,
    val avoidanceString: List<String>,
    var avoidanceValue: BooleanArray,
    var todayMenu: List<String>,
    var budget: Double,
    var menuChange: Boolean,
    var loginFirst:Boolean,
    var dietlog: ArrayList<DietLog>,
    var avoidanceFlag: Boolean,
    var plan:Data.Plan

)




fun main()
{
    //val file = File("a.json")
    //file.createNewFile()
    val content = File("a.json").readText()
    val newUser=Gson().fromJson(content, User::class.java)
    println(newUser.username)
    //val user = User(username="阿四", password="123456")
    //val json = Gson().toJson(user)
}