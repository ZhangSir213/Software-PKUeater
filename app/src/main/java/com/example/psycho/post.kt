package com.example.psycho

import android.util.Log
import com.example.psycho.data.Data
import com.example.psycho.data.PostData
import com.google.gson.Gson
import okhttp3.*
import okio.IOException
import kotlin.concurrent.thread

private fun simpleDealData(response: Response): String = StringBuilder().apply {
    append("\n\t")
    append("header")
    append("\n\t")
    append(response.headers.joinToString("\n\t"))
    append("\n\t")
    append("body")
    append("\n\t")
    append("responseCode: ${response.code}")
    append("\n\t")
    append(
        "content: ${
            (response.body?.string() ?: "").let { s: String ->
                //对获取到的数据 简单做一下格式化
                s.split(",").joinToString("\n\t")
            }
        }"
    )
}.toString()

@Synchronized
fun simplePostUseFrom(url: String, params: Map<String,String>? = null) {
    //创建 formBody
    val t= thread{

        val formBody = FormBody.Builder()
            .also { builder ->
                params?.forEach { (name, value) ->
                    //参数需要 add 进入FormBody.Builder
                    builder.add(name, value)
                }
            }.build()
        val okHttpClient = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .post(formBody) //注意：此时formBody以post的形式传入
            .build()

        val call = okHttpClient.newCall(request)
        try {
            val responses = call.execute()
            var msg="post同步请求" + (responses.body?.string() ?: "")
            val msg_new=msg.drop(8)
            Log.d("Post", msg_new)
            //val response=Gson().fromJson(responses.body.toString(),PostData::class.java)
            //println(response.status)
            val response=Gson().fromJson(msg_new,PostData::class.java)
            var global_file=Data
            global_file.setPostData(response)
        } catch (e: Throwable) {
            Log.d("Post", "failed")
            println(e.toString())
            e.printStackTrace()
        }


        /*okHttpClient.newCall(request).enqueue(object :Callback {
        override fun onFailure(call: Call, e: IOException) {
            print("go failure ${e.message}")
        }
        override fun onResponse(call: Call, response: Response) {
            val msg = if (response.isSuccessful) {
                val msg=(response.body?.string() ?: "")
                Log.d("Post",msg)

            } else {
                "failure code:${response.code} message:${response.message}"
            }

        }
    })*/
    }
    t.join()

}


fun main()
{
    val url="http://localhost:8090/user/register"
    val map = mapOf("name" to "user1","gender" to "2","age" to "25","password" to "23333")
    simplePostUseFrom(url,map)

    /*
    val url="localhost:8090/user/register"
    var jsonObject = JSONObject()
    jsonObject.put("userName", "root")
    jsonObject.put("password", "123456")
    var jsonStr=jsonObject.toString()
    val requestBody = jsonStr?.let {
        //创建requestBody 以json的形式
        val contentType: MediaType = "application/json".toMediaType()
        jsonStr.toRequestBody(contentType)
    } ?: run {
        //如果参数为null直接返回null
        FormBody.Builder().build()
    }

    thread {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(requestBody) //以post的形式添加requestBody
            .build()
        var response = client.newCall(request).execute()
        val responseData = response.body?.string()
        if (responseData != null) {
            val jsonObject = JSONObject(responseData)
            val token = jsonObject.getString("token")
        }
    }

     */
}