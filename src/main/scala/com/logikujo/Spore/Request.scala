package com.logikujo.Spore

import Spore._
import scalaz._
import Scalaz._
import dispatch._
import com.ning.http.client.RequestBuilder
import scala.util.matching.Regex.Match

/**
 *
 * spores / LogiDev - [Fun Functional] / Logikujo.com
 *
 * com.logikujo.Spore 24/08/13 :: 17:16 :: eof
 *
 */

sealed trait HttpMethod
sealed trait GET extends HttpMethod
sealed trait POST extends HttpMethod

sealed trait ParamsReplacer {
  val str: String
  private val r = ":([a-zA-Z0-9]+)".r
  private def f(p:Params) = (m:Match) => {
    val s = m.toString.drop(1)
    (p get s).toSuccess(s"Param '$s' is required").toValidationNEL
  }

  def apply(params: Params) = {
    val reduce = (a: String, b: (Match, String)) => b._1.matched.r.replaceFirstIn(a, b._2)
    val zipAndReduce = (a:List[Match], b:List[String]) => (a zip b).foldLeft(str)(reduce)
    val matchList = r.findAllMatchIn(str).toList
    (matchList.successNel[String] |@|
      (matchList <*> f(params).pure[List]).sequenceU)(zipAndReduce)
  }
}

object ParamsReplacer {
  def apply(s: String):ParamsReplacer = new ParamsReplacer {
    val str = s
  }
}

sealed trait HttpRequest[M <: HttpMethod] {
  val setParams: SetParamsF
  val url: RequestBuilder
  def doRequest[M](params: Params) =
    Http(setParams(url, params) OK as.String).either
}

private object HttpRequest {
  def apply[M](rb: RequestBuilder) = (f:SetParamsF) => new HttpRequest[GET] {
    val url = rb
    val setParams = f
  }
}

trait MethodRequestOps {
  import MethodRequest._
  val v: MethodRequest
  private val placeHolder = ":([^:]+)".r
  private def availableParams(listParams: List[String]) = listParams.
    filter(v.params.get(_).isDefined).map(a => a -> v.params.get(a).get).toMap
  lazy val requiredParams = availableParams(v.method.required_params)
  lazy val optionalParams = availableParams(v.method.optional_params)
  lazy val replacedPath: scalaz.ValidationNEL[String, List[String]] = v.method.path.drop(1).split("/").toList.map { str =>
    placeHolder.findFirstIn(str) ? {
      requiredParams.get(str.tail).
        toSuccess(s"Parameter '${str.tail}' is required").
        toValidationNEL
    } | str.successNel
  }.sequenceU
  def +(p:Param) = addParams(p) exec v
  def +(p:Params) = addParams(p) exec v
  // Monadic + operator
  def +>(p:Param) = (v + p).right
  def +>(p:Params) = (v + p).right

  // Creates a new HttpRequest and executes it. \/ are transformed to Promises
  def !> = {
    def mkRequestBuilder(url: RequestBuilder, path:List[String]) =
      path.foldLeft(url)((a,b) => a / b)
    def mkHttpRequest(httpMethod:String) = (u: RequestBuilder) => httpMethod match {
      case "GET" => u.doRequest[GET](v.params)
      //case "POST" => u.mkRequest[POST](v.params)
    }
    val url = v.method.base_url.map(dispatch.url(_)).get.successNel[String]
    val rb = (url |@| replacedPath) (mkRequestBuilder)
    (((l:NonEmptyList[String]) => l.list.mkString(";")) <-: rb :-> mkHttpRequest(v.method.method)).disjunction
  }

  def ! = (v.!>).fold(
    e => Http.promise(Left(new Exception(e))),
    p => p
  )
}

case class MethodRequest(method:Method, params: Params)

object MethodRequest {
  val methodRequestParams = Lens.lensu[MethodRequest, Params](
    (o, v) => o.copy(params = v),
    _.params
  )
  def addParams(p:Param) = methodRequestParams %= {_ + p}
  def addParams(p:Params) = methodRequestParams %= {_ ++ p}
}

trait RequestImplicits {
  type Param = (String,String)
  type Params = Map[String, String]
  type SetParamsF = (RequestBuilder, Params) => RequestBuilder

  implicit def HttpRequestGET(rb: RequestBuilder): HttpRequest[GET] =
    HttpRequest[GET](rb) {(r,p) => r <<? p}
  implicit def MethodRequestOps(mr: MethodRequest): MethodRequestOps = new MethodRequestOps {
    val v = mr
  }
}
