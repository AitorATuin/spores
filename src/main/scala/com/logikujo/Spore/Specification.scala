package com.logikujo.Spore

import Spore._
import net.liftweb.json._
import scala.util.Try
import java.net.URL
import dispatch._
import java.nio.file.Paths
import scala.io.Source
import scalaz._
import Scalaz._

/**
 *
 * spores / LogiDev - [Fun Functional] / Logikujo.com
 *
 * com.logikujo 24/08/13 :: 16:49 :: eof
 *
 */


case class Method(method: String,
                  description: Option[String],
                  documentation: Option[String],
                  path: String,
                  optional_params: List[String],
                  required_params: List[String],
                  expected: List[Int],
                  authentication: Option[Boolean],
                  base_url: Option[String],
                  formats: List[String])

sealed trait MethodOps {
  import Method._
  val v: Method

  def request(base_url: Option[String]): V[MethodRequest] =
    List(v.base_url, base_url).find(_.isDefined).flatten.\/>("'base_url' is required.").
      map(u => MethodRequest(methodBaseUrl.set(v, u.some), Map.empty))
}

object Method {
  val methodBaseUrl = Lens.lensu[Method, Option[String]](
    (o, v) => o.copy(base_url = v),
    _.base_url
  )
}

sealed case class Specification(name: String,
                                authority: Option[String],
                                base_url: Option[String],
                                formats: List[String],
                                version: String,
                                authentication: Option[Boolean],
                                methods: Map[String, Method])

sealed trait SpecificationOps {
  val v: Spec

  def apply(method: String): V[MethodRequest] = for {
    m <- v.methods.get(method).\/>(s"Unable to call '$method'")
    r <- m.request(v.base_url)
  } yield r
}

object Specification {
  implicit val formats = DefaultFormats

  // Tries to parse and deserialize a json String
  private def specFromJson(json: String): SpecTry = Try {
    parse(json).extract[Spec]
  }

  // Returns the promised Spec from URL
  private def fromUrl(u: URL): SpecTryP =
    Http(url(u.toString) OK as.String).either.
      map(_.fold(e => Try(throw e),
                 s => Try(s)).
           flatMap(specFromJson))

  private def fromFile(path: String): Throwable => SpecTryP = _ =>
    Http.promise(Try(Paths.get(path)).
      map(f => Source.fromFile(f.toString).mkString).
      flatMap(specFromJson))

  def apply(s: String) = Try(fromUrl(new URL(s))).recover {
    case e => fromFile(s)(e)
  }.get // We can use safely get here. Try always returns Success
}

trait SpecificationImplicits {
  type Spec = Specification
  type SpecTry = Try[Specification]
  type SpecP = Prom[Specification]
  type SpecTryP = Prom[SpecTry]
  implicit def SpecificationOps(s:Spec):SpecificationOps = new SpecificationOps {
    val v = s
  }
  implicit def MethodOps(m:Method):MethodOps = new MethodOps {
    val v = m
  }
}