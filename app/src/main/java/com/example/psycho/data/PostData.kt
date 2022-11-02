package com.example.psycho.data

data class BaseData(var errCode:Int=10001,var errMsg:String="None")
data class PostData(  var  status: String="fail",var  data:BaseData)