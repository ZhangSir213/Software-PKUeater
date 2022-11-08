package com.example.psycho.data

data class Food(
        private var id: Int = 0,              //表示菜品编号
        private var window: Int = 0,          //窗口号
        private var name: String = "" ,       //名字
        private var canteenId: Int = 0,    //食堂
        private var calorie: Int = 0,          //卡路里
        private var avoidance: Int = 0,        //忌口
        private var price: Int = 0,       //价格
        private var imgAddr: String = "",
        var type:Int=0,       //图片地址
        private var intro: String = "")        //简单描述

data class FoodGet( var  status: String="fail",var data:List<Food>)
