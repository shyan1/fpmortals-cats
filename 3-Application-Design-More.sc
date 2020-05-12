// It is good practice in FP to encode constraints in parameters and return types

// cats' NonEmptyList https://typelevel.org/cats/datatypes/nel.html


import cats.data.NonEmptyList

NonEmptyList.one(1)
NonEmptyList.one("Hello")

NonEmptyList.of(1,2,3,4,5)
NonEmptyList.of("hello", "world")

NonEmptyList.ofInitLast(List(), 3)
NonEmptyList.ofInitLast(List(1,2,3,4), 9)
NonEmptyList.ofInitLast(List(), "Hello")

NonEmptyList.fromList(List())
NonEmptyList.fromList(List[Int]())
NonEmptyList.fromList(List(1,2,3,4))


import cats.syntax.list._

List(1,2,3,4).toNel

// Even more general, you can use NonEmptyList.fromFoldable and NonEmptyList.fromReducible.
import cats.implicits._

NonEmptyList.fromFoldable(List())
