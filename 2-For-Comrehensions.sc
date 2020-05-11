import scala.reflect.runtime.universe._

val a, b, c = Option(1)

show {
  reify {
    for {
      i <- a
      j <- b
      k <- c
    } yield (i + j + k)
  }
}

/**
 * `For comprehension` is an abstraction over a couple of monadic operations: `map`, `flatMap`, `withFilter`, and
 * `foreach`.
 *
 */

trait ForComprehensible[C[_]] {
  def map[A, B](f: A => B): C[B]

  def flatMap[A, B](f: A => C[B]): C[B]

  def withFilter[A](f: => Boolean): C[A]

  def foreach[A](f: A => Unit): Unit
}


// `for` comprehensions are fundamentally for defining sequential programs.
// DON'T USE 'Future'.




/// Unhappy path
/**
 *
 * In the 'Option' example, the 'yield' is called only when i,j,k are all defined.
 *    for {
 *      i <- a
 *      j <- b
 *      k <- c
 *    } yield (i+j+k)
 *
 * If any of a,b,c are None, the comprehension short-circuits with 'None' but it doesn't tell us what went wrong.
 *
 * If a function requires every input then it should make its requirement explicit, pushing the responsibility of
 * dealing with optional parameters to its caller.
 */

///  in FP it puts a clear ownership of responsibility for unexpected error recovery and resource cleanup onto the
///  context (which is usually a Monad as we will see later), not the business logic.


