package part1recap

import scala.concurrent.Future

object AdvancedRecap extends App {
  // partial functions

  // Only operates on those values
  // Throws exception on another function
  var partialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 65
    case 5 => 999
  }

  val pf = (x: Int) =>
    x match {
      case 1 => 42
      case 2 => 65
      case 5 => 999
    }

  val function: (Int => Int) = partialFunction

  val modifiedList = List(1, 2, 3).map({
    case 1 => 42
    case _ => 0
  })

  // Lifting
  val lifted = partialFunction.lift // total function Int => Option[Int]
  lifted(2) // Some(  65)
  lifted(5000) // None

  // orElse
  val pfChain = partialFunction.orElse[Int, Int] { case 60 =>
    9000
  }

  pfChain(5) // 999 as per partialFunction
  pfChain(60) // 9000
  pfChain(457) // throws a MatchError

  // Type aliases
  type ReceiveFunction = PartialFunction[Any, Unit]
  def receive: ReceiveFunction = {
    case 1 => println("hello")
    case _ => println("confused...")
  }

  // implicits
  implicit val timeout = 3000 // This is passed into the 2nd param list for us
  def setTimeout(f: () => Unit)(implicit timeout: Int) = f()
  setTimeout(() => println("timeout")) // extra parameter list omitted

  // implicit conversions
  // 1) implicit defs
  case class Person(name: String) {
    def greet = s"Hi, my name is $name"
  }

  implicit def fromStringToPerson(string: String): Person = Person(string)

  "Peter".greet // fromStringToPerson("Peter").greet
  // fromStringToPerson("Peter").greet
  // Compiler magic

  // 2) implicit classes
  implicit class Dog(name: String) {
    def bark = println("bark!")
  }

  "Lassie".bark
  // new Dog("Lassie").bark
  // They must be defined inside another trait/class/object.

  // object Helpers {
  //   implicit class RichInt(x: Int) // OK!
  // }
  // implicit class RichDouble(x: Double) // BAD!

  // They may only take one non-implicit argument in their constructor.

  // implicit class RichDate(date: java.util.Date) // OK!
  // implicit class Indexer[T](collection: Seq[T], index: Int) // BAD!
  // implicit class Indexer[T](collection: Seq[T])(implicit index: Index) // OK!

  // The implicit def introduced by implicit class must not be ambiguous with respect to other term members.

  // object Bar
  // implicit class Bar(x: Int) // BAD!

  // val x = 5
  // implicit class x(y: Int) // BAD!

  // implicit case class Baz(x: Int) // BAD!

  // Organize
  // local scope 
  implicit val inverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
    List(1,2,3).sorted // List(3,2,1)

    // imported scope
    import scala.concurrent.ExecutionContext.Implicits.global
    val future = Future {
        println("Hello, future")
    }

    // companion objects of the types involved in the call
    object Person {
        implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
        List(Person("Bob"), Person("Alice")).sorted // List(Person(Alice), Person(Bob))
    }
}
