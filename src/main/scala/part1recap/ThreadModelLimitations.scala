package part1recap

import scala.concurrent.Future

object ThreadModelLimitations extends App {

  /** Rants
    */

  /**   1. OOP encapsulation is only valid in the single threaded model Think
    *      it's encapsulated because only object on the outside can modify state
    *      But in a multi-threaded environment, you can have multiple threads
    *      accessing the same object and being in its internals
    */

  class BankAccount(private var amount: Int) {
    override def toString: String = "" + amount
    def withdraw(money: Int) = this.amount -= money
    def deposit(money: Int) = this.amount += money
    def getAmount() = amount
  }

  val account = new BankAccount(2000)
  for (_ <- 1 to 1000) {
    new Thread(() => account.withdraw(1)).start()
  }

  for (_ <- 1 to 1000) {
    new Thread(() => account.deposit(1)).start()
  }

  println(account.getAmount())

  // OOP encapsulation is broken in multithreaded
  // synchronization! Locks to the rescue
  // deadlocks, livelocks

  /**
    * 2. delegating something to a thread is a PAIN
    */

    // you have a running thread and you want to pass a runnable to that thread

    var task: Runnable = null 
    val runningThread: Thread = new Thread(() => {
      while (true) {
        while (task == null) {
            runningThread.synchronized({
                println("[background] waiting for a task...")
                runningThread.wait()
            })

            task.synchronized({
              println("[background] I have a task")
                task.run()
                task = null
            })
          
       
        }
        task.run() // oops
        task = null
      }
    })

    def delegateToBackgroundThread(r: Runnable) = {
      if (task == null) task = r
      runningThread.synchronized({
        runningThread.notify()
      })
    }

    runningThread.start()
    Thread.sleep(500)
    delegateToBackgroundThread(() => println(42))
    Thread.sleep(1000)
    delegateToBackgroundThread(() => println("this should run in the background"))


    /**
      * 3. tracing and dealing with errors in a multithreaded env is a PAIN
      */
    
    // 1M numbers in between 10 threads
    import scala.concurrent.ExecutionContext.Implicits.global
    val futures = (0 to 9).map(i => 100000 * i until 100000 * (i + 1))
      .map(range => Future {
        if (range.contains(546735)) throw new RuntimeException("invalid number")
        range.sum
      })
    
    val sumFuture = Future.reduceLeft(futures)(_ + _) // Future with the sum of all the numbers
    sumFuture.onComplete(println)
    
}
