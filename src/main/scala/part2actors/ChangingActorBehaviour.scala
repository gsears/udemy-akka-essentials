package part2actors

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props

object ChangingActorBehaviour extends App {

  object FussyKid {
    case object Accept
    case object Reject
    val HAPPY = "happy"
    val SAD = "sad"
  }
  class FussyKid extends Actor {
    import FussyKid._
    import Mom._

    var state = HAPPY
    def receive: Receive = {
      case Mom.Feed(VEGETABLE) => state = SAD
      case Mom.Feed(CHOCOLATE) => state = HAPPY
      case Mom.Ask(_) =>
        if (state == HAPPY) sender() ! Accept
        else sender() ! Reject
    }
  }

  class StatelessFussyKid extends Actor {
    import FussyKid._
    import Mom._
    
    override def receive: Receive = happyReceive

    def happyReceive: Receive = {
        case Mom.Feed(VEGETABLE) => context.become(sadReceive)
        case Mom.Feed(CHOCOLATE) =>
        case Ask(_) => sender() ! Accept
    }
    def sadReceive: Receive = {
        case Mom.Feed(VEGETABLE) =>
        case Mom.Feed(CHOCOLATE) => context.become(happyReceive)
        case Ask(_) => sender() ! Reject
    }
  }

  object Mom {
    case class Start(kidRef: ActorRef)
    case class Feed(food: String)
    case class Ask(message: String) // Do you want to play?
    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"
  }
  class Mom extends Actor {
    import Mom._
    import FussyKid._
    def receive: Receive = {
        case Start(kidRef) => 
            // test our interaction
            kidRef ! Feed(VEGETABLE)
            kidRef ! Ask("Do you want to play?")
        case FussyKid.Accept => println("Yay, my kid is happy!")
        case FussyKid.Reject => println("My kid is sad, but at least he's healthy!")
    } 
  }
  
  val system = ActorSystem("changingActorBehaviourDemo")
  val fussyKid = system.actorOf(Props[FussyKid](), "fussyKid")
  val statelessFussyKid = system.actorOf(Props[StatelessFussyKid](), "statelessFussyKid")
  val mom = system.actorOf(Props[Mom](), "mom")

  mom ! Mom.Start(statelessFussyKid)

  // Stateless

}
