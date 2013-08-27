package com.logikujo

/**
 *
 * spore / LogiDev - [Fun Functional] / Logikujo.com
 *
 * com.logikujo 24/08/13 :: 16:29 :: eof
 *
 */

import scalaz._
import Scalaz._
import scala.util.{Failure, Success}
import dispatch.{Http, Promise}

package object Spore
  extends SpecificationImplicits
  with RequestImplicits
{
  type Val[A] = String \/ A
  type V[A] = String \/ A
  type Prom[A] = Promise[A]
  type SporeClientV = Val[SporeClient]


  /*saled trait Spore {
     protected val spec: Specification
     def apply(m: String) = spec(m)
   }*/
 /* trait Promised[F] {
    def promise[A](v:F):Promise[Either[Throwable,A]]*/
    /* = (v.!>).fold(
      e => Http.promise(Left(new Exception(e))),
      p => p
      String \/ Promise[A] => Promise
      {type M[X] = scalaz.\/[String,X]; type A = scalaz.Id.Id[com.logikujo.Spore.MethodRequest]}#M
    )*/
 // }

  trait EitherPromised[A] {
    val v: V[Promise[Either[Throwable,A]]]
    def promise: Promise[Either[Throwable,A]] = v.fold(
      e => Http.promise(Left(new Exception(e))),
      p => p
    )
  }

  trait SporeClientOps {
    val v: SporeClient
    def apply(m: String): V[MethodRequest] = v.spec(m)
  }
  sealed case class SporeClient(spec: Specification)

  object Spore {
    def client(url: String): V[SporeClient]  = Specification(url)() match {
      case Success(spec) => SporeClient(spec).right[String]
      case Failure(exc) => exc.toString.left[SporeClient]
    }
  }

//  trait SporeImplicits
    //extends SpecificationImplicits
    //with RequestImplicits {
 // {
  implicit def SporeClientOps(sc: SporeClient) = new SporeClientOps {
    val v = sc
  }

  implicit def EitherPromised[A](d:V[Promise[Either[Throwable, A]]]) = new EitherPromised[A] {
    val v = d
  }

  /*implicit def PromisedFromDisjunction[A](v:String \/ Promise[Either[Throwable,A]]): Promise[Either[Throwable,A]] =
    v.fold(
      e => Http.promise(Left(new Exception(e))),
      p => p)*/
  //}
}
