package com.logikujo.spore

/**
 *
 * spores / LogiDev - [Fun Functional] / Logikujo.com
 *
 * com.logikujo.spore 30/06/14 :: 22:05 :: eof
 *
 */

import scala.util.{Try, Success, Failure}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import scalaz._, Scalaz._

sealed case class SporeClient(specification: Specification)

object SporeClient {
  def apply(url: String): Future[SporeClient] = Specification(url).map(SporeClient.apply)
}

trait SporeClientImplicits {
  implicit class SporeClientOps(c: SporeClient) {
    def apply(m: String): String \/ MethodRequest = c.specification(m)
  }
}
