package com.example.psycho.data

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T : Any> {

    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data class Error_msg(val code:Int): Result<Nothing>()

    override fun toString(): String {
      when (this) {
            is Success<*> -> return "Success[data=$data]"
            is Error -> return "${exception.message}"
            is Error_msg-> return code.toString()
        }

    }
     fun toInt():Int
    {
        when(this)
        {
            is Success<*> -> return 0
            is Error -> return 1
            is Error_msg-> return code
        }
    }

}