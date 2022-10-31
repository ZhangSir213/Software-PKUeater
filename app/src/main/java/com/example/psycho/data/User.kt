package com.example.psycho.data

import com.google.gson.Gson
import java.io.File


data class User(
    var username: String,
    var password: String,
    var weight: Double,
    var height: Int
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