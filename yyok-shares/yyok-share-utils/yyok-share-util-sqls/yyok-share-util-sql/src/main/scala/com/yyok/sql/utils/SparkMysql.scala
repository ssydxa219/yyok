package com.yyok.sql.utils

// 重命名成员
import java.util.{ HashMap => JavaHashMap }
// 隐藏成员
import java.util.{ HashMap => _, _ }

object SparkMysql {

  def main(args: Array[String]): Unit = {
    print("wq vb ")
    var hm = new JavaHashMap();
    println(addInt(8, 9))
    println(dupsString(10, 11))
    println(multtipliner(8))
    var array = Array(1, 2, 3, 4, 5)
    for (i <- array) {
      println(i)
    }
    println("-----------------")
    var max = 3
    for (i <- 0 to array.length - 1) {
      println(i)
      println("-----------------")
      if (array(i) > max) max = array(i)
      println(max)
      val traitin = new Point(5,6);
      val traitim = new Point(6,6);
      println(traitin.isNotEqual(5))
      println(matchs(5))
      var str="Scala is scalaable and cool"
      println((pattern findAllIn str).mkString(","))
  println(pattern replaceFirstIn(str,"java"))
    
    
    }
  }

  def addInt(a: Int, b: Int): Int = {
    var sum: Int = 0
    sum = a + b
    return sum
  }

  def dupsString(c: Int, d: Int): String = {
    var str: String = "hello world" + addInt(c, d)
    return str
  }

  val multtipliner = (i: Int) => i * 8

  //interface
  trait Equal {
    def isEqual(x: Any): Boolean
    def isNotEqual(x: Any): Boolean = !isEqual(x)
  }

  class Point(xc: Int, yc: Int) extends Equal {
    var x: Int = xc
    var y: Int = yc
    def isEqual(obj: Any) =
      obj.isInstanceOf[Point] &&
        obj.asInstanceOf[Point].x == x
  }
  
  def matchs(i:Int): String = i match {
    case 1=>"one"
    case 2=>"two"
    case 3=>"three"
    case 4=>"four"
    case 5=>"five"
  }
import scala.util.matching.Regex

  val pattern = new Regex("(S|s)cala")
  
}