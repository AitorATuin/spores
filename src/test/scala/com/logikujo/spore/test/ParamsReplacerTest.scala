package com.logikujo.spore
package test

/**
 *
 * spores / LogiDev - [Fun Functional] / Logikujo.com
 *
 * 16/07/14 :: 19:19 :: eof
 *
 */

import scalaz._
import Scalaz._

import org.scalatest.matchers._

class ParamsReplacerTest extends UnitSpec {
  val path1 = "/examplePath/justOneParam.:param1/aComposedParamWith.:param2.And:param3/:singleParam"
  val myMap1 = Map(
    "param1" -> "P1",
    "param2" -> "P2",
    "param3" -> "P3",
    "singleParam" -> "SP"
  )
  val listOfMaps1 = (0 to myMap1.size) map (myMap1.slice(0, _)) toList
  val replacer1 = ParamsReplacer(path1)
  def beSuccessOrFailure(success: Boolean) = Matcher { (value: ValidationNel[String, String]) =>
    MatchResult(
      if (success) value.isSuccess else value.isFailure,
      value.toString + " was not a Failure",
      value.toString + " was a Failure"
    )
  }
  val success = true
  val failure = false
  val beSuccess= beSuccessOrFailure(success)
  val beFailure = beSuccessOrFailure(failure)
  def haveNumberOfErrors(n: Int) = have length(n) compose {
    (value: ValidationNel[String,String]) => value.swap.toOption.fold(List[String]())(_.toList)
  }

  path1 should
    "return Failure with 4 errors when no params are passed" in {
    replacer1(listOfMaps1(0)) should (beFailure and haveNumberOfErrors(4))
  }
  it should
    "return Failure with 3 errors when 1 param is passed" in {
    replacer1(listOfMaps1(1)) should (beFailure and haveNumberOfErrors(3))
  }
  it should
    "return Failure with 2 errors when 2 params are passed" in {
    replacer1(listOfMaps1(2)) should (beFailure and haveNumberOfErrors(2))
  }
  it should
    "return Failure with 1 error when 3 params are passed" in {
    replacer1(listOfMaps1(3)) should (beFailure and haveNumberOfErrors(1))
  }
  it should
    "return Success with the string replaced when all params are passed" in {
    replacer1(listOfMaps1(4)) should beSuccess
  }
}
