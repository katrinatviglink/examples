package com.viglink.scala_nlp_tests

import epic.trees.Span
import epic.sequences.Segmentation
import epic.sequences.SegmentationEval
import epic.sequences.SemiCRF
import epic.sequences.Gazetteer
import epic.corpora.CONLLSequenceReader
import java.io.InputStream
import java.io.FileInputStream
import epic.sequences.TaggedSequence
import epic.sequences.CRF
import epic.sequences.TaggedSequenceEval
import epic.sequences.SemiNerPipeline
import scala.collection.mutable.ArrayBuffer
import nak.data.Example
import epic.preprocess.TreebankTokenizer
import breeze.optimize.FirstOrderMinimizer.OptParams
import epic.util.CacheBroker

object traintest {

  def main(args: Array[String]): Unit = {
 val stream = new FileInputStream("/Users/katrintomanek/dev/cprod/test.iob")
    val x = CONLLSequenceReader.readTrain(stream, "conll", " ").toIndexedSeq
    val training = new ArrayBuffer[Segmentation[String, String]]()
    for (y <- x) {
      //println(y.label)
      val seg = makeSegmentation(y)
     println(seg.asBIOSequence())
     println(seg.render("O"))
    }
  }

  def makeSegmentation(ex: Example[IndexedSeq[String], IndexedSeq[IndexedSeq[String]]]): Segmentation[String, String] = {
    val labels = ex.label
    val words = ex.features.map(_ apply 0)
    assert(labels.length == words.length)
    val out = new ArrayBuffer[(String, Span)]()
    var start = labels.length
    var i = 0
    while (i < labels.length) {
      val l = labels(i)
      println(l+":"+l(0)+":"+i+":"+start)
      l(0) match {
        case 'O' =>
          if (start < i)
            out += (labels(start).replaceAll(".-", "").intern -> Span(start, i))
          out += ("O".intern -> Span(i, i + 1))
          start = i + 1
        case 'B' =>
          if (start < i)
            out += (labels(start).replaceAll(".-", "").intern -> Span(start, i))
          start = i
        case 'I' =>
          if (start >= i) {
            start = i
          //} else if (labels(start) != l) {//TODO has to be the first char!
          } 
          //else if (labels(start)(0)!=l(0)){
           // out += (labels(start).replaceAll(".-", "").intern -> Span(start, i))
           // start = i
          //} // else, still in a field, do nothing.
        case _ =>
          sys.error("weird label?!?" + l)
      }

      i += 1
    }
    if (start < i)
      out += (labels(start).replaceAll(".-", "").intern -> Span(start, i))

    assert(out.nonEmpty && out.last._2.end == words.length, out + " " + words + " " + labels)
    Segmentation(out, TreebankTokenizer.tokensToTreebankTokens(words), ex.id)
  }

}