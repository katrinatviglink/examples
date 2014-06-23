package com.viglink.scala_nlp_tests

import epic.sequences.Segmentation
import epic.preprocess.TreebankTokenizer
import scala.collection.mutable.ArrayBuffer
import epic.trees.Span
import nak.data.Example
import epic.corpora.CONLLSequenceReader
import java.io.InputStream
import java.io.FileInputStream
import epic.sequences.SemiCRF
import epic.util.CacheBroker
import breeze.optimize.FirstOrderMinimizer.OptParams
import epic.sequences.SegmentationEval
import java.io.File
import epic.sequences.SemiCRFModel
import epic.models.NerSelector
import scala.util.Random
import java.io.ObjectOutputStream
import java.io.ObjectInputStream
import java.io.FileOutputStream

object TrainSemiCRF {

  def main(args: Array[String]): Unit = {

    val doTraining = true

    
    // read all data
    val modelPath = "/tmp/cprod_all_iob.crf"
    val path = "/Users/katrintomanek/dev/cprod/training_small.iob"

    val data = helpers.readIOBData(path)
    println("all data: " + data.length)
    val r: Random = new Random(100)
    val shuffledData = r.shuffle(data)
    val num = data.length
    val per = 2 / 3
    val train = shuffledData.slice(0, num / 2)
    val test = shuffledData.slice(num / 2, num)
    println("training size: " + train.length)
    println("testing size: " + test.length)

    // train or reload
    var myCRF: SemiCRF[String, String] = null
    if (doTraining) {
      println("Training model...")
      myCRF = trainModel(train, modelPath)
      evalModel(train, test, myCRF)
    } else {
      println("Loading model...")
      myCRF = helpers.read[SemiCRF[String, String]](modelPath)
    }

    predict(test, myCRF)
  }

  def trainModel(train: IndexedSeq[Segmentation[String, String]], modelPath: String): SemiCRF[String, String] = {
    println("Training model...")
    val opti: OptParams = OptParams()
    val cache = new CacheBroker()
    val t1 = System.currentTimeMillis()
    val myCRF = SemiCRF.buildSimple(train.toIndexedSeq, "--XXX--", "O", opt = opti)(cache)
    val t2 = System.currentTimeMillis()

    println("-----------------------")
    println("Time for training: " + (t2 - t1) / 1000 + " sec")
    println("Opt params: " + opti)
    println("-----------------------")

    if (modelPath != null) {
      println("Storing model to: " + modelPath)
      helpers.write(myCRF, modelPath)
    }

    return myCRF
  }

  def evalModel(train: IndexedSeq[Segmentation[String, String]], test: IndexedSeq[Segmentation[String, String]], myCRF: SemiCRF[String, String]) {
    val evTest = SegmentationEval.eval(myCRF, test, "O")
    val evTrain = SegmentationEval.eval(myCRF, train, "O")

    println("----------------------")
    println("Perf on test set: " + evTest)
    println("Perf on train set: " + evTrain)
  }

  def predict(test: IndexedSeq[Segmentation[String, String]], myCRF: SemiCRF[String, String]) {
    println("----------------------")
    println("Predictions")
    // show predictions containing at least one mention
    for (ex <- test) {
      if (ex.asFlatTaggedSequence("O").label.contains("B") || ex.asFlatTaggedSequence("O").label.contains("I")) {
        println("\tTRUE: " + ex.render("O"))
        // predict
        val seq = myCRF.bestSequence(ex.features)
        println("\tPRED: " + seq.render("O"))
        println("\t" + SegmentationEval.evaluateExample(Set("O"), seq, ex))
        println("\n")
      }
    }
  }

}