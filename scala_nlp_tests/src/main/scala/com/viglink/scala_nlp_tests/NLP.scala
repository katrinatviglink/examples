package com.viglink.scala_nlp_tests
//import breeze.linalg._
//import epic._
import epic.preprocess.MLSentenceSegmenter
import scala.reflect.io.File
object NLP {

  def main(args: Array[String]): Unit = {
    //val x = DenseVector.zeros[Double](5)
    //println("halloxxx")
    //println(x)

    val text = "Baghdad, Iraq (CNN) -- More than 1 million Iraqis have been forced from their homes by conflict this year, the U.N. refugee agency said Friday -- a number likely only to rise as Islamist militants and Iraqi security forces battle for control. A humanitarian crisis is brewing, as families who've fled fighting with little more than the clothes on their back seek water, food and shelter from the summer heat. Meanwhile, the first of up to 300 U.S. military advisers will arrive in Iraq as soon as Saturday, a senior defense official told CNN. This first group from outside Iraq is expected to be very small, the official said."

    /*          var parsefile = File("/Users/katrintomanek/dev/scala/epic-parser-en-span_2.10-2014.6.3-SNAPSHOT.jar")
    println(parsefile)
        val p2 = epic.models.ParserModelLoader.readFromJar("en", parsefile)
]println("explicitly loaded: " +p2.getClass())  */

    println("loading sentence splitter...")
    val sentenceSplitter = MLSentenceSegmenter.bundled().get
    println("sentence ready: " + sentenceSplitter.getClass())

    // println("loading tokenizer...")

    val tokenizer = new epic.preprocess.TreebankTokenizer()
    println("loading p...")
    val parserEN = epic.parser.models.en.span.EnglishSpanParser.load()
    println("loaded: " + parserEN.getClass())
    val sentences = sentenceSplitter.apply(text).toIndexedSeq
    println(sentences)
    //IndexedSeq[IndexedSeq[String]] = sentenceSplitter(text).map(tokenizer).toIndexedSeq
    println(sentences.length)
    for (sentence <- sentences) {
      println("next: " + sentence)
      val tokens = tokenizer.apply(sentence)
      println(tokens.length)
      val parsed = parserEN(tokens)
      println(parsed.toString)
      // val tags = tagger.map(tokens)
    }



    //val posEN=epic.sequences.CRFModel.

    //val sentences: IndexedSeq[IndexedSeq[String]] = sentenceSplitter(text).map(tokenizer).toIndexedSeq

    // val ner = epic.models.NerSelector.loadNer("en") // or another 2 letter code.

    //val segments = tagger(sentence)

    //println(tags.render(tagger.outsideLabel))

    /*
    println("loading pos tagger...")
    val tagger = epic.models.PosTagSelector.loadTagger("en")
    println("pos loaded: " + tagger.nonEmpty)
    println(tagger)
    tagger.
    
    //println("loading parser...")
    //val parser = epic.models.ParserSelector.loadParser("en")
    //println("parser loaded: " + parser.nonEmpty)



    //println("loading ner tagger...")
    //val ner = epic.models.NerSelector.loadNer("en")
    //println("ner loaded: " + ner.nonEmpty)
    
    
    val sentences = sentenceSplitter.apply(text).toIndexedSeq
    println(sentences)
    //IndexedSeq[IndexedSeq[String]] = sentenceSplitter(text).map(tokenizer).toIndexedSeq
    println(sentences.length)
    for (sentence <- sentences) {
      println("next: " + sentence)
      val tokens = tokenizer.apply(sentence)
      println(tokens.length)
     // val tags = tagger.map(tokens)
    }*/

    /*    val a = "Hello world, how is Joe doing? This is nothing new."
    val res = sentenceSplitter.apply(a)
    println(res)

    */

  }

}