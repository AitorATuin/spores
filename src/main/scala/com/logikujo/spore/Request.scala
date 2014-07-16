package com.logikujo.spore

import scalaz._
import Scalaz._
import dispatch._, Defaults._
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

sealed trait HttpRequest[M <: HttpMethod] {
  val setParams: SetParamsF
  val url: Req
  def doRequest[M](params: Params) =
    Http(setParams(url, params) OK as.String)
}

private object HttpRequest {
  def apply[M](req: Req) = (f:SetParamsF) => new HttpRequest[GET] {
    val url = req
    val setParams = f
  }
}

trait MethodRequestOps {
  import MethodRequest._
  val v: MethodRequest

  private def availableParams(listParams: List[String]) = listParams.collect {
    case param if v.params.contains(param) => param -> v.params(param)
  }.toMap
  private lazy val replacedPath = ParamsReplacer(v.method.path)(requiredParams.getOrElse(Map.empty))
  private lazy val requiredParams: Option[Params] = v.method.required_params.map(availableParams)
  private lazy val optionalParams: Option[Params] = v.method.optional_params.map(availableParams)
  /*private val placeHolder = ":([^:]+)".r
  private def availableParams(listParams: List[String]) = listParams.collect {
    case param if v.params.contains(param) => param -> v.params(param)
  }.toMap
  private def containsParam(paramsOpt: Option[Params]): String => Option[String] = param => for {
    _ <- placeHolder.findFirstIn(param)
    params <- paramsOpt
    paramValue <- params.get(param)
  } yield paramValue
  private val isOptionalParam: String => Option[String] = containsParam(optionalParams)
  private val isRequiredParam: String => Option[String] = containsParam(requiredParams)
  lazy val requiredParams: Option[Params] = v.method.required_params.map(availableParams)
  lazy val optionalParams: Option[Params] = v.method.optional_params.map(availableParams)
  lazy val replacedPath = v.method.path.drop(1).split("/").toList.map {
      case str if isRequiredParam(str).isDefined =>
      case str => str.success[NonEmptyList[String]]
    }*/
  /*lazy val replacedPath: ValidationNel[String, List[String]] = v.method.path.drop(1).split("/").toList.map { str =>
    placeHolder.findFirstIn(str) ? {
      requiredParams.get(str.tail).
        toSuccess(s"Parameter '${str.tail}' is required").
        toValidationNel
    } | str.successNel
  }.sequenceU*/
  def +(p:Param) = addParams(p) exec v
  def +(p:Params) = addParams(p) exec v
  // Monadic + operator
  def +>(p:Param) = (v + p).right
  def +>(p:Params) = (v + p).right

  // Creates a new HttpRequest and executes it. \/ are transformed to Promises
  def !> = {
    def mkReq(url: Req, path:String): Req =
      path.split("/").foldLeft(url)((a,b) => a / b)
    def mkHttpRequest(httpMethod:String) = (u: Req) => httpMethod match {
      case "GET" => u.doRequest[GET](v.params)
      //case "POST" => u.mkRequest[POST](v.params)
    }
    val url = v.method.base_url.map(dispatch.url(_)).get.successNel[String]
    val rb: Validation[NonEmptyList[String], Req] = (url |@| replacedPath) (mkReq)
    (((l:NonEmptyList[String]) => l.list.mkString(";")) <-: rb :-> mkHttpRequest(v.method.method)).disjunction
  }

  def ! = (v.!>).fold(
    e => Future(Left(new Exception(e))),
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
  type SetParamsF = (Req, Params) => Req

  implicit def HttpRequestGET(req: Req): HttpRequest[GET] =
    HttpRequest[GET](req) {(r,p) => r <<? p}
  implicit def MethodRequestOps(mr: MethodRequest): MethodRequestOps = new MethodRequestOps {
    val v = mr
  }
}
