package part1recap

object GeneralRecap {
  def main(args: Array[String]): Unit = {
    val aCondition: Boolean = false

    var aVariable = 42
    aVariable += 1 // aVariable = 43

    // expressions
    val aConditionedVal = if (aCondition) 42 else 65

    // code block
    val aCodeBlock = {
      if (aCondition) 74
      67
    }

    // Unit - Side effects
    val theUnit = println("Hello Scala")

    def aFunction(x: Int): Int = x + 1
    // Recursion
    // TAIL recursion
    def factorial(n: Int, acc: Int): Int =
      if (n <= 0) acc
      else factorial(n - 1, acc * n)

    // OOP
    class Animal 
    class Dog extends Animal
    val aDog: Animal = new Dog // subtyping polymorphism

    //  Interface
    trait Carnivore {
      // Abstract
      def eat(a: Animal): Unit
    }

    class Crocodile extends Animal with Carnivore {
      override def eat(a: Animal): Unit = println("Crunch!")
    }

    // Method notations
    val aCrocodile = new Crocodile
    aCrocodile.eat(aDog)
    aCrocodile eat aDog // natural language but identical

    // Anonymous classes
    val aCarnivore = new Carnivore {
      override def eat(a: Animal): Unit = println("Roar!")
    }

    aCarnivore eat aDog

    // Generics
    abstract class MyList[+A] // Extends aDog extends Animal as well (+)
    // companion object (singleton?)
    object MyList

    // Case classes
    case class Person(name: String, age: Int) // Serializable, comparable, pattern matching

    // Exceptions
    val aPotentialFailure = try {
      throw new RuntimeException("I'm innocent, I swear!") // Nothing type
    } catch {
      case e: Exception => "I caught an exception"
    } finally {
      println("some logs")
    }

    // Functional programming

    val incrementer = new Function1[Int, Int] {
      override def apply(v1: Int): Int = v1 + 1
    }

    val incremented = incrementer(42) // 43
    // incrementer.apply(42)

    val anonymousIncrementer = (x: Int) => x + 1 // Lambda syntax...creates obj as above

    // FP is all about working with functions as first-class
    List(1, 2, 3).map(incrementer) // map = HOF

    // For comprehensions
    val pairs = for {
      num <- List(1, 2, 3) // if condition
      char <- List('a', 'b', 'c')
    } yield s"${num}-${char}"

    // List(1, 2, 3).flatMap(num => List('a', 'b', 'c').map(char => num + "-" + char))
    // pairs = List(1-a, 1-b, 1-c, 2-a, 2-b, 2-c, 3-a, 3-b, 3-c)

    // Scala collections: Seqs, Arrays, Lists, Vectors, Maps, Tuples
    // "Collections"
    // Options / Try
    val anOption = Some(2)

    // ?
    // val aTry = Try {
    //   throw new RuntimeException
    // }

    // Pattern matching
    val unknown = 2
    var order = unknown match {
      case 1 => "first"
      case 2 => "second"
      case 3 => "third"
      case _ => s"${unknown}th"
    }

    val bob = Person("Bob", 22)
    val greeting = bob match {
      case Person(n, _) => s"Hi, my name is $n"
      case _ => "I don't know my name"
    }

    // All the patterns

  }
}
