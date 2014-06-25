package com.viglink.scala_nlp_tests

import epic.sequences.SegmentationEval
import epic.sequences.SegmentationEval.Stats

object Evaluate {

  def main(args: Array[String]): Unit = {

    val showExamples = true

    val golddataPath = "/Users/katrintomanek/dev/data/CPROD/CPROD1.0/experiment/test.iob"
    val golddata = helpers.readIOBData(golddataPath)

    val testdataPath = "/tmp/cprod/scala_test_feat_pred"
    val testdata = helpers.readIOBData(testdataPath)

    //Predef.assert(testdata.length == golddata.length)

    if (testdata.length != golddata.length)
      throw new IllegalArgumentException("length of test and gold differs");
    
    var stats = new Stats(0, 0, 0)
    for (i <- 0 to golddata.length - 1) {
      val st = SegmentationEval.evaluateExample(Set("O"), testdata(i), golddata(i))

   stats += st
//P=0.0965 R=0.1236 F=0.1084
      if (showExamples) {
        val goldEx = golddata(i)
        val testEx = testdata(i)
        if (goldEx.asFlatTaggedSequence("O").label.contains("B") || goldEx.asFlatTaggedSequence("O").label.contains("I")) {
          println("\tTRUE: " + goldEx.render("O"))
          // predict

          println("\tPRED: " + testEx.render("O"))
          println("next: " + st)
          println("\n")
        }
      }
    }

    println("\nOverall evaluation....")
    println("number gold sentences: " + golddata.length)
    println("number test sentences: " + testdata.length)
    println(stats)

  }
}