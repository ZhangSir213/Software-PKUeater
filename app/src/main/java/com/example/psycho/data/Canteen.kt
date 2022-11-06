package com.example.psycho.data

data class Canteen(var id:Int,var name:String,var imgurl:String,var intro:String)
data class CanteenGet( var  status: String="fail",var data:List<Canteen>)