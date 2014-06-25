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
import breeze.linalg.Counter2
import java.util.ArrayList
import epic.sequences.SegmentationEval.Stats


object TrainSemiCRF {

  def main(args: Array[String]): Unit = {

    // configurations
    val doTraining = true
    val useDefaultFeatures = true
    val modelPath = "/tmp/cprod/scala_train_zz.mod"
    val showOutput = true
    val predOut = "/tmp/cprod/scala_test_pred_zz"

    // read training and testing data
    val traindataPath = "/Users/katrintomanek/dev/data/CPROD/CPROD1.0/experiment/train.ftd.iob"
    val testdataPath = "/Users/katrintomanek/dev/data/CPROD/CPROD1.0/experiment/test.ftd.iob"
    val train = helpers.readIOBData(traindataPath)
    val test = helpers.readIOBData(testdataPath)
    println("training size: " + train.length)
    println("testing size: " + test.length)

    // train or reload
    val myCRF: SemiCRF[String, String] = doTraining match {
      case true => {
        trainModel(train, useDefaultFeatures, modelPath)
      }
      case _ => {
        println("Loading model...")
        helpers.read[SemiCRF[String, String]](modelPath)
      }
    }

    // eval
    evalModel(train, test, myCRF)

    // predict
    //predict(test, myCRF, showOutput, predOut)
  }

  def trainModel(train: IndexedSeq[Segmentation[String, String]], useDefaultFeatures: Boolean, modelPath: String): SemiCRF[String, String] = {
    println("Training model...")
    val opti: OptParams = OptParams()
    
    val cache = new CacheBroker()
    val t1 = System.currentTimeMillis()

    val myCRF: SemiCRF[String, String] = useDefaultFeatures match {
      case true => {
        println("Training model with general NER features...")
        SemiCRF.buildSimple(train.toIndexedSeq, "--XXX--", "O", opt = opti)(cache)
      }
      case _ => {
        println("Training model with special product mention features...")
        SemiCRF.buildSimple(train.toIndexedSeq, "--XXX--", "O", opt = opti, wordFeaturizer = getFeaturizer(train, "O"))(cache)
      }
    }

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

  def getFeaturizer(data: IndexedSeq[Segmentation[String, String]], outsideSymbol: String) = {
    val counts: Counter2[String, String, Double] = Counter2.count(data.map(_.asFlatTaggedSequence(outsideSymbol)).map { seg => seg.label zip seg.words }.flatten).mapValues(_.toDouble)
    //testfeatures.productMentionFeats[String](counts)
    //testfeatures.testFeaturizer[String](counts)
    testfeatures.productMentionFeats(counts)
  }

  def evalModel(train: IndexedSeq[Segmentation[String, String]], test: IndexedSeq[Segmentation[String, String]], myCRF: SemiCRF[String, String]) {
    val evTest = SegmentationEval.eval(myCRF, test, "O")
    val evTrain = SegmentationEval.eval(myCRF, train, "O")

    println("----------------------")
    println("Perf on test set: " + evTest)
    println("Perf on train set: " + evTrain)
  }

  def predict(test: IndexedSeq[Segmentation[String, String]], myCRF: SemiCRF[String, String], showOutput: Boolean, outpath: String) {
    val t1 = System.currentTimeMillis();
    println("----------------------")
    var buf = new ArrayBuffer[String]()

    println("Predictions")
    // show predictions containing at least one mention
    //ArrayList
    for (ex <- test) {
      val guess = myCRF.bestSequence(ex.words, ex.id + "-guess")
      if (outpath != null && !outpath.isEmpty()) {
        val words = ex.words
        val labels = guess.label
        val c = words.zip(guess.asBIOSequence("O").label).map { case (x, y) => (x + " " + y.toString()(0)) }
        buf.appendAll(c)
        buf.append("")
      }

      if (showOutput && (ex.asFlatTaggedSequence("O").label.contains("B") || ex.asFlatTaggedSequence("O").label.contains("I"))) {
        println("\tTRUE: " + ex.render("O"))
        // predict

        println("\tPRED: " + guess.render("O"))
        println("\t" + SegmentationEval.evaluateExample(Set("O"), guess, ex))
        println("\n")
      }
    }

    // write to file
    if (outpath != null && !outpath.isEmpty()) {
      println("Writing predictions in IOB format to: " + outpath)
      helpers.printToFile(new File(outpath))(p => {
        buf.foreach(p.println)
      })

    }

    val t2 = System.currentTimeMillis();
    println("Time for predicton: " + (t2 - t1) / 1000)
  }

  // read all data (shuffle and split)
  /*
    val path = "/Users/katrintomanek/dev/cprod/training.iob"
    val data = helpers.readIOBData(path)
    println("all data: " + data.length)
    val r: Random = new Random(100)
    val shuffledData = r.shuffle(data)
    val num = data.length
    val per = 2 / 3
    val train = shuffledData.slice(0, num / 2)
    val test = shuffledData.slice(num / 2, num)
    */

}
