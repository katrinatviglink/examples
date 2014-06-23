package com.viglink.scala_nlp_tests

import epic.sequences.CRF
import epic.trees.AnnotatedLabel
import epic.preprocess.MLSentenceSegmenter

object NLP2 {
  def main(args: Array[String]): Unit = {
    
    //val p2 = epic.sequences.models.en.postag.EnglishPosTag.load
    val text = "Baghdad, Iraq (CNN) -- More than 1 million Iraqis have been forced from their homes by conflict this year, the U.N. refugee agency said Friday -- a number likely only to rise as Islamist militants and Iraqi security forces battle for control. A humanitarian crisis is brewing, as families who've fled fighting with little more than the clothes on their back seek water, food and shelter from the summer heat. Meanwhile, the first of up to 300 U.S. military advisers will arrive in Iraq as soon as Saturday, a senior defense official told CNN. This first group from outside Iraq is expected to be very small, the official said."

    val sentenceSplitter = MLSentenceSegmenter.bundled().get
    val tokenizer = new epic.preprocess.TreebankTokenizer()
    
    println("sentence ready: " + sentenceSplitter.getClass())

    println("loading tagger...")
    val path = "pos.ser.gz"
    val tagger = epic.models.deserialize[CRF[AnnotatedLabel, String]](path)
    val sentences = sentenceSplitter.apply(text).toIndexedSeq
    val ner = epic.sequences.models.en.conll.EnglishConllNer.load()
    println(sentences)
    
    
    println(sentences.length)
    for (sentence <- sentences) {
      println("next: " + sentence)
      val tokens = tokenizer.apply(sentence)
      println(tokens.length)
      val seq = tagger.bestSequence(tokens);
      println(seq.render)
 
      // ner
     val nerseq= ner.bestSequence(tokens)
      println(nerseq.asFlatTaggedSequence("O"))
      println(nerseq.features)
      for (t<-nerseq.words) {
        println(t)
        //println(nerseq.)
      }
     // println(nerseq.f("O"))
    }
  }
}