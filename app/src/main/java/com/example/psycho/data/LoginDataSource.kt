package com.example.psycho.data

import android.util.Log
import com.example.psycho.data.model.LoggedInUser
import com.example.psycho.simplePostUseFrom
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String,register:Boolean): Result<LoggedInUser> {
        val globalFile = Data
        try {
            if (register==false)
            {
                Log.d("username", username)
                Log.d("password", password)
                globalFile.setUserName(username)
                globalFile.setPassword(password)
                val map = mapOf("name" to username, "password" to password)
                val url = "http://47.94.139.212:3000/user/login"
                simplePostUseFrom(url, map,true)
                Log.d("Finish","login")
                if (globalFile.getState()=="fail")
                {
                    throw IOException("Error Login")
                }
                globalFile.setLogin()
                globalFile.setFirstFlag()
                val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
                return Result.Success(fakeUser)
            }
            else
            {
                Log.d("register","True")
                Log.d("username", username)
                Log.d("password", password)
                globalFile.setUserName(username)
                globalFile.setPassword(password)
                val map = mapOf("name" to username, "password" to password)
                val url = "http://47.94.139.212:3000/user/register"
                simplePostUseFrom(url, map,true)
                Log.d("Finish","register")
                if (globalFile.getState()=="fail")
                {
                    throw IOException("Error Register")
                }
                globalFile.setLogin()
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