package com.viglink.scala_nlp_tests

import scala.collection.mutable.ArrayBuffer
import java.io.File

object HeyJoe {

  def main(args: Array[String]): Unit = {
    println("Hey Joe.!.")
    val a = new ArrayBuffer[String]()
    a.append("S")
    a.append("ffS")

    val b = new ArrayBuffer[String]()
    b.append("1")
    b.append("2")

    a.foreach(println)
    println(a)


    val c = a.zip(b).map { case (x,y) => (x+ " " +y)}
    println(c)
    
    printToFile(new File("/tmp/example.txt"))(p => {
      c.foreach(p.println)
    })
  }
  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }
}