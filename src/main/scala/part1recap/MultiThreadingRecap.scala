package part1recap

import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure

object MultiThreadingRecap extends App {
  // Creating threads on the JVM
    val aThread = new Thread(new Runnable {
        override def run(): Unit = println("I'm running in parallel")
    })

    // Syntax
    val aSweeterThread = new Thread(() => println("Sweet, Scala!"))

    aThread.start()
    aThread.join() // blocks until aThread finishes running

    val threadHello = new Thread(() => (1 to 1000).foreach(_ => println("hello")))
    val threadGoodbye = new Thread(() => (1 to 1000).foreach(_ => println("goodbye")))
    threadHello.start()
    threadGoodbye.start()

    // different runs produce different results!

    // Volatile locks private member for read and write (amount)
    // Only works for primitives
    class BankAccount(@volatile private var amount: Int) {
        override def toString: String = "" + amount

        def withdraw(money: Int) = this.amount -= money

        // Blocking
        def safeWithdraw(money: Int) = this.synchronized {
            this.amount -= money
        }
    }

    /**
      * BA (10000)
      * T1 -> withdraw 1000
      * T2 -> withdraw 2000
      * T1 -> this.amount = this.amount - .... // PREEMPTED by the OS
      * T2 -> this.amount = this.amount - 2000 = 8000
      * T1 -> -1000 = 9000
      * => result = 9000
      * this.amount = this.amount - 1000 is NOT ATOMIC
      */
    

    // inter-thread communication on the JVM
    // wait - notify mechanism

    // Scala Futures
    import scala.concurrent.ExecutionContext.Implicits.global
    val future = Future {
        // long computation - on a different thread
        42
    }

    future.onComplete {
        case Success(42) => println("I found the meaning of life")
        case Failure(_) => println("Something happened with the meaning of life")
    }

    val aProcessedFuture = future.map(_ + 1) // Future with 43
    val aFlatFuture = future.flatMap {
        value => Future(value + 2)
    } // Future with 44

    val filteredFuture = future.filter(_ % 2 == 0) // NoSuchElementException (if type is Future[Int])

    // for comprehensions
    val aNonsenseFuture = for {
        meaningOfLife <- future
        filteredMeaning <- filteredFuture
    } yield meaningOfLife + filteredMeaning
    
    // andThen, recover/recoverWith

    // Promises
}
