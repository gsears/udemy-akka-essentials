package part2actors

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props

// Domain of the counter
object Counter {
  case object Increment
  case object Decrement
  case object Print
}

class Counter extends Actor {
  import Counter._
  
  var count = 0;

  def receive: Receive = {
    case Increment => count += 1;
    case Decrement => count -= 1;
    case Print     => println(s"My count is: $count")
  }
}

object Exercise1_1 extends App {
  import Counter._

  val system = ActorSystem("actorSystem");
  val counter = system.actorOf(Props[Counter](), "counter")

  (1 to 5).foreach(_ => counter ! Increment)
  (1 to 3).foreach(_ => counter ! Decrement)
  counter ! Print

  system.terminate();
}
