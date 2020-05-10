// Monad
// Monadic types
// Implicit classes
// Implicit classes as parameter

/**
 * https://typelevel.org/cats/typeclasses/functor.html
 *
 * Functor is a type class that abstracts over type constructors that can be 'map'ed over.
 * Examples of such type constructors are 'List', 'Option', and 'Future'.
 */
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

// example implementation for Option
implicit val functorForOption: Functor[Option] = new Functor[Option] {
  override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa match {
    case None => None
    case Some(a) => Some(f(a))
  }
}

// A `Functor` instance must obey two laws:
// - Composition    fa.map(f).map(g) === fa.map(f andThen g)
// - Identity       fa.map(x => x) === fa


/**
 * Applicative extends Functor with an 'ap' and 'pure' methods.
 */
trait Applicative[F[_]] extends Functor[F] {
  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]

  def pure[A](a: A): F[A]

  def map[A, B](fa: F[A])(f: A => B): F[B] = ap(pure(f))(fa)
}


/**
 * Monad extends the Applicative type class with a new function flatten.
 */

Option(Option(1)).flatten // Some(1)
Option(None).flatten // None
List(List(1), List(2, 3, 4)) // List(1,2,3,4)

/**
 * https://itnext.io/benefits-of-identity-monad-in-scala-cats-a2cb0baef639
 * https://blog.knoldus.com/scalafp-lets-find-reasons-behind-monads/
 */


/**
 * https://docs.scala-lang.org/overviews/core/implicit-classes.html
 *
 * An implicit class is a class marked with the implicit keyword. This keyword
 * makes the class’s primary constructor available for implicit conversions
 * when the class is in scope.
 */
object Helpers {

  implicit class IntWithTimes(x: Int) {
    def times[A](f: => A): Unit = {
      def loop(current: Int): Unit = {
        if (current > 0) {
          f
          loop(current - 1)
        }
      }

      loop(x)
    }
  }

}

import Helpers._

5 times println("HI")

// Implicit classes have the following restrictions:
// 1 - They must be defined inside of another trait/class/object
// 2 - They may only take one non-implicit argument in their primary constructor
// 3 - There may not be any method, member or object in scope with the same name
//     as the implicit class.

object StringHelpers {
  implicit class StringImprovements(val s: String) {
    def increment = s.map(c => (c+1).toChar)
    def decrement = s.map(c => (c-1).toChar)
    def hideAll = s.replaceAll(".", "*")
  }
}

import StringHelpers._

"HAL".increment
"IBM".decrement
"ABC".hideAll


// https://www.lihaoyi.com/post/ImplicitDesignPatternsinScala.html
