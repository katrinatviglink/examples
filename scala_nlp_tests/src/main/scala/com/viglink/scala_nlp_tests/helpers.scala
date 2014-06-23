package com.viglink.scala_nlp_tests

import java.io.ObjectInputStream
import java.io.FileInputStream
import java.io.ObjectOutputStream
import java.io.FileOutputStream
import epic.sequences.Segmentation
import scala.collection.mutable.ArrayBuffer
import nak.data.Example
import epic.trees.Span
import epic.corpora.CONLLSequenceReader

object helpers {

   def  write(x: AnyRef, path: String) {
    val output = new ObjectOutputStream(new FileOutputStream(path))
    output.writeObject(x)
    output.close()
  }

  def read[A](path: String) = {
    val input = new ObjectInputStream(new FileInputStream(path))
    val obj = input.readObject()
    input.close()
    obj.asInstanceOf[A]
  }
  
    def readIOBData(path: String): IndexedSeq[Segmentation[String, String]] = {
    println("Preparing training data...")
    val stream = new FileInputStream(path)
    val dataset = new ArrayBuffer[Segmentation[String, String]]()
    for (el <- CONLLSequenceReader.readTrain(stream, " ").toIndexedSeq) {
      val seg = makeSegmentation(el)
      if (seg == null) {
        println("\tskipping null line.." + el)
      } else {
        dataset += (seg)
      }
    }
    dataset.toIndexedSeq
  }
  
   /**
   * replaces empty words when presents this leads to IndexOutOfBoundsException during feature extraction
   */
  def makeSegmentation(ex: Example[IndexedSeq[String], IndexedSeq[IndexedSeq[String]]]): Segmentation[String, String] = {
    val labels = ex.label
    val words = ex.features.map(_ apply 0)
    for(w<-words) {
      if (w==null || w.isEmpty()) {
        return null
      }
    }
    assert(labels.length == words.length)
    val out = new ArrayBuffer[(String, Span)]()
    
    
    
    var start = labels.length
    var i = 0
    while (i < labels.length) {
      val l = labels(i)
      //println(l+":"+l(0)+":"+i+":"+start)
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
    //Segmentation(out, TreebankTokenizer.tokensToTreebankTokens(words), ex.id)
    Segmentation(out, words, ex.id)
  }


}