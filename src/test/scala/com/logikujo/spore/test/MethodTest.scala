package com.logikujo.spore
package test

/**
 *
 * spores / LogiDev - [Fun Functional] / Logikujo.com
 *
 * com.logikujo.spore.test 16/07/14 :: 20:53 :: eof
 *
 */

import argonaut._
import Argonaut._

class MethodTest extends UnitSpec {
  val methodEmpty = "{}"
  val methodMinimal =
    """{
      |  "path" : "/sample/method",
      |  "method" : "GET"
      |}
    """.stripMargin
  val methodWithoutMethodType =
   """{
  | "path": "/statuses/public_timeline.:format"
  |}
  """.stripMargin
  val methodWithoutPath =
    """{
      |  "method" : "GET"
      |}
    """.stripMargin
  val methodComplete =
  """
    |{
    |     "path" : "/:format/user/show/:username",
    |     "method" : "POST",
    |     "form-data" : {
    |     "values[name]"       : ":name",
    |      "values[email]"      : ":email",
    |        "values[blog]"       : ":blog",
    |        "values[company]"    : ":company",
    |        "values[location]"   : ":location"
    |     },
    |     "required_params" : [
    |        "format",
    |        "username"
    |     ],
    |     "optional_params" : [
    |        "name",
    |        "email",
    |        "blog",
    |        "company",
    |        "location"
    |     ],
    |     "authentication" : true
    |  }
  """.stripMargin

  "A Method" should
    "return error when empty" in {
    Parse.decodeOr[String,Method](methodEmpty, _.path, "error") should be("error")
  }
  it should "return error if not method verb specified" in {
    Parse.decodeOr[String, Method](methodWithoutMethodType, _.path, "error") should be("error")
  }
  it should "return error if not path specified" in {
    Parse.decodeOr[String, Method](methodWithoutPath, _.path, "error") should be("error")
  }
  it should "return a method if path and verb are specified" in {
    Parse.decodeOr[String, Method](methodMinimal, _.path, "error") should be("/sample/method")
  }
  it should "return a method when a more complex (but valid) method is passed" in {
    Parse.decodeOr[String, Method](methodComplete, _.path, "error") should be("/:format/user/show/:username")
  }
  it should "return failure when trying to parse from an arbitrary string" in {
    Parse.decodeOr[String, Method]("test-test-test", _.path, "error") should be("error")
  }
}
