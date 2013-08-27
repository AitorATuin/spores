package com.logikujo
package fancytypes

import scalaz._
import Scalaz._

sealed trait FuncValidated[A, B] {
  val value: A => B

  def vApply(a: A): Validation[Throwable, B] = try {
    val r = value(a)
    r.success
  } catch {
    case t: Throwable => t.failure
  }
}

// Catch exceptions and return Either[Throwable, B]
sealed trait FuncEither[A,B] { 
  val value: A => B
  
  def either(a:A): Either[Throwable,B] = try { 
    val r = value(a)
    Right(r)
  } catch { 
    case t: Throwable => Left(t)
  }
}

sealed trait FuncImplicits {
  implicit def toValidation[A, B](f: A => B) = new FuncValidated[A, B] {
    val value = f
  }
  implicit def toEither[A,B](f: A => B) = new FuncEither[A,B] { 
    val value = f
  }
}

object Implicits extends  FuncImplicits
