package com.example.psycho.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.util.Log
import com.example.psycho.R
import com.example.psycho.kernel.Kernel
import com.example.psycho.simpleGetUseFrom
import com.example.psycho.simplePostUseTo
import com.google.gson.Gson
import okio.IOException
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


/**
 * created by WSH
 */
object Data {

    enum class Plan{
        slim,strong,keep
    }
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
    private var budget: Double = 5000000.5
    private val dietLog: ArrayList<DietLog> = ArrayList<DietLog>()
    private var user=User("Lemon","123456",60.0,170,
        false,0,"2002-04-10", IntArray(18), avoidanceString,
        avoidanceValue, menu, budget, false,true, dietLog,false,Plan.keep)
    private val root=User("Lemon","123456",60.0,170,
        false,0,"2002-04-10", IntArray(18), avoidanceString,
        avoidanceValue, menu, budget, false,true, dietLog,false,Plan.keep)
    private var errorCode:Int=1
    private var postData:PostData=PostData("fail",BaseData(10001,"None"))
    private val fileName = "userData3.json"
    private var modify_flag=true
    private var timer=false
    private var heightVisible=false
    private var weightVisible=false
    private var Carolie=100
    private var idCode=100
    private var plan=Plan.keep
    public  val map=mapOf(10001 to R.string.register_wrong, 20002 to R.string.login_wrong,10002 to R.string.login_wrong)
    private  var mysqlhelper: SQLiteOpenHelper? = null
    var DB_NAME = "Pku-Eater.db" //数据库名称
    var TABLE_NAME = "USER" //表名称
    var CURRENT_VERSION = 1 //当前的最新版本，如有表结构变更，该版本号要加一
    var version=2
    fun update(context:Context,column:String,value:String)
    {
        val dbHelper=MyDatabaseHelper(context, DB_NAME,version)
        dbHelper.writableDatabase
        val db=dbHelper.writableDatabase
        val values=ContentValues()
        values.put(column,value)
        db.update(TABLE_NAME,values,"uid=?", arrayOf(idCode.toString()))
        db.close()
    }
    fun query(context: Context,column: String):String
    {
        val dbHelper=MyDatabaseHelper(context, DB_NAME,version)
        dbHelper.writableDatabase
        val db=dbHelper.writableDatabase
        val condition="uid=$idCode"
        val sql = "select $column from $TABLE_NAME where $condition;"
        val cursor=db.rawQuery(sql,null)
        var value=""
        if (cursor.moveToFirst()) {
            while (true) {
                value=cursor.getString(0)
                if (cursor.isLast) {
                    break
                }
                cursor.moveToNext()
            }
        }
        cursor.close()
        db.close()
        return value
    }

    init{//构造函数,将用户信息初始化
        //write2Json()

        var fileExist = createNewFile(dataDir, fileName)
        if(fileExist == 0){//文件已经存在
            val content = File(userDataFile).readText()
            user = Gson().fromJson(content, User::class.java)
            Log.d("Init","Init User")
        }else{//文件还不存在
            //user.dietlog.clear()
            write2Json()
        }
        if(user.dietlog == null)
            Log.d("init:","dietlog==null")
        timer=true
    }

    fun getLoginFlag(context:Context):Boolean
    {
        val value=query(context,"login").toInt()
        if (value==1)
        {
            return true
        }
        return false
    }
    fun setFirstFlag(context: Context)
    {
        try {
            update(context,"login","0")
        }
        catch (e:IOException)
        {
            e.printStackTrace()
        }
    }

    fun setUserName(context: Context,username: String)
    {
        try {
            update(context,"name",username)
        }
        catch (e:IOException)
        {
            e.printStackTrace()
        }
    }
    fun setPassword(context: Context,password: String)
    {
        try {
            update(context,"password",password)
        }
        catch (e:IOException)
        {
            e.printStackTrace()
        }
    }

    fun getTime():Int
    {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = current.format(formatter)

        Log.d("Time","当前日期和时间为: $formatted")
        return current.hour
    }
    fun getMeal():Int
    {
        var meal=4
        val hour= getTime()
        Log.d("Time",hour.toString())
        if ((6<=hour)&&(hour<=9))
        {
            meal=1
        }
        else if((11<=hour)&&(hour<=13))
        {
            meal=2
        }
        else if((17<=hour)&&(hour<=19))
        {
            meal=3
        }
        return meal
    }
    fun update()
    {
        val url = "http://47.94.139.212:3000/user/update"
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user = nowUser
        val avoidance= AvoidanceToAlgo()
        var state=0
        if (user.plan==Plan.slim)
        {
            state=0
        }
        else if(user.plan==Plan.strong)
        {
            state=1
        }
        else
        {
            state=2
        }
        Log.d("Update",user.birthday)

        val map = mapOf("id" to idCode,"gender" to user.gender,"birthday" to user.birthday,
            "avoidance" to avoidance,"weight" to user.weight.toInt(),"height" to user.height.toInt(),"state" to state)

        simplePostUseTo(url, map)
        Log.d("Finish","update")
        if (getState()=="fail")
        {
            throw IOException("Error Update")
        }


    }
    fun setHeightVisible()
    {
        heightVisible=true
    }
    fun setHeightInvisible()
    {
        heightVisible=false
    }
    fun setPlan(context: Context,state:Int)
    {
        update(context,"state",state.toString())
    }
    fun setPlan(plan1:Plan)
    {
        val fileExist = createNewFile(dataDir, fileName)
        user.plan=plan1
        write2Json()
    }
    fun getPlan(context: Context):Plan
    {
        val value= query(context,"state").toInt()
        if (value==0)
        {
            return Plan.slim
        }
        else if(value==1)
        {
            return Plan.keep
        }
        else
        {
            return Plan.strong
        }
    }

    fun getPlan():Plan
    {
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user = nowUser
        return user.plan
    }

    fun setBirthday(context: Context,birthday: String)
    {
        try {
            update(context,"birthday",birthday)
        }
        catch (e:IOException)
        {
            e.printStackTrace()
        }
    }
    fun setBirthday(birthday:String)
    {
        val fileExist = createNewFile(dataDir, fileName)
        user.birthday=birthday
        write2Json()
    }
    fun getBirthday():String
    {
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user = nowUser
        return user.birthday
    }
    fun setCarolie(car :Int)
    {
        Carolie=car
    }
    fun getCarolie():Int
    {
        return Carolie
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
    fun deleteUser(context: Context)
    {
        timer=false
        update(context,"name","root")
        update(context,"password","123456")
        timer=true
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
    fun getidCode():Int
    {
        return idCode
    }
    fun setPostData(data:PostData,update:Boolean,context: Context?):Int
    {
        postData =data
        if((postData.status=="success")&&(update==true))
        {
            Log.d("Login", postData.data.toString())
            setBirthday(context!!, postData.data.birthday)
            setBirthday(postData.data.birthday)
            setUserName(context!!,postData.data.name)
            setUserName(postData.data.name)
            setGender(context!!,postData.data.gender)
            setGender(postData.data.gender)
            update(context!!,"uid", postData.data.id.toString())
            idCode= postData.data.id
            setTrueWeight(context!!,postData.data.weight.toDouble())
            setTrueHeight(context!!,postData.data.height)

            setTrueWeight(postData.data.weight.toDouble())
            setTrueHeight(postData.data.height)
            setAvoidance(context!!, postData.data.avoidance)

            for(i in avoidanceValue.indices)
            {
                val j=1 shl i
                if((postData.data.avoidance and j)!=0)
                {
                    user.avoidanceValue[i]=true
                }
            }
            setPlan(context!!, postData.data.state)
            if (postData.data.state==0)
            {
                setPlan(Plan.slim)
            }
            else if(postData.data.state==1)
            {
                setPlan(Plan.strong)
            }
            else
            {
                setPlan(Plan.keep)
            }

        }
        return 1
    }

    private fun setAvoidance(context: Context,avoidance:Int) {
        update(context,"avoidance",avoidance.toString())
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
    fun setTrueWeight(context: Context,double: Double)
    {
        update(context,"weight",double.toString())
    }

    fun setTrueWeight(double: Double){
        val fileExist = createNewFile(dataDir, fileName)
        user.weight = double
        write2Json()

    }

    fun setTrueHeight(context: Context,int: Int)
    {
        update(context,"weight",int.toString())
    }

    fun setTrueHeight(int: Int){
        val fileExist = createNewFile(dataDir, fileName)
        user.height = int
        write2Json()
    }
    fun getUserName():String{
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

    fun setGender(context: Context,gender: Int)
    {
        update(context,"gender",gender.toString())
    }
    fun setGender(gender:Int)
    {
        val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        user.gender=gender
        write2Json()
    }
    fun getGender(context: Context):Int
    {
        val value= query(context,"gender").toInt()
        return value
    }
    fun getGender():Int
    {
        val fileExist = createNewFile(dataDir, fileName)
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

    fun setAvoidanceChange(flag: Boolean){
        val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        user.avoidanceFlag = flag
        write2Json()
        update()
    }
    fun getAvoidanceChange(): Boolean{
        val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        return nowUser.avoidanceFlag
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
                user.avoidanceValue[i] = !user.avoidanceValue[i]
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
                user.avoidanceValue[i] = !user.avoidanceValue[i]
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
    **增加日志记录，成功则返回true
    * param: meal=1-早餐,2-午餐,3-晚餐,4-加餐
     */
    /*fun addDietLog(meal: Int, foodName:String):Boolean{
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user = nowUser
        if(user.dietlog == null){
            Log.d("data","user.dietlog == null")
        }
        user.dietlog.add(DietLog(meal,foodName))
        try{
            write2Json()
        }catch (e: Exception){
            return false
        }
        return true
    }
     */

    /**
     *获取对应类型的日志
     */
    fun getDietLog(meal: Int):List<String>{
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user = nowUser
        val logFoodList: MutableList<String> = mutableListOf<String>()
        for(i in user.dietlog.indices){
            if(user.dietlog[i].meal == meal){
                logFoodList.add(user.dietlog[i].foodName)
            }
        }
        return logFoodList.toList()
    }

    /**
    *若用户日志为空，返回false,否则返回true
     */
    fun checkDietLog():Boolean{
        Log.d("checkDietLog","enter")
        /*
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user = nowUser
        if(user.dietlog.size == 0){
            return false
        }*/
        for (i in 1..4){
            val foodName = getLogFromServer(i)
            if(!foodName.isEmpty()){
                return true
            }
        }
        return false
    }

    /**
     * 将用户日志初始化
     */
    fun initDietLog():Boolean{
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content,User::class.java)
        user = nowUser
        user.dietlog.clear()
        try{
            write2Json()
        }catch (e:Exception){
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
            Log.d("file",dirFile.toString())
            dirFile.mkdirs()
        }

        Log.d("file",file.toString())
        userDataFile =file.toString()

        if (!file.exists())
        {
            Log.d("file","Try to Create")
            if(!file.createNewFile())
            {
                Log.d("file","Create file Failed")
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

    fun getLogFromServer(meal: Int, uid: Int = idCode): List<String> {
        val url = "http://47.94.139.212:3000/journal/listbyusr"
        val map = mapOf("uid" to uid.toString())
        val responseData = simpleGetUseFrom(url, map)
        val getResponse = Gson().fromJson(responseData, DietLogGet::class.java)
        val dietLogList = getResponse.data
        Log.d("dietlog",dietLogList.toString())
        val foodList = Kernel.getFoodList()
        var res = listOf<String>()
        for (dietLog in dietLogList) {
            if (dietLog.meal == meal) {
                for (food in foodList) {
                    if (food.id == dietLog.fid) {
                        res = res.plusElement(food.name)
                    }
                }
                //res = res.plusElement(dietLog.foodName)
                //Log.d("getlog",(dietLog.foodName == null).toString())
            }
        }
        return res
    }
    private fun updateDeletedLogToServer(id:Int)
    {
        val url = "http://47.94.139.212:3000/journal/delete"
        Log.d("delete",id.toString())
        Log.d("delete", idCode.toString())
        val map = mapOf("uid" to idCode.toString(),"id" to id.toString())
        try {
            simplePostUseTo(url, map)
            Log.d("delete","Success")
        }
        catch (e:IOException)
        {
            Log.d("delete","Error")
        }
    }
    fun deleteLog(meal:Int)
    {
        val url = "http://47.94.139.212:3000/journal/listbyusr"
        val map = mapOf("uid" to idCode.toString())
        val responseData = simpleGetUseFrom(url, map)
        val getResponse = Gson().fromJson(responseData, DietLogGet::class.java)
        val dietLogList = getResponse.data
        if (dietLogList.isEmpty())
            return
        val dietLog=dietLogList[dietLogList.size-1]
        updateDeletedLogToServer(dietLog.id)
    }
    fun postLogToServer(fid: Int, meal: Int, uid: Int = idCode, calorie: Int = 0, price: Int = 0) {
        val url = "http://47.94.139.212:3000/journal/create"
        Log.d("postlog",fid.toString())
        Log.d("userId", idCode.toString())
        if (fid != 0) {
            val map = mapOf("uid" to uid, "fid" to fid, "meal" to meal)
            simplePostUseTo(url, map)
        } else {
            val map = mapOf("uid" to uid, "fid" to 0, "meal" to meal,
                "calorie" to calorie, "price" to price)
            simplePostUseTo(url, map)
        }
    }

    fun getCalorieFromServer(uid: Int = idCode): Int {
        val url = "http://47.94.139.212:3000/journal/listbyusr"
        val map = mapOf("uid" to uid.toString())
        val responseData = simpleGetUseFrom(url, map)
        val getResponse = Gson().fromJson(responseData, DietLogGet::class.java)
        val dietLogList = getResponse.data
        var res:Int = 0
        for (dietLog in dietLogList) {
            res += dietLog.calorie
        }
        return res
    }
}
