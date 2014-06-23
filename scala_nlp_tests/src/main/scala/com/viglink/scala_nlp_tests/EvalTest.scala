package com.viglink.scala_nlp_tests

import epic.trees.Span
import epic.sequences.Segmentation
import epic.sequences.SegmentationEval

object EvalTest {

  def main(args: Array[String]): Unit = {
    val segments1 = Vector((0, Span(2, 4)),(0, Span(5, 6)))
    val words1 = IndexedSeq.range(0, 8)
    val x1 = Segmentation(segments1, words1)
    
        val segments2 = Vector((0, Span(2, 4)))
    val words2 = IndexedSeq.range(0, 8)
    val x2 = Segmentation(segments2, words2)


    val eval =SegmentationEval.evaluateExample(Set(), x1,x2)
    println("eval: " + eval)
    
    
    println(x2.label)
    
    
  }

}