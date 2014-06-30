package com.logikujo.spore

/**
 *
 * spores / LogiDev - [Fun Functional] / Logikujo.com
 *
 * com.logikujo.Spore 30/06/14 :: 21:37 :: eof
 *
 */

import scalaz._
import Scalaz._

import argonaut._
import Argonaut._

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


object MethodL {
  val baseUrlML = Lens.lensu[Method, Option[String]](
    (o, v) => o.copy(base_url = v),
    _.base_url
  )
}

trait MethodImplicits {
  import Method._
  implicit class MethodOps(m: Method) {
    def _base_url(p: Option[String]) = MethodL.baseUrlML.set(m, p)
    def request(base_url: Option[String]): String \/ MethodRequest =
      m.base_url.orElse(base_url).\/>("'base_url' is required.") ∘
        (u => MethodRequest(m._base_url(u.some), Map.empty))
  }
  implicit def MethodDecodeJson: DecodeJson[Method] =
    jdecode10L(Method.apply)("method",
                              "description",
                              "documentation",
                              "path",
                              "optional_params",
                              "required_params",
                              "expected",
                              "authenticacion",
                              "base_url",
                              "formats")
  implicit def MethodEncondeJson: EncodeJson[Method] =
    EncodeJson((m: Method) =>
      ("method" := m.method) ->:
        ("description" := m.description) ->:
        ("documentation" := m.documentation) ->:
        ("path" := m.path) ->:
        ("optional_params" := m.optional_params) ->:
        ("required_params" := m.required_params) ->:
        ("expected" := m.expected) ->:
        ("authentication" := m.authentication) ->:
        ("base_url" := m.base_url) ->:
        ("formats" := m.formats) ->: jEmptyObject)
}