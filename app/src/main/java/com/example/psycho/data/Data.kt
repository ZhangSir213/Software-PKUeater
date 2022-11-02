package com.example.psycho.data

import android.content.ContentValues
import android.os.Environment
import android.util.Log
import com.example.psycho.R
import com.google.gson.Gson
import java.io.*



/**
 * created by WSH
 */
object Data {
    private var trueWeight:CharSequence = "182"
    private var trueHeight:CharSequence = "182"
    private var userName:CharSequence="Lemon"
    private var password:CharSequence="123456"
    private var userDataFile:String = ""
    private var dataDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path + "/PKU_Eater2")
    //private val canteenCnt = IntArray(18)//
    //食堂计数初始化为0，若已有数据，则用已有的
    private val avoidanceString:List<String> = listOf("重辣","普通辣","生冷","海鲜","油腻","葱","姜","蒜",
        "芥末","花椒","洋葱","芹菜","牛奶","鸡蛋","非素食","非清真")
    private var avoidanceValue: BooleanArray = BooleanArray(16)
    private var menu: List<String> = listOf("你干嘛","小黑子","只因你太美","两年半")
    private var budget: Double = 15.5
    private var user=User("Lemon","123456",60.0,170,
        false,0,2002,4,10, IntArray(18), avoidanceString,
        avoidanceValue, menu, budget, false,true)
    private val root=User("Lemon","123456",60.0,170,
        false,0,2002,4,10, IntArray(18), avoidanceString,
        avoidanceValue, menu, budget, false,true)
    private var errorCode:Int=1
    private var postData:PostData=PostData("fail",BaseData(10001,"None"))
    private val fileName = "userData.json"
    private var modify_flag=true
    private var timer=false
    private var heightVisible=false
    private var weightVisible=false

    public  val map=mapOf(10001 to R.string.register_wrong, 20002 to R.string.login_wrong,10002 to R.string.login_wrong)

    init{//构造函数,将用户信息初始化
        print("Datahhh"+ dataDir.path)
        var fileExist = createNewFile(dataDir, fileName)
        if(fileExist == 0){//文件已经存在
            val content = File(userDataFile).readText()
            user = Gson().fromJson(content, User::class.java)
        }else{//文件还不存在
            write2Json()
        }
        timer=true
    }
    fun setHeightVisible()
    {
        heightVisible=true
    }
    fun setHeightInvisible()
    {
        heightVisible=false
    }
    fun setWeightVisible()
    {
        weightVisible=true
    }
    fun setWeightInvisible()
    {
        weightVisible=false
    }
    fun getWeightVisible():Boolean
    {
        return weightVisible
    }
    fun getHeightVisible():Boolean
    {
        return heightVisible
    }
    
    fun getTimerFlag():Boolean
    {
        val userFile=File(dataDir, fileName)
        if (!userFile.exists())
            return false
        return timer
    }
    fun deleteUser()
    {
        timer=false
        File(dataDir, fileName).delete()
        user=root.copy()
        var fileExist = createNewFile(dataDir, fileName)
        timer=true
    }
    fun initUser()
    {
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user=nowUser
    }
    fun getFirstFlag():Boolean
    {
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user = nowUser
        return user.loginFirst
    }
    fun setFirstFlag()
    {
        val fileExist = createNewFile(dataDir, fileName)
        user.loginFirst=false
        write2Json()
    }

    fun getLoginFlag():Boolean
    {
        userDataFile=File(dataDir, fileName).toString()
        val userFile=File(dataDir, fileName)
        Log.d("Flag","Login")
        if(!userFile.exists())
        {
            Log.d("Get","False")
            return false
        }
        //initUser()

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
        user = nowUser
        return nowUser.weight
    }
    //从json中获取用户身高
    fun getTrueHeight():Int{
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user = nowUser
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
        user = nowUser
        return nowUser.username
    }
    fun getPassword():String{
        val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        user = nowUser
        return nowUser.password
    }

    fun getLogin():Boolean
    {
        val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        Log.d("Login","Get")
        user = nowUser
        return nowUser.Login
    }
    private fun write2Json()
    {
        val json=Gson().toJson(user)
        val fw=FileWriter(userDataFile,false)
        fw.write(json)
        fw.close()
        modify_flag=true
    }
    fun setModifyFlag()
    {
        modify_flag=false
    }
    fun getModifyFlag():Boolean
    {
        return modify_flag
    }
    fun setUserName(seq:CharSequence)
    {
        val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        user.username=seq.toString()
        write2Json()
    }

    fun setDay(day:Int)
    {
        val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        user.day=day
        write2Json()
    }
    fun getDay():Int
    {
        val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        return nowUser.day
    }

    fun setMonth(month:Int)
    {
        val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        user.month=month
        write2Json()
    }
    fun getMonth():Int
    {
        val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        return nowUser.month
    }

    fun setYear(year:Int)
    {
        val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        user.year=year
        write2Json()
    }
    fun getYear():Int
    {
        val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        return nowUser.year
    }

    fun setGender(gender:Int)
    {
        val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        user.gender=gender
        write2Json()
    }

    fun getGender():Int
    {
        val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        return nowUser.gender
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
        user.Login=true
        write2Json()
    }
    fun addCanteenCount(canteenId: Int){//增加对应食堂的计数
        val fileExist = createNewFile(dataDir, fileName)
        var content:String = ""
        if(fileExist == 0){//若用户数据已经存在,则从文件中读取用户信息
            content = File(com.example.psycho.data.Data.userDataFile).readText()
            user=Gson().fromJson(content, User::class.java)
        }
        user.canteenCount[canteenId] += 1
        write2Json()
    }

    fun getCanteenCount(canteenId: Int):Int{//获取第canteenId个食堂的计数
        val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        return nowUser.canteenCount[canteenId]
    }

    // 设置当天菜单更改标记
    fun getMenuChange(): Boolean{

        val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        return nowUser.menuChange
    }
    fun setMenuChange(flag: Boolean){
        val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        user.menuChange = flag
        write2Json()
    }

    // 设置菜单
    fun getTodayMenu(): List<String>{
        val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        return nowUser.todayMenu
    }
    fun setTodayMenu(todayMenu:List<String>){
        val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        user.todayMenu = todayMenu
        write2Json()
    }

    fun getAvoidanceString(): List<String>{//获取所有忌口类型字符串
        return avoidanceString
    }

    fun getAvoidanceType(): List<String>{//返回忌口的菜品
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user = nowUser
        val avoidancetype: MutableList<String> = mutableListOf<String>()
        for(i in user.avoidanceValue.indices){
            if(user.avoidanceValue[i]){
                avoidancetype.add(avoidanceString[i])
            }
        }
        return avoidancetype.toList()
    }

    fun getAcceptable(): List<String>{//返回不忌口的菜品
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user = nowUser
        val avoidancetype: MutableList<String> = mutableListOf<String>()
        for(i in user.avoidanceValue.indices){
            if(!user.avoidanceValue[i]){
                avoidancetype.add(avoidanceString[i])
            }
        }
        return avoidancetype.toList()
    }

    /**
     *  Param: 忌口类型名 : String
     *  return : 成功返回true，否则返回false
     **/
    fun addAvoidance(avoidanceName: String): Boolean{
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user = nowUser
        for(i in avoidanceString.indices){
            if(avoidanceName == avoidanceString[i]){
                user.avoidanceValue[i] = true
                break
            }
        }
        val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        try{
            write2Json()
        } catch(e : Exception){
            return false
        }
        return true

    }

    /**
     * Param:忌口类型名: String,
     * return: 成功返回true，否则返回false
     **/
    fun deleteAvoidance(avoidanceName: String): Boolean{
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user = nowUser
        for(i in avoidanceString.indices){
            if(avoidanceName == avoidanceString[i]){
                user.avoidanceValue[i] = false
                break
            }
        }
        val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        try{
            write2Json()
        } catch(e : Exception){
            return false
        }
        return true
    }


    /**
     * 返回标记为忌口的菜品，对应二进制位标记位1
     **/
    fun AvoidanceToAlgo():Int {
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user = nowUser
        var avoidance: Int = 0
        for(i in user.avoidanceValue.indices){
            if(user.avoidanceValue[i]){
                avoidance = avoidance or (1 shl i)
            }
        }
        return avoidance
    }

    /**
     * 获取用户预算，返回值为预算*100的Int
     * （Kernel 调用）
     */
    fun getBudget():Int{
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user = nowUser
        return (user.budget*100).toInt()
    }

    /**
     * 设置用户预算
     * param: Budget limit
     * 设置成功返回true，否则返回false
     * */
    fun setBudget(budget: Double): Boolean{
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user = nowUser
        user.budget = budget
        try{
            write2Json()
        } catch(e : Exception){
            return false
        }
        return true

    }



    /**
     * 在指定目录下创建文件，若文件不存在，则创建并且返回-1；若已经存在，则不创建且返回0
     */
    private fun createNewFile(dirFile: File, fileName: String):Int {
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
            write2Json()
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
