package com.logikujo.spore

/**
 *
 * spores / LogiDev - [Fun Functional] / Logikujo.com
 *
 * com.logikujo.spore 16/07/14 :: 18:58 :: eof
 *
 */

import scala.util.matching.Regex.Match

import scalaz._
import Scalaz._

sealed trait ParamsReplacer {
  val str: String
  private val r = ":([a-zA-Z0-9]+)".r
  private def f(p: Map[String, String]): Match => ValidationNel[String,String] = (m:Match) => {
    val s = m.toString.drop(1) // Remove ':'
    (p get s).toSuccess(s"Param '$s' is required").toValidationNel
  }

  def apply(params: Map[String, String]): ValidationNel[String,String] = {
    val reduce = (a: String, b: (Match, String)) => b._1.matched.r.replaceFirstIn(a, b._2)
    val zipAndReduce = (a:List[Match], b:List[String]) => (a zip b).foldLeft(str)(reduce)
    val matchList = r.findAllMatchIn(str).toList
    val l = matchList.successNel[String]
    (matchList.successNel[String] |@|
      (matchList <*> f(params).pure[List]).sequenceU)(zipAndReduce)
  }
}

object ParamsReplacer {
  def apply(s: String):ParamsReplacer = new ParamsReplacer {
    val str = s
  }
}