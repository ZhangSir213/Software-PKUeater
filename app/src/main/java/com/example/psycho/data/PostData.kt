package com.example.psycho.data

data class BaseData(var errCode:Int=10001,var errMsg:String="None",var id:Int=100,var name:String="xiaoming",
var gender:Int=0,var birthday:String="1111-11-11",var weight:Int=0,var height:Int=0,var avoidance:Int=0,
var state:Int=0)
data class PostData(  var  status: String="fail",var  data:BaseData)