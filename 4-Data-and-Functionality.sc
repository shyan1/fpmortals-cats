/**
 * FP takes a different approach, defining data and functionality separately.
 *
 * We prefer abstract class to trait in order to get better binary compatibility and to discourage trait mixing.
 *
 */

// values
case object A

type B = String
type C = Int

// product
final case class ABC(a: A.type, b: B, c: C)

// coproduct
sealed abstract class XYZ

case object X extends XYZ

case object Y extends XYZ

final case class Z(b: B) extends XYZ


// Recursive ADTs
// When an ADT refers to itself, we call it a Recursive Algebraic Data Type.
// The standard library `List` is recursive.
sealed abstract class List[+A]

case object Nil extends List[Nothing]

case class ::[+A](head: A, tail: List[A]) extends List[A]


// Exhaustivity (穷举，彻底性)
// It's important that we use "sealed abstract class", not just "abstract class".
// Sealing a class means that all subtypes are all defined in the same file, allowing the compile to know them in
// pattern matching exhaustivity checks and in macros that eliminate boilerplate.


import eu.timepit.refined
import refined.api.Refined

// All types with two parameters can be written infix in Scala.
// For example, Either[String, Int] is the same as String Either Int.
// It is conventional for Refined to be written infix since A Refined B can be read as “an A that meets the requirements
// defined in B”.
import refined.numeric.Positive
import refined.collection.NonEmpty

final case class Person(
                         name: String Refined NonEmpty,
                         age: Int Refined Positive
                       )


import refined.refineV

refineV[NonEmpty]("hello") // Right("hello")
refineV[NonEmpty]("") // Left(Predicate isEmpty() did not fail.)


import refined.auto._

val sum: String Refined NonEmpty = "Zara"
val empty: String Refined NonEmpty = ""


// Currying:
// A => B => C
// is equivalent to
// (A => B) => C


/**
 * A typeclass is trait that
 * - holds no state
 * - has a type parameter
 * - has at least one abstract method (primitive combinators)
 * - may contain generalised methods (derived combinators)
 * - may extend other typeclasses
 */

/**
 * There can only be one implementation of a typeclass for any given type parameter, a property known as typclass
 * coherence (一致；连贯性；凝聚).
 *
 * Typeclass coherence is primarily about consistency, and the consistency gives us the confidence to use implicit
 * parameters.
 *
 * Additionally, typeclass coherence allows us to globally cache implicits at runtime and save memory allocations,
 * gaining performance improvements from reduced pressure on the garbage collector.
 *
 */
trait Ordering[T] {
  def compare(x: T, y: T): Int

  def lt(x: T, y: T): Boolean = compare(x, y) < 0
  def gt(x: T, y: T): Boolean = compare(x, y) > 0
}

trait Numeric[T] extends Ordering[T] {
  def plus(x: T, y: T): T
  def times(x: T, y: T): T
  def negate(x: T): T
  def zero: T

  def abs(x: T): T = if (lt(x, zero)) negate(x) else x
}
