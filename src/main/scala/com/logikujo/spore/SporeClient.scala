package com.logikujo.spore

/**
 *
 * spores / LogiDev - [Fun Functional] / Logikujo.com
 *
 * com.logikujo.spore 30/06/14 :: 22:05 :: eof
 *
 */

import scala.util.{Try, Success, Failure}

import scalaz._, Scalaz._

sealed case class SporeClient(specification: Specification)

object SporeClient {
  def client(url: String): V[SporeClient]  = Specification(url)() match {
    case Success(spec) => SporeClient(spec).right[String]
    case Failure(exc) => exc.toString.left[SporeClient]
  }
}

trait SporeClientImplicits {
  implicit class SporeClientOps(c: SporeClient) {
    def apply(m: String): V[MethodRequest] = c.specification(m)
  }
}
