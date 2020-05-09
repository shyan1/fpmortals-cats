import scala.concurrent.Future

trait TerminalSync {
  def read(): String

  def write(t: String): Unit
}

trait TerminalAsync {
  def read(): Future[String]

  def write(t: String): Future[Unit]
}

/**
 * HKT (Higher-Kinded Types)
 * Higher Kinded Types allows us to use a type constructor in our type
 * parameters, which looks like `C[_]`.
 *
 * `List` is a type constructor because it takes a type (e.g. Int) and
 * constructs a type (List[Int]).
 *
 * Other type constructors are: `Either[E, _]`, `Option`, `State[S,_]` ...
 *
 * type Id[T] = T    // `Id` is a valid type constructor
 *
 *
 * 1 - https://typelevel.org/blog/2016/08/21/hkts-moving-forward.html
 * The bread and butter of everyday functional programming, the “patterns” if
 * you like, is the implementation of standard functional combinators for your
 * datatypes, and more importantly the comfortable, confident use of these
 * combinators in your program.
 *
 * For example, confidence with bind, also known as >>= or flatMap, is very
 * important.
 * def flatMap[B](f: A => List[B]): List[B]            // in List[A]
 * def flatMap[B](f: A => Option[B]): Option[B]        // in Option[A]
 * def flatMap[B](f: A => Either[E, B]): Either[E, B]  // in Either[E, A]
 * def flatMap[B](f: A => State[S, B]): State[S, B]    // in State[S, A]
 *
 * All flatMaps are the same
 *
 * In programming, when we encounter such great sameness—not merely similar
 * code, but identical code—we would like the opportunity to parameterize:
 * extract the parts that are different to arguments, and recycle the common
 * code for all situations.
 *
 * trait Bind[F[_]] {
 * def map[A, B](fa: F[A])(f: A => B): F[B]
 * def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
 * }
 *
 */

// We can define a common interface for async and sync terminals.
trait Terminal[C[_]] {
  def read(): C[String]

  def write(t: String): C[Unit]
}

type Now[X] = X

// In the context of Now
object TerminalSync extends Terminal[Now] {
  override def read(): String = ???

  override def write(t: String): Unit = ???
}

// In the context of Future
object TerminalAsync extends Terminal[Future] {
  override def read(): Future[String] = ???

  override def write(t: String): Future[Unit] = ???
}


// Now we know nothing about `C`, and cannot do anything with a Now[String] or
// Future[String].

// What we need is a kind of execution environment that lets us call a method
// returning C[T] and then be able to do something with the 'T'.

trait Execution[C[_]] {
  def chain[A, B](c: C[A])(f: A => C[B]): C[B]

  def create[B](b: B): C[B]
}

def echo[C[_]](t: Terminal[C], e: Execution[C]): C[String] = {
  e.chain(t.read()) { in: String =>
    e.chain(t.write(in)) { _: Unit =>
      e.create(in)
    }
  }
}

// But the code for echo is unpleasant.

object Execution {

  implicit class Ops[A, C[_]](c: C[A]) {
    def flatMap[B](f: A => C[B])(implicit e: Execution[C]): C[B] = e.chain(c)(f)

    def map[B](f: A => B)(implicit e: Execution[C]): C[B] = e.chain(c)(f andThen e.create)
  }

}


def echo[C[_]](implicit t: Terminal[C], e: Execution[C]): C[String] = {
  import Execution._

  t.read().flatMap { in: String =>
    t.write(in).map { _: Unit =>
      in
    }
  }
}

def echo[C[_]](implicit t: Terminal[C], e: Execution[C]): C[String] = {
  import Execution._

  for {
    in <- t.read()
    _ <- t.write(in)
  } yield in
}


/**
 * FP is the act of writing programs with pure functions.
 *
 * Pure functions have three properties:
 * - Total: return a value for every possible input
 * - Deterministic: return the same value for the same input
 * - Inculpable(无辜的；无可非议的): no (direct) interaction with the world or program state
 *
 * https://en.wikipedia.org/wiki/Pure_function
 * In computer programming, a pure function is a function that has the following properties:[1][2]
 *    - Its return value is the same for the same arguments
 *    - Its evaluation has no side effects
 * https://www.schoolofhaskell.com/school/starting-with-haskell/basics-of-haskell/3-pure-functions-laziness-io
 * https://github.com/MostlyAdequate/mostly-adequate-guide/blob/master/ch03.md
 *
 *
 * Side effects: directly accessing or changing mutable state (e.g. maintaining
 * a var in a class or using a legacy API that is impure), communicating with
 * external resources (e.g. files or network lookup), or throwing and catching
 * exceptions.
 *
 * We write pure functions by avoiding exceptions, and interacting with the world
 * only through a safe F[_] execution context.
 *
 * As a result, applications built with Future are difficult to reason about.
 *
 * An expression is referentially transparent if it can be replaced with its
 * corresponding value without changing the program’s behaviour.
 *
 * Pure functions are referentially transparent, allowing for a great deal of
 * code reuse, performance optimisation, understanding, and control of a
 * program.
 *
 */


// We can define a simple safe F[_] execution context
final class IO[A](val interpret: () => A) {
  def map[B](f: A => B): IO[B] = IO {
    f(interpret())
  }

  def flatMap[B](f: A => IO[B]): IO[B] = IO {
    f(interpret()).interpret()
  }
}

object IO {
  def apply[A](a: => A): IO[A] = new IO(() => a)
}

object TerminalIO extends Terminal[IO] {
  override def read(): IO[String] = IO {
    io.StdIn.readLine
  }

  override def write(t: String): IO[Unit] = IO {
    println(t)
  }
}

val delayed: IO[String] = echo[IO]

// The impure code inside the IO is only evaluated when we .interpret() the
// value, which is an impure action
delayed.interpret()

/**
 * An application composed of IO programs is only interpreted once, in the main
 * method, which is also called the end of the world.
 */

