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


