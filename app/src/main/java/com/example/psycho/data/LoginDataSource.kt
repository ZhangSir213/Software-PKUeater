package com.example.psycho.data

import android.content.Context
import android.util.Log
import com.example.psycho.data.model.LoggedInUser
import com.example.psycho.simplePostUseFrom
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String,register:Boolean,context:Context): Result<LoggedInUser> {
        val globalFile = Data
        try {
            if (register==false)
            {
                Log.d("username", username)
                Log.d("password", password)
                globalFile.setUserName(context,username)
                globalFile.setPassword(context,password)

                //globalFile.setUserName(context, username)
                //globalFile.setPassword(context, password)
                val map = mapOf("name" to username, "password" to password)
                val url = "http://47.94.139.212:3000/user/login"
                simplePostUseFrom(url, map,true,context)
                Log.d("Finish","login")
                if (globalFile.getState()=="fail")
                {
                    throw IOException("Error Login")
                }
                globalFile.setLogin(context)
                globalFile.setFirstFlag(context)
                val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
                return Result.Success(fakeUser)
            }
            else
            {
                Log.d("register","True")
                Log.d("username", username)
                Log.d("password", password)
                globalFile.setUserName(context, username)
                globalFile.setPassword(context, password)
                val map = mapOf("name" to username, "password" to password)
                val url = "http://47.94.139.212:3000/user/register"
                simplePostUseFrom(url, map,true,context)
                Log.d("Finish","register")
                if (globalFile.getState()=="fail")
                {
                    throw IOException("Error Register")
                }
                globalFile.setLogin(context)
                val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
                return Result.Success(fakeUser)
            }
        } catch (e: Throwable)
        {
            Log.d("Error",globalFile.getErrorCode().toString())
            return Result.Error_msg(globalFile.getErrorCode())
        }
    }

    fun logout() {
    }
}