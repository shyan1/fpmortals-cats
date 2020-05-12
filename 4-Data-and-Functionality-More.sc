// http://miklos-martin.github.io/learn/fp/category-theory/2018/01/29/adventures-in-category-theory-introduction.html
// http://miklos-martin.github.io/learn/fp/category-theory/2018/02/01/adventures-in-category-theory-the-algebra-of-types.html

// https://www.47deg.com/blog/fp-for-the-average-joe-part-1-scalaz-validation/
// https://www.47deg.com/blog/fp-for-the-average-joe-part-2-scalaz-monad-transformers/
// https://www.47deg.com/blog/fp-for-the-average-joe-part3-free-monads/

// Category Theory
/**
 * Composition. Category theory is all about composition.
 * Abstraction. Abstraction is important. Finding the right abstractions is hard.
 *
 * Category Theory has strong mathematical foundations.
 *
 * A category consists of objects and morphisms (or arrows) between these objects.
 * An object can be anything, it has nothing to do with the term we are familiar with from programming.
 * We can think of a category as a graph with some special rules.
 *
 * Arrows compose, so if we have an arrow from object `a` to object `b` called `f` and an arrow from object `b`
 * to object `c` called `g`, then we can obtain an arrow from `a` to `c` called `h` by composing those two
 * arrows together `h = g ◦ f`.
 *
 * Every object must have an identity arrow (id), which goes from itself to itself.
 *
 * Arrow composition is associative, so given three arrows: from a to b , from b to c and from c to d, then it does
 * not matter if we obtain the arrow from a to d by composing the first two together first and composing that with
 * the third, or the other way around.
 *
 * f◦g◦h === (f◦g)◦h === f◦(g◦h)
 *
 * Monoid
 * Every category with a single object is called a Monoid.
 * The most common examples are addition for integers with identity element being `0`, or multiplication with the
 * id `1`, or string concatenation with the id being the empty string.
 *
 * Isomorphism (同型，同构)
 * In category theory, we don’t really need equality, it is good enough if two objects are isomorphic.
 * objects a and b are isomorphic, if there is an arrow from a to b, say, f and an arrow from b to a, say, ‘g’, so
 * composing f and g is the same as the identity arrow.
 * f◦g=id and  g◦f=id
 *
 *
 */
