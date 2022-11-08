package com.example.psycho.kernel

import com.example.psycho.R
import com.example.psycho.data.Data
import com.example.psycho.data.Food
import com.example.psycho.data.FoodGet
import com.example.psycho.resource.CanteenAdapter
import com.example.psycho.simpleGetUseFrom
import com.google.gson.Gson
import java.util.function.Predicate
import kotlin.random.Random

object Kernel {
    //    var food = arrayOfNulls<Array<kernel.Food?> >(4)
    private var CanteenList: List<String> = listOf(
        "农园一层", "农园二层", "燕南一层", "家园一层",
        "家园二层", "家园三层", "家园四层", "松林包子",
        "学一食堂", "学五食堂", "勺园一层", "勺园二层",
        "佟园餐厅", "勺西餐厅", "勺中餐厅")
    private var PreferCanteen: String = "随机食堂"//想去的食堂
    private var Avoidance: Int = 0//忌口
    private var Budget: Int = 10000//预算，愿意为一顿饭花多少钱
    private var CarolieLimit: Int = 10000//一顿饭应该摄入多少卡路里
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
    var food = Array(4) {Array<Food>(100, {i: Int -> Food()})}      //各类食物信息
    var nwfood = Array(4) {Array<Food>(100, {i: Int -> Food()})}    //排除忌口等因素后本次可以推荐的食物
    var candidate = Array<Food>(10, {i: Int -> Food()})                  //每次搜索的推荐结果
    var recommend = Array<Food>(10, {i: Int -> Food()})                  //最终推荐结果
    var cnt = intArrayOf(0,0,0,0)     //记录各类食物数量
    var ncnt = intArrayOf(0,0,0,0)    //记录当前可供推荐的各类食物数量
    var ccnt = 0                      //记录搜索出来的推荐结果的数量
    var rcnt = 0                      //记录最终推荐结果的数量
    var rec_carolie = 0       //推荐结果的卡路里总量
    var rec_distance = 0      //推荐结果的步行距离

    init
    {
        //构造函数
        val responseData= simpleGetUseFrom("http://47.94.139.212:3000/food/list",null)
        val getResponse=Gson().fromJson(responseData,FoodGet::class.java)
        val FoodList=getResponse.data
        val length=FoodList.size
        for (i in 0 until  length)
        {
            val type=FoodList.get(i).type-1
            food[type][++cnt[type]]=FoodList.get(i)
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
    fun getFoodList(canteen: String): List<String>{
        //获得指定食堂的菜品推荐
        if(canteen == "家园一层") return listOf("辣子鸡","兔头","什么？")
        if(canteen == "家园三层") return listOf("辣椒炒肉","烤盘饭","真能吃")
        return listOf("包子","亲自指挥，亲自部署","民心所盼，众望所归","真不错")
    }
    //所有食物&在本次推荐中可以推荐的食物

    fun Dist(i: Int, j: Int):Int {
        if(i > ccnt || j > ccnt) return 0
        var res = candidate[i].window - candidate[j].window
        if(res < 0) res = -res
        return res
    }
    fun dfs(CarolieTot: Int, Cost: Int, Type: Int): Unit{
        //    print("DFS("+CarolieTot+","+Cost+","+Type+")\n")
        if(CarolieTot > CarolieLimit) return //卡路里超标则停止
        if(Cost > Budget) return //预算超标则停止
        if(Type == 4){//四种菜品遍历完，结束推荐
            var distance = Dist(1, 2) + Dist(1,3) + Dist(2,3)
            if(rcnt > 0){//已有之前的搜索结果，需要两者进行比较
                //i. 摄入卡路里越接近limit越好
                if(rec_carolie >= CarolieTot) return
                //ii. 走的距离越短越好
                if(rec_distance <= distance) return
            }
            rcnt = 0
            for(i in 1..ccnt) {
                recommend[++rcnt] = candidate[i]
                //    print("food:"+candidate[i]+"\n")
            }
            rec_carolie = CarolieTot
            rec_distance = distance
            //print("Update It!!\n")
            return
        }
        for(i in 1..cnt[Type]){
            candidate[++ccnt] = nwfood[Type][i]
            dfs(CarolieTot+nwfood[Type][i].carolie, Cost+ nwfood[Type][i].price, Type+1)
            ccnt--  //回溯
        }
        dfs(CarolieTot, Cost,Type+1)  //不选择Type类食物
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
        for(i in 0..3)
            for(j in 1..cnt[i]) {
                print(food[i][j])
                print('\n')
            }

        //Step1 计算去哪个食堂
        if(PreferCanteen == "随机食堂"){
            //默认情况
            Canteen = Kernel.CanteenList[(0..14).random()]
        }
        else if(PreferCanteen == "换个推荐"){
            Canteen = Canteen//食堂号不变
        }
        else if(PreferCanteen == "换个食堂"){
            var lastCanteen:String = Canteen
            while(Canteen == lastCanteen)
                Canteen = Kernel.CanteenList[(0..14).random()]
        }
        else
        //有指定的食堂
            Canteen = PreferCanteen



        //Step2 获得推荐菜品
        //i. 获得忌口等信息
/*        var Mydata = Data
        var a = Mydata.getAvoidanceType()
        print(a)
 */
        //ii. 筛选出本次可以推荐的菜品集合
        ncnt = intArrayOf(0,0,0,0)
        for(i in 0..3)
            for(j in 1..cnt[i]) {
                if(//(Canteen == CanteenList[food[i][j].canteenID]) && //食堂对应
                    (Avoidance.and(food[i][j].avoidance) == 0)       //忌口对应
                )
                    nwfood[i][++ncnt[i]] = food[i][j]
            }
        print("筛选后的食物数据：\n")
        for(i in 0..3)
            for(j in 1..ncnt[i]) {
                print(nwfood[i][j])
                print('\n')
            }
        //iii. 获得本次推荐
        dfs(0,0,0)
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
    print(xzy.getResult())
}
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
