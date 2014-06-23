package com.viglink.scala_nlp_tests

import epic.models.NerSelector
import epic.sequences.SegmentationEval
import epic.sequences.Segmentation
import epic.sequences.SemiCRF
import epic.trees.Span
import java.io.ObjectOutputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.FileInputStream
import epic.sequences.SemiCRFInference

object NLP3 {
  def main(args: Array[String]): Unit = {

    // ____
    val text = "Baghdad, Iraq (CNN) -- More than 1 million Iraqis have been forced from their homes by conflict this year, the U.N. refugee agency said Friday -- a number likely only to rise as Islamist militants and Iraqi security forces battle for control. A humanitarian crisis is brewing, as families who've fled fighting with little more than the clothes on their back seek water, food and shelter from the summer heat. Meanwhile, the first of up to 300 U.S. military advisers will arrive in Iraq as soon as Saturday, a senior defense official told CNN. This first group from outside Iraq is expected to be very small, the official said."

    //val nerModel = NerSelector.loadNer("en").get
    //println(nerModel.getClass())
   //write(nerModel)
    val nerModel = read[SemiCRF[String,String]]("/tmp/test.obj")
println(nerModel.getClass())
    val tokenized = epic.preprocess.tokenize(text);
    println(tokenized)
    val nerred = nerModel.bestSequence(tokenized)
    println(nerred.render("O"))
/*
    println(nerred.segments)
    println(nerred.words)
    println("segm: " + nerred)
    println(nerred.label.getClass())
    println("last: " + nerred.label.last.getClass())
    println(nerred.label.last._1.getClass)
    println(nerred.label.last._1)
    val eval = SegmentationEval.evaluateExample(Set(nerred.label.last._1), nerred, nerred)
    println("eval: " + eval)*/
  }

  def write(x: AnyRef) {
    val output = new ObjectOutputStream(new FileOutputStream("/tmp/test.obj"))
    output.writeObject(x)
    output.close()
  }

  def read[A](path:String) = {
    val input = new ObjectInputStream(new FileInputStream(path))
    val obj = input.readObject()
    input.close()
    obj.asInstanceOf[A]
  }
}


  