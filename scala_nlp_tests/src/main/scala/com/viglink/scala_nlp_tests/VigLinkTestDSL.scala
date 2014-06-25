package com.viglink.nlp.features

import epic.framework.Feature
import epic.features.{WordFeaturizer, WordFeatureAnchoring, IndicatorWSFeature}
import collection.mutable.ArrayBuffer


object VigLinkClusters {

/*
  case class DSL[L](counts: Counter2[L, String, Double],
                    commonWordThreshold: Int = 100,
                    unknownWordThreshold: Int = 2) {

  }
*/
  trait DSL {
    val startsWithHash = new HashTagFeaturizer()
  }
}

case class HashTagFeaturizer extends WordFeaturizer[String] with Serializable {
  
  def anchor(w: IndexedSeq[String]): WordFeatureAnchoring[String] = new WordFeatureAnchoring[String]{
    override def words: IndexedSeq[String] = w

    override def featuresForWord(pos: Int): Array[Feature] = {
      val feats = new ArrayBuffer[Feature]()
      //w.map{word:String => w.head == "#"}.map(HashTagFeature(_)).toArray
      feats += IndicatorWSFeature('hashHashTag)

      feats.toArray
    }
  }
}

case class HashTagFeature extends Feature