package com.example.psycho.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.util.Log
import com.example.psycho.R
import com.example.psycho.kernel.Kernel
import com.example.psycho.resource.AvoidanceAdapter
import com.example.psycho.simpleGetUseFrom
import com.example.psycho.simplePostUseTo
import com.google.gson.Gson
import okio.IOException
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


/**
 * created by WSH
 */
object Data {

    enum class Plan{
        slim,strong,keep
    }
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
    private var menuChange = false
    private var avoidanceChange = false
    private var timer=false
    private var heightVisible=false
    private var weightVisible=false
    private var Carolie=100
    private var idCode=100
    private var plan=Plan.keep
    private var todayMenu:List<String> = listOf("empty")
    public  val map=mapOf(10001 to R.string.register_wrong, 20002 to R.string.login_wrong,10002 to R.string.login_wrong)
    private  var mysqlhelper: SQLiteOpenHelper? = null
    var DB_NAME = "Pku-Eater.db" //数据库名称
    var TABLE_NAME = "USER" //表名称
    var CURRENT_VERSION = 3 //当前的最新版本，如有表结构变更，该版本号要加一

    fun initSQL(context:Context)
    {
        val dbHelper=MyDatabaseHelper(context, DB_NAME, CURRENT_VERSION)
        dbHelper.writableDatabase
        val db=dbHelper.writableDatabase
        val cursor: Cursor =
            db.query(TABLE_NAME, null, null, null, null, null, null, null)
        if(cursor.getCount()<=0)
        {
            val userRoot=ContentValues().apply {
                put("uid",100)
                put("name","Root")
                put("weight",50)
                put("height",170)
                put("state",0)
                put("birthday","1111-11-11")
                put("gender",1)
                put("avoidance",0)
                put("login",0)
                put("loginFirst",0)
                put("password","caonima")
                put("budget",30)
            }
            db.insert("USER",null,userRoot)
        }
        else
        {
            cursor.moveToFirst()
            idCode=cursor.getInt(cursor.getColumnIndex("uid"))
        }
    }
    private fun update(context:Context,column:String,value:String)
    {

        val dbHelper=MyDatabaseHelper(context, DB_NAME, CURRENT_VERSION)
        dbHelper.writableDatabase
        val db=dbHelper.writableDatabase
        val values=ContentValues()
        values.put(column,value)
        db.update(TABLE_NAME,values,"uid=?", arrayOf(idCode.toString()))
        db.close()
    }
    fun query(context: Context,column: String):String
    {
        val dbHelper=MyDatabaseHelper(context, DB_NAME, CURRENT_VERSION)
        dbHelper.writableDatabase
        val db=dbHelper.writableDatabase
        val condition="uid=$idCode"
        val sql = "select $column from $TABLE_NAME where $condition;"
        val cursor=db.rawQuery(sql,null)
        var value=""
        if (cursor.moveToFirst()) {
            while (true) {
                Log.d("Cur",cursor.getColumnName(0))
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

        if(user.dietlog == null)
            Log.d("init:","dietlog==null")
        timer=true
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
    fun update(context: Context)
    {
        val url = "http://47.94.139.212:3000/user/update"
        var state=0

        Log.d("Update", getBirthday(context))
        val plan1=getPlan(context)
        if (plan1==Plan.slim)
            state=0
        else if(plan1==Plan.keep)
            state=1
        else
            state=2
        val map = mapOf("id" to idCode,"gender" to getGender(context),"birthday" to getBirthday(context),
            "avoidance" to getAvoidance(context),"weight" to getTrueWeight(context).toInt(),"height" to getTrueHeight(context).toInt(),"state" to state
        )

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
    //完成对setPlan调用的修改
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

    fun getBirthday(context:Context):String{
        val birthday:String = query(context,"birthday")
        return birthday
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
        /*val userFile=File(dataDir, fileName)
        if (!userFile.exists())
            return false*/
        return timer
    }
    fun deleteUser(context: Context)
    {
        timer=false
        update(context,"name","root")
        update(context,"password","123456")
        update(context,"login","0")
        timer=true
    }


    fun getFirstFlag(context:Context):Boolean{
        val firstFlag:Boolean = query(context,"loginFirst").toBoolean()
        return firstFlag
    }

    fun getFirstFlag():Boolean
    {
        val content = File(userDataFile).readText()
        val nowUser = Gson().fromJson(content, User::class.java)
        user = nowUser
        return user.loginFirst
    }

    fun setFirstFlag(context: Context)
    {
        try {
            update(context,"loginFirst","0")
        }
        catch (e:IOException)
        {
            e.printStackTrace()
        }
    }


    fun getLoginFlag(context:Context):Boolean
    {
        val value=query(context,"login")
        if (value == "1")
        {
            return true
        }
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
            //setBirthday(postData.data.birthday)
            setUserName(context!!,postData.data.name)
            //setUserName(postData.data.name)
            setGender(context!!,postData.data.gender)
            //setGender(postData.data.gender)
            update(context!!,"uid", postData.data.id.toString())
            idCode= postData.data.id
            setTrueWeight(context!!,postData.data.weight.toDouble())
            setTrueHeight(context!!,postData.data.height)

            //setTrueWeight(postData.data.weight.toDouble())
            //setTrueHeight(postData.data.height)
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
        }
        return 1
    }

    private fun setAvoidance(context: Context,avoidance:Int) {
        update(context,"avoidance",avoidance.toString())
    }
    private  fun getAvoidance(context: Context):Int
    {
        val value=query(context,"avoidance").toInt()
        return value
    }
    fun getState():String
    {
        return postData.status
    }
    fun getErrorCode():Int
    {
        return postData.data.errCode
    }

    fun getTrueWeight(context: Context):Double{
        val weight:Double = query(context,"weight").toDouble()
        return weight
    }

    //从json中获取用户体重

    fun getTrueHeight(context: Context):Int{
        val height:Int = query(context,"height").toInt()
        return height
    }

    //从json中获取用户身高
    //设置用户体重
    fun setTrueWeight(context: Context,double: Double)
    {
        update(context,"weight",double.toString())
    }


    fun setTrueHeight(context: Context,int: Int)
    {
        update(context,"height",int.toString())
    }

    fun getUserName(context: Context):String{
        val name:String = query(context, "name").toString()
        return name
    }



    fun getPassword(context: Context):String{
        val password: String = query(context, "password").toString()
        return password
    }



    fun getLogin(context: Context):Boolean{
        val login:Boolean = query(context, "login").toBoolean()
        return login
    }

    //唯一调用旧版本getLogin的函数未被其他模块调用

    fun setModifyFlag()
    {
        modify_flag=false
    }
    fun getModifyFlag():Boolean
    {
        return modify_flag
    }




    fun setGender(context: Context,gender: Int)
    {
        update(context,"gender",gender.toString())
    }

    fun getGender(context: Context):Int
    {
        val value= query(context,"gender").toInt()
        return value
    }

    fun setLogin(context:Context)
    {
        update(context,"login","1")
    }
    /*fun addCanteenCount(canteenId: Int){//增加对应食堂的计数
        val fileExist = createNewFile(dataDir, fileName)
        var content:String = ""
        if(fileExist == 0){//若用户数据已经存在,则从文件中读取用户信息
            content = File(com.example.psycho.data.Data.userDataFile).readText()
            user=Gson().fromJson(content, User::class.java)
        }
        user.canteenCount[canteenId] += 1
        write2Json()
    }*/

    /*
    fun getCanteenCount(canteenId: Int):Int{//获取第canteenId个食堂的计数
        val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        return nowUser.canteenCount[canteenId]
    }

     */

    // 设置当天菜单更改标记
    fun getMenuChange(): Boolean{
        return menuChange
        /*
        val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        return nowUser.menuChange

         */
    }
    fun setMenuChange(flag: Boolean){
        menuChange = flag
        /*val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        user.menuChange = flag
        write2Json()

         */
    }

    fun setAvoidanceChange(flag: Boolean){
        /*val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        user.avoidanceFlag = flag
        write2Json()
        update()*/
        avoidanceChange = flag
    }
    fun getAvoidanceChange(): Boolean{
        /*val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        return nowUser.avoidanceFlag
         */
        return avoidanceChange
    }

    // 设置菜单
    fun getTodayMenu(): List<String>{
        /*
        val content = File(userDataFile).readText()
        val nowUser=Gson().fromJson(content, User::class.java)
        return nowUser.todayMenu*/
        return todayMenu
    }
    fun setTodayMenu(todayMenu_:List<String>){
        /*val fileExist = createNewFile(dataDir, fileName)//打开/创建文件
        user.todayMenu = todayMenu_
        write2Json()*/
        todayMenu = todayMenu_
    }

    fun getAvoidanceString(): List<String>{//获取所有忌口类型字符串
        return avoidanceString
    }

    fun getAvoidanceType(context: Context): List<String>{//返回忌口的菜品
        val avoidanceType: MutableList<String> = mutableListOf<String>()
        val avoidance: Int = query(context,"avoidance").toInt()
        for(i in avoidanceString.indices){
            if((avoidance and (1 shl i)) != 0){
                avoidanceType.add(avoidanceString[i])
            }
        }
        return avoidanceType.toList()
    }



    fun getAcceptable(context: Context): List<String>{//返回接受的菜品
        val avoidanceType: MutableList<String> = mutableListOf<String>()
        val avoidance: Int = query(context,"avoidance").toInt()
        for(i in avoidanceString.indices){
            if((avoidance and (1 shl i)) == 0){
                avoidanceType.add(avoidanceString[i])
            }
        }
        return avoidanceType.toList()
    }



    fun addAvoidance(context: Context, avoidanceName: String):Boolean{
        var avoidance: Int = query(context, "avoidance").toInt()
        for(i in avoidanceString.indices){
            if(avoidanceName == avoidanceString[i]){
                avoidance = avoidance.or((1 shl i))
            }
        }
        try {
            update(context, "avoidance", avoidance.toString())
        }catch(e : okio.IOException){
            return false
        }
        return true
    }
    /**
     *  Param: 忌口类型名 : String
     *  return : 成功返回true，否则返回false
     **/

    fun deleteAvoidance(context: Context,avoidanceName: String): Boolean{
        var avoidance: Int = query(context, "avoidance").toInt()
        for(i in avoidanceString.indices){
            if(avoidanceName == avoidanceString[i]){
                avoidance = avoidance.and((1 shl i).inv())
            }
        }
        Log.d("Avoidance",avoidance.toString())
        update(context, "avoidance", avoidance.toString())
        return true
    }
    /**
     * Param:忌口类型名: String,
     * return: 成功返回true，否则返回false
     **/



    fun AvoidanceToAlgo(context: Context):Int {
        val avoidance:Int = query(context, "avoidance").toInt()
        return avoidance
    }
    /**
     * 返回标记为忌口的菜品，对应二进制位标记位1
     **/


    fun getBudget(context:Context):Int{
        val budget:Double = query(context, "budget").toDouble()
        return (budget).toInt()
    }

    /**
     * 获取用户预算，返回值为预算*100的Int
     * （Kernel 调用）
     */

    fun setBudget(context: Context, budget: Double): Boolean{
        try{
            update(context, "budget", budget.toString())
        }catch(e : okio.IOException){
            return false
        }
        return true

    }


    /**
     * 设置用户预算
     * param: Budget limit
     * 设置成功返回true，否则返回false
     * */

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
