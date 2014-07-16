package com.logikujo.spore

/**
 *
 * spores / LogiDev - [Fun Functional] / Logikujo.com
 *
 * com.logikujo 24/08/13 :: 16:49 :: eof
 *
 */

import SporeClient._

import scala.util.Try
import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

import java.net.URL
import java.nio.file.Paths

import dispatch._

import scalaz._
import Scalaz._

import argonaut._
import Argonaut._

sealed case class Specification(name: String,
                                authority: Option[String],
                                base_url: Option[String],
                                formats: Option[List[String]],
                                version: String,
                                authentication: Option[Boolean],
                                methods: Map[String, Method])

object Specification {
  case class SpecificationParserException(msg: String) extends SporeException(msg)
  case class SpecificationDecoderException(msg: String) extends SporeException(msg)
  // Tries to parse and deserialize a json String
  private def specFromJson2(json: String): String \/ Specification = Parse.decodeEither[Specification](json)
  private def specFromJson(json: String): Try[Specification] = Parse.decode[Specification](json) match {
    case \/-(spec) => scala.util.Success(spec)
    case -\/(-\/(msg)) => scala.util.Failure(new SporeException(msg) initCause SpecificationParserException(msg))
    case -\/(\/-((msg, history))) => scala.util.Failure(new SporeException(msg) initCause SpecificationDecoderException(history.shows))
    }

  // Returns the promised Spec from URL
  def fromUrl(u: URL): Future[Specification] = for {
    result <- Http(url(u.toString) OK as.String)
    spec <- Promise().complete(specFromJson(result)).future
  } yield spec

  def fromFile(path: String): Future[Specification] = for {
    path <- Paths.get(path).future
    fileContents <- Source.fromFile(path.toString).mkString.future
    spec <- Promise().complete(specFromJson(fileContents)).future
  } yield spec

  def apply(s: String): Future[Specification] = Future(new URL(s)).flatMap(fromUrl).recoverWith {case _ => fromFile(s)}
}

trait SpecificationImplicits {
  type Spec = Specification
  implicit class SpecificationOps(s: Specification) {
    def apply(method: String): V[MethodRequest] = for {
      m <- s.methods.get(method).\/>(s"Unable to call '$method'")
      r <- m.request(s.base_url)
    } yield r
  }

  implicit def SpecificationDecodeJson: DecodeJson[Specification] =
    jdecode7L(Specification.apply)("name",
                                    "authority",
                                    "base_url",
                                    "formats",
                                    "version",
                                    "authentication",
                                    "methods")

  implicit def SpecificationEncodeJson: EncodeJson[Specification] =
    EncodeJson((s: Specification) =>
      ("name" := s.name) ->:
        ("authority" := s.authority) ->:
        ("base_url" := s.base_url) ->:
        ("formats" := s.formats) ->:
        ("version" := s.version) ->:
        ("authentication" := s.authentication) ->:
        ("methods" := s.methods) ->: jEmptyObject)
}