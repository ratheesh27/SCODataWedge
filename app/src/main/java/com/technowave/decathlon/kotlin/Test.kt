package com.technowave.decathlon.kotlin

class Test {


}

fun main()
{
    var a:String?=null
    Thread{
        println("coroutine")
        while(true)
        {
            a=null
            println(a.toString())
        }
    }.start()

}