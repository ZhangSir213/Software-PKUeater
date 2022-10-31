package com.example.psycho.data

import android.content.ContentValues
import android.os.Environment
import android.util.Log
import com.example.psycho.R
import com.google.gson.Gson
import java.io.*


object Data {
    private var trueWeight:CharSequence = "182"
    private var trueHeight:CharSequence = "182"
    private var userName:CharSequence="Lemon"
    private var password:CharSequence="123456"
    private var userDataFile:String = ""
    private var dataDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path + "/PKU_Eater2")
    private var user=User("Lemon","123456",60.0,170,false)
    private var errorCode:Int=1
    private var postData:PostData=PostData("fail",BaseData(10001,"None"))
    private val fileName = "userData.json"

    public  val map=mapOf(10001 to R.string.register_wrong, 20002 to R.string.login_wrong)

    fun getLoginFlag():Boolean
    {
        userDataFile=File(dataDir, fileName).toString()
        val userFile=File(dataDir, fileName)

        if(!userFile.exists())
        {
            Log.d("Get","False")
            return false
        }
        if (getLogin()==true)
        {
            Log.d("Get","True")
            return true
        }
        Log.d("Get","False")
        return false
    }
    fun setPostData(data:PostData):Int
    {
        postData =data
        return 1
    }
    fun getState():String
    {
        return postData.status
    }
    fun getErrorCode():Int
    {
        return postData.data.errCode
    }
    //从json中获取用户体重
    fun getTrueWeight():Double{
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        return nowUser.weight
    }
    //从json中获取用户身高
    fun getTrueHeight():Int{
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        return nowUser.height
    }
    //设置用户体重
    fun setTrueWeight(double: Double){
        val fileExist = createNewFile(dataDir, fileName)
        user.weight = double
        write2Json()
    }

    fun setTrueHeight(int: Int){
        val fileExist = createNewFile(dataDir, fileName)
        user.height = int
        write2Json()
    }
    fun getUserName():CharSequence{
        val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        return nowUser.username
    }
    fun getPassword():String{
        val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        return nowUser.password
    }

    fun getLogin():Boolean{
        val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        return nowUser.Login
    }
    fun write2Json()
    {
        val json=Gson().toJson(user)
        val fw=FileWriter(userDataFile,false)
        fw.write(json)
        fw.close()
    }
    fun setUserName(seq:CharSequence)
    {
        val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        user.username=seq.toString()
        write2Json()
    }

    fun setPassword(seq:CharSequence)
    {
        val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        user.password=seq.toString()
        write2Json()
    }
    fun setLogin()
    {
        val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        user.Login=false
        write2Json()
    }
    /**
     * 在指定目录下创建文件，若文件不存在，则创建并且返回-1；若已经存在，则不创建且返回0
     */
    fun createNewFile(dirFile: File, fileName: String):Int {
        val file = File(dirFile, fileName)

        if (!dirFile.exists()) {
            //创建目录
            Log.d("File",dirFile.toString())
            dirFile.mkdirs()
        }

        Log.d("file",file.toString())
        userDataFile =file.toString()
        if (!file.exists())
        {
            Log.d("file","Try to Create")
            if(!file.createNewFile())
            {
                Log.d("cookie","Create file Failed")
                return -2
            }
            Log.d("file","Create Success")
            return -1
        }
        return 0
    }

    /**
     * 向指定文件写入指定内容
     */
    fun write2File(destFileName: String, str: String) {
        //LogUtils.d(TAG, "write2File destFileName: $destFileName content: $str")
        val fw = FileWriter(destFileName, true)

        try {
            fw.write(str)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            fw.close()
        }
    }

    /**
     * 读取Sdcard文件内容
     * @param context
     * @param filePathAndName 文件完整的路径，包含文件名，如"/sdcard/file.txt
     */
    fun getContentFromSdcard(filePathAndName: String): String? {
        Log.d(ContentValues.TAG, "getContentFromSdcard filePathAndName: $filePathAndName")
        try {
            val fr = FileReader(filePathAndName)
            var bufReader: BufferedReader? = null
            try {
                if (null == fr) {
                    return null
                }
                bufReader = BufferedReader(fr)
                var result = ""
                var line: String? = bufReader.readLine()
                while (line != null) {
                    result += line
                    line = bufReader.readLine()
                }
                //LogUtils.d(TAG, "getContentFromSdcard result: $result")
                return result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                //closeSilently(fr)
                //closeSilently(bufReader)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return null
    }



}