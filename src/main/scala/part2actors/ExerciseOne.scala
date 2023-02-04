package part2actors

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props

  class Counter extends Actor {
    var count = 0;

    def receive: Receive = {
      case Increment => count += 1;
      case Decrement => count -= 1;
      case Print     => println(s"My count is: $count")
    }
  }

  case class Increment();
  case class Decrement();
  case class Print();

object ExerciseOne extends App {
  val system = ActorSystem("actorSystem");
  val counter = system.actorOf(Props[Counter](), "counter")
  
  counter ! Increment
  counter ! Increment
  counter ! Decrement
  counter ! Print

  system.terminate();
}
