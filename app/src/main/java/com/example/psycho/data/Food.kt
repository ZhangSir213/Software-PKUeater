package com.example.psycho.data

data class Food (
        var id: Int = 0,                //表示菜品编号
        var window: Int = 0,            //窗口号
        var name: String = "" ,         //名字
        var canteenId: Int = 0,         //食堂
        var calorie: Int = 0,           //卡路里
        var avoidance: Int = 0,         //忌口
        var price: Int = 0,             //价格
        var imgAddr: String = "",       //图片地址
        var type: Int = 0,
        var intro: String = ""          //简单描述
)

data class FoodGet (var status: String = "fail", var data: List<Food>)
