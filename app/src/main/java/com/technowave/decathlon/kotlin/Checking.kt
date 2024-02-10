package com.technowave.decathlon.kotlin

class Checking {

    lateinit var a:String

}


fun main()
{
  /**  var ab= Person("Syed",100).apply{
        "helo" }
    println(ab)

    var c= ab.let {
        it }
    println(c)

    var a=Person("Syed",100).run {
        "helo" }
    println(a)**/

    var abc: String? = null
    abc="syed"
    Thread{
        println("coroutine")
        while(true)
        {
            abc=null
          //  println(abc.toString())
        }
    }.start()
    abc.let{
    println(abc)
    }

}

data class Person(var name:String,var price: Int)
