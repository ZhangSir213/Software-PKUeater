package com.example.psycho.kernel

import com.example.psycho.R
import com.example.psycho.data.Data
import com.example.psycho.data.Food
import com.example.psycho.data.FoodGet
import com.example.psycho.resource.CanteenAdapter
import com.example.psycho.simpleGetUseFrom
import com.google.gson.Gson
import java.lang.Math.*
import java.text.SimpleDateFormat
import java.util.function.DoubleUnaryOperator
import java.util.function.Predicate
import kotlin.math.roundToInt
import kotlin.random.Random

object Kernel {
    //    var food = arrayOfNulls<Array<kernel.Food?> >(4)
    private var CanteenList: List<String> = listOf(
        "空食堂占位！","农园一层", "农园二层", "燕南一层", "家园一层",
        "家园二层", "家园三层", "家园四层", "松林包子",
        "学一食堂", "学五食堂", "勺园一层", "勺园二层",
        "佟园餐厅", "勺西餐厅", "勺中餐厅", "艺园食堂")
    private var PreferCanteen: String = "随机食堂"//想去的食堂
    private var Avoidance: Int = 0//忌口
    private var Budget: Int = 10000//预算，愿意为一顿饭花多少钱
    private var CalorieLimit: Int = 10000//一顿饭应该摄入多少卡路里
    var Canteen:String = ""//推荐该食堂的菜品
    val mealMap= mapOf("辣子鸡" to R.drawable.chicken,"油麦菜" to R.drawable.vagetable,
        "一两米饭" to R.drawable.rice,"红豆粥" to R.drawable.redbean,"奥尔良鸡腿" to R.drawable.chickenleg,
        "土豆丝" to R.drawable.potato,"枣糕" to R.drawable.cake,"煎饼果子" to R.drawable.pancake,"真tm的好" to R.drawable.pkueater)
    /*
        PreferCanteen = "农园一层"等
                      = “随机食堂”
                      = "换个推荐"
                      = “换个食堂”
     */
    var food = Array<Food>(1000, {i: Int -> Food()})     //各类食物信息
    var nwfood = Array<Food>(1000, {i: Int -> Food()})    //排除忌口等因素后本次可以推荐的食物
    var candidate = Array<Food>(10, {i: Int -> Food()})                  //每次搜索的推荐结果
    var recommend = Array<Food>(10, {i: Int -> Food()})                  //最终推荐结果
    var cnt = 0                       //记录各类食物数量
    var ncnt = 0                      //记录当前可供推荐的各类食物数量
    var ccnt = 0                      //记录搜索出来的推荐结果的数量
    var rcnt = 0                      //记录最终推荐结果的数量
    var rec_calorie = 0       //推荐结果的卡路里总量
    var rec_distance = 0      //推荐结果的步行距离
    var lastRecommendationHash = 0  //上一次推荐的哈希值

    init
    {
        //构造函数
        val responseData= simpleGetUseFrom("http://47.94.139.212:3000/food/list",null)
        val getResponse=Gson().fromJson(responseData,FoodGet::class.java)
        val FoodList=getResponse.data
        val length=FoodList.size
        for (i in 0 until  length)
        {
            food[++cnt]=FoodList.get(i)
        }
    }

    fun setPrefer(string: String): Boolean{
        PreferCanteen = string
        return true
    }
    fun getPictureId(dish:String):Int?
    {
        if (dish in mealMap.keys)
        {
            return mealMap.get(dish)
        }
        return R.drawable.pkueater
    }
    fun getPrefer(): String{
        return PreferCanteen
    }
    fun getAllCanteen(): List<String>{
        return CanteenList
    }
    fun getFoodlist(): List<String>{
        var res: List<String> = listOf()
        for(i in 1..cnt)
            res = res.plusElement(food[i].name)
        return res
    }
    fun getCanteenfood(Canteen: String): List<String>{
        var res: List<String> = listOf()
        for(i in 1..cnt)
            if(Canteen == CanteenList[food[i].canteenId])
                res = res.plusElement(food[i].name)
        return res
    }

    fun calcCalorie(Gender:Int, Weight: Double, Height: Int,Age: Int,Goat: Int): Int{
        // Weight kg   Height cm
        var calorie: Double = 0.1
        if(Gender == 1)//Man
            calorie = 10*Weight + 6.25*Height - 5*Age + 5
        else//Woman
            calorie = 10*Weight + 6.25*Height - 5*Age - 161

        calorie += Goat * 250
        return calorie.roundToInt()
    }

    fun Hash(): Int{
        var hash = 0
        var M = 993244853
        for(i in 1..ccnt){
            hash = (hash+
                    candidate[i].window*2002725+
                    candidate[i].calorie*189854+
                    candidate[i].price*93)%M
        }
        hash = (hash+ccnt*199)%M
        return hash
    }
    fun Dist(i: Int, j: Int):Int {
        if(i > ccnt || j > ccnt) return 0
        var res = candidate[i].window - candidate[j].window
        if(res < 0) res = -res
        return res
    }
    fun dfs(CalorieTot: Int, Cost: Int, x: Int,Type: Int): Unit{
        //    print("DFS("+CarlorieTot+","+Cost+","+x+","+Type+")\n")
        if(CalorieTot > CalorieLimit) return //卡路里超标则停止
        if(Cost > Budget) return //预算超标则停止
        if(x == ncnt+1||Type.and(7) == 7){//菜品遍历完，结束推荐
            //I. 当前搜到的组合不优
            if(Type.and(7) < 7) return//一定要主食肉素齐全
            var distance = Dist(1, 2) + Dist(1,3) + Dist(2,3)
            if(rcnt > 0){//已有之前的搜索结果，需要两者进行比较
                //i. 摄入卡路里太少了不行
                if(CalorieTot < rec_calorie-100) return
                //ii. 走的距离太远了不行
                if(distance > rec_distance+3) return
            }

            //II. 随机因素：两个几乎等价的结果，有50%概率更新
            var update: Int = round(random()*1000).toInt() % 2
            if(rcnt == 0) update = 1//第一组结果，直接保留
            else {//如果该组结果明显优于上组，直接保留
                //i. 少走大量的路
                if(distance < rec_distance-5) update = 1
                //ii. 比上组结果更加接近calorielimit
                if(CalorieTot > rec_calorie+100) update = 1
            }
            if(update == 0) return

            //III. 搜到和上次相同的推荐
            var recommendationHash = Hash()
            if(PreferCanteen == "换个推荐" &&
                recommendationHash == lastRecommendationHash)
                return

            //顺利更新答案
            rcnt = 0
            for(i in 1..ccnt) {
                recommend[++rcnt] = candidate[i]
                //    print("food:"+candidate[i]+"\n")
            }
            rec_calorie = CalorieTot
            rec_distance = distance
            lastRecommendationHash = recommendationHash
            //print("Update It!!\n")
            return
        }
        if(Type.and(nwfood[x].type) == 0){
            candidate[++ccnt] = nwfood[x]
            dfs(CalorieTot+nwfood[x].calorie, Cost+ nwfood[x].price, x+1,Type+nwfood[x].type)
            ccnt--  //回溯
        }
        dfs(CalorieTot, Cost, x+1, Type)  //不选择这个食物
        return
    }

    fun getResult():List<String> {
        //Step0 读取食物数据
        /*
            食物数据保存在二维数据food[4][100]中，food[i]存储第i类食物（type为i）
            读取时，可以先实例一个临时的food对象 F 存储数据，
            得到type之后，food[type][++cnt[type]]= F
            Upd：在init构造函数中已经读取
         */
        print("所有的食物数据：\n")
        for(j in 1..cnt) {
            print(food[j])
            print('\n')
        }


        //Step1 计算去哪个食堂
        var ableCanteen = intArrayOf(2,5,16)
        if(PreferCanteen == "随机食堂"){
            //默认情况
            Canteen = Kernel.CanteenList[ableCanteen.random()]
        }
        else if(PreferCanteen == "换个推荐"){

        }
        else if(PreferCanteen == "换个食堂"){
            var lastCanteen:String = Canteen
            while(Canteen == lastCanteen)
                Canteen = Kernel.CanteenList[ableCanteen.random()]
        }
        else
        //有指定的食堂
            Canteen = PreferCanteen

        print("这次要去的食堂："+Canteen+'\n')



        //Step2 获得推荐菜品
        //i. 获得忌口等信息

        val Mydata = Data //定义在最上面
        //计算卡路里（xzy电脑安卓模拟机坏了，，inline方便调试）
        /*
            Calculate the Calorie
            查论文目前最先进的方法：
            Mifflin St.Jeor Formulas

            减肥一公斤，减少摄入7500kcal,一个月每天少250卡，增肥同理
        */
        var Gender = Mydata.getGender()
        var Weight = Mydata.getTrueWeight()
        var Height = Mydata.getTrueHeight()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        var Age = 2022-sdf.parse(Mydata.getBirthday()).year
        var Goal = 0//keep
        if(Mydata.getPlan() == Data.Plan.slim) Goal = -1
        if(Mydata.getPlan() == Data.Plan.strong) Goal = 1
        var Cal = calcCalorie(Gender,Weight, Height, Age, Goal)

        Avoidance = Mydata.AvoidanceToAlgo()
        Budget = Mydata.getBudget()
        CalorieLimit = Cal/2



        //ii. 筛选出本次可以推荐的菜品集合
        ncnt = 0
        for(i in 1..cnt) {
            if((Canteen == CanteenList[food[i].canteenId]) && //食堂对应
                (Avoidance.and(food[i].avoidance) == 0)       //忌口对应
            )
                nwfood[++ncnt] = food[i]
        }
        print("筛选后的食物数据：\n")
        for(j in 1..ncnt) {
            print(nwfood[j])
            print('\n')
        }
        //iii. 获得本次推荐
        dfs(0,0,1,0)
        print("推荐的食物：\n")
        for(i in 1..rcnt) {
            print(recommend[i])
            print('\n')
        }

        var result = listOf<String>(Canteen)
        for(i in 1..rcnt)
            result = result.plusElement(recommend[i].name)
        return result
    }
}

fun main(){
    var xzy = Kernel
    print(xzy.getFoodlist())
    print("\n")
    print(xzy.getCanteenfood("家园二层"))
}
/*
README 1113:
    推荐算法大纲已实现
    新增获取所有菜品的接口
    新增获取指定食堂所有菜品的接口

    待实现：
    1、减肥与增重的卡路里计算功能
    2、随机算法好像不够随机，可以更优化
 */
/*
README1108:
    更新了推荐算法：
    1、筛选出不符合忌口的食物
    2、推荐满足预算的组合
    3、推荐满足卡路里摄入限制的组合，同时选择卡路里摄入最接近限制的组合
    4、推荐走动距离最短可获取的组合

    待实现：
    Data对象获得用户忌口、预算、卡路里限制等数据
    卡路里限制的计算
    菜品更加丰富
    算法的随机化
 */
/*
README1030:
    实现功能按照规定所说的三个接口：
        get/setPrefer
        getAllCanteen
        getResult
    对getResult做如下说明：
    1、返回推荐只和PreferCanteen的取值有关:
        PreferCanteen = "农园一层"等
                      = “随机食堂”
                      = "换个推荐"
                      = “换个食堂”
       根据指定的取值获得推荐
    2、在此次demo展示中，由于未获得菜品的信息，故先根据PreferCanteen的值输出一些指定的样例
       具体代码段如下：
        //假装可以切换功能
        if(PreferCanteen == "随机食堂")
            return listOf(Canteen,"一两米饭","辣子鸡","油麦菜")
        else if(PreferCanteen == "换个食堂")
            return listOf(Canteen,"红豆粥","奥尔良鸡腿","土豆丝")
        else if(PreferCanteen == "换个推荐")
            return listOf(Canteen,"枣糕","煎饼果子")
        else return listOf(Canteen,"白面馒头","青椒炒肉","豆芽")


    另外实现了getFoodList(Canteen:String):List<String>功能
        获得该食堂的所有菜品名。同样，因为暂未实现菜品信息的获取，也只实现了输出指定样例
 */
