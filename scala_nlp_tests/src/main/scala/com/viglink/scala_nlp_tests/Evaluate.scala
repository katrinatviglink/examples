package com.viglink.scala_nlp_tests

import epic.sequences.SegmentationEval
import epic.sequences.SegmentationEval.Stats

object Evaluate {

  def main(args: Array[String]): Unit = {

    val golddataPath = "/Users/katrintomanek/dev/data/CPROD/CPROD1.0/experiment/train.ftd.iob"
    val golddata = helpers.readIOBData(golddataPath)

    val testdataPath = "/tmp/scala_pred"
    val testdata = helpers.readIOBData(testdataPath)

    //Predef.assert(testdata.length == golddata.length)

    if (testdata.length != golddata.length)
      throw new IllegalArgumentException("length of test and gold differs");


    var stats = new Stats(0, 0, 0)
    for (i <- 0 to golddata.length - 1) {
      val st = SegmentationEval.evaluateExample(Set("O"), golddata(i), testdata(i))
      //println("next: " + st)
      stats += st
    }

    println("\nOverall evaluation....")
    println("number gold sentences: " + golddata.length)
    println("number test sentences: " + testdata.length)
    println(stats)

  }
}