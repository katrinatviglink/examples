package com.viglink.scala_nlp_tests

import epic.features.WordFeaturizer
import epic.features.SurfaceFeaturizer
import breeze.linalg.Counter2
import epic.features.BrownClusters
import com.viglink.features._

object testfeatures {

  def testFeaturizer[L](counts: Counter2[L, String, Double]) = {
    val dsl = new WordFeaturizer.DSL[L](counts)
    import dsl._

    (
      unigrams(word, 1) // previous, current, and next word
      + unigrams(clss, 1) // previous, current, and next word class
      + bigrams(clss, 2) // bigrams of (clss(-2), clss(-1)), (clss(-1), clss(0)), ...
      + bigrams(tagDict, 2) // bigrams of the most common tag for each word
      + suffixes() // suffixes of the current word string
      + prefixes() // prefixes
      + props // a set of hand designed patterns, encoding things like capitalization,
      // whether or not the word looks like a year, etc.
      )
  }

  def productMentionFeats(counts: Counter2[String, String, Double]) = {
    val dsl = new WordFeaturizer.DSL[String](counts) with SurfaceFeaturizer.DSL with BrownClusters.DSL with VLFeatures.DSL
    val ccounts = counts
    import dsl._

    (
      unigrams(word, 2)
      + unigrams(brownClusters(9, 15), 2)
      + unigrams(clss, 1)
      //        + unigrams(shape, 2)
      + bigrams(clss, 1)
      // + bigrams(tagDict, 2)
      // + bigrams(shape, 1)
      //        + shape(-1) * shape * shape(1)
      + prefixes()
      + suffixes()
      //        + unigrams(props, 2)
      //        + bigrams(props, 1)
      + unigrams(props, 1)
      + vigLinkFeatures(ccounts)
    )
  }

}