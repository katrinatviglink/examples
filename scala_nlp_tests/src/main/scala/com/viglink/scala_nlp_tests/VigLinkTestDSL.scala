package com.viglink.features

/*
 Copyright 2012 David Hall

 Licensed under the Apache License, Version 2.0 (the "License")
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/
import epic.framework.Feature
import breeze.linalg._
import collection.mutable.ArrayBuffer
import epic.parser.features.IndicatorFeature
import java.text.NumberFormat
import java.util.Locale
import breeze.util.{Encoder, Index}

import epic.features.{WordFeaturizer,WordFeatureAnchoring}

final case class IndicatorWSFeature(name: Symbol) extends Feature
final case class SuffixFeature(str: String) extends Feature
final case class PrefixFeature(str: String) extends Feature
final case class ShapeFeature(str: String) extends Feature
final case class SignatureFeature(str: String) extends Feature
final case class SeenWithTagFeature(str: Any) extends Feature
final case class LeftWordFeature(str: Any) extends Feature
final case class RightWordFeature(str: Any) extends Feature



class VLWordPropertyFeaturizer(wordCounts: Counter[String, Double],
                             commonWordThreshold: Int = 20) extends WordFeaturizer[String] with Serializable {
  import VLWordPropertyFeaturizer._

  private val wordIndex = Index(wordCounts.keysIterator)
  private val knownWordFeatures = Encoder.fromIndex(wordIndex).tabulateArray(s => featuresFor(s).toArray)

  def anchor(w: IndexedSeq[String]): WordFeatureAnchoring[String] = new WordFeatureAnchoring[String] {
    def words: IndexedSeq[String] = w
    val indices = words.map(wordIndex)
    val myFeatures = (0 until words.length).map(i => if (indices(i) < 0) featuresFor(words(i)).toArray else knownWordFeatures(indices(i)))
    
    def featuresForWord(pos: Int): Array[Feature] = {
      myFeatures(pos)
    }

  }

  //  val signatureGenerator = EnglishWordClassGenerator
  def featuresFor(w: String): IndexedSeq[Feature] = {
    val wc = wordCounts(w)
    val features = ArrayBuffer[Feature]()

    val wlen = w.length
    val numCaps = (w:Seq[Char]).count{_.isUpper}
    val hasLetter = w.exists(_.isLetter)
    val hasNotLetter = w.exists(!_.isLetter)
    val hasDigit = w.exists(_.isDigit)
    val hasNonDigit = hasLetter || w.exists(!_.isDigit)
    val hasLower = w.exists(_.isLower)
    val hasDash = w.contains('-')
    val numPeriods = w.count('.' ==)
    val hasPeriod = numPeriods > 0

    if(hasLetter && hasDigit) features += hasLetterNumber

    features
  }

  def apply(w: String) = featuresFor(w)

  
}

object VLFeatures {
  trait DSL {
      //val vigLinkFeatures = new WordPropertyFeaturizer(wordCounts)
      def vigLinkFeatures(counts:Counter2[String, String, Double]) = new VLWordPropertyFeaturizer(sum(counts, Axis._0))
    }
}

object VLWordPropertyFeaturizer {
  
  /*
  case class DSL[L](counts: Counter2[L, String, Double],
                  commonWordThreshold: Int = 100,
                  unknownWordThreshold: Int = 2) {
    val summedCounts = sum(counts, Axis._0)
    def vigLinkFeatures(counts:Counter2[L, String, Double]) = new VLWordPropertyFeaturizer(sum(counts, Axis._0))
  }
  */
  // features
  val hasLetterNumber = IndicatorWSFeature('HasLetterNumber)
  val hasNoLower = IndicatorWSFeature('HasNoLower)
  val hasDashFeature = IndicatorWSFeature('HasDash)
  val hasDigitFeature = IndicatorWSFeature('HasDigit)
  val hasNoLetterFeature = IndicatorWSFeature('HasNoLetter)
  val hasNotLetterFeature = IndicatorWSFeature('HasNotLetter)
  val endsWithSFeature = IndicatorWSFeature('EndsWithS)
  val longWordFeature = IndicatorWSFeature('LongWord)
  val shortWordFeature = IndicatorWSFeature('ShortWord)
  val hasKnownLCFeature = IndicatorWSFeature('HasKnownLC)
  val hasKnownTitleCaseFeature = IndicatorWSFeature('HasKnownTC)
  val hasInitCapFeature = IndicatorWSFeature('HasInitCap)
  val hasInitialCapsAndEndsWithSFeature = IndicatorWSFeature('HasInitCapAndEndsWithS)
  val hasCapFeature = IndicatorWSFeature('HasCap)
  val hasManyCapFeature = IndicatorWSFeature('HasManyCap)
  val isAllCapsFeature = IndicatorWSFeature('AllCaps)
  val isProbablyAcronymFeature = IndicatorWSFeature('ProbablyAcronym)
  val isProbablyYearFeature = IndicatorWSFeature('ProbablyYear)
  val startOfSentenceFeature = IndicatorWSFeature('StartOfSentence)
  val integerFeature = IndicatorWSFeature('Integer)
  val floatFeature = IndicatorWSFeature('Float)
  val isAnInitialFeature = IndicatorWSFeature('IsAnInitial)
  val endsWithPeriodFeature = IndicatorWSFeature('EndsWithPeriod)
}
