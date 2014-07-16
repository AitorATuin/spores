package com.logikujo.spore
package test

/**
 *
 * spores / LogiDev - [Fun Functional] / Logikujo.com
 *
 * 16/07/14 :: 19:20 :: eof
 *
 */

import org.scalatest._
import org.scalatest.matchers._

abstract class UnitSpec
  extends FlatSpec
  with ShouldMatchers
  with OptionValues
{
  def anInstanceOf[T](implicit tag: reflect.ClassTag[T]) = {
    val clazz = tag.runtimeClass
    new BePropertyMatcher[AnyRef] {
      def apply(left: AnyRef) =
        BePropertyMatchResult(clazz.isAssignableFrom(left.getClass),
          "an instance of " + clazz.getName)
    }
  }
}