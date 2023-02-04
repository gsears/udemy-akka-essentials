package part2actors

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorContext
import akka.actor.ActorRef

object ActorCapabilities extends App {
  class SimpleActor extends Actor {

    override def receive: Receive = {
      case "Hi" => context.sender() ! "Hello, there!" // replying to a message
      case message: String =>
        println(s"[${self.path}] I have recieved $message")
      case number: Int =>
        println(s"[simple actor] I have recieved a NUMBER: $number")
      case SpecialMessage(contents) =>
        println(s"[simple actor] I have recieved something SPECIAL: $contents")
      case SendMessageToYourself(content) =>
        println("Autoesteem is important")
        self ! content
      case SayHiTo(ref) => ref ! "Hi" // Alice is being passed as the sender
      case WirelessPhoneMessage(content, ref) => ref forward (content + "s") // I keep the original sender of the WPM
    }
  }

  val system = ActorSystem("actorCapabilitiesDemo")
  val simpleActor = system.actorOf(Props[SimpleActor](), "simpleActor")
  simpleActor ! "hello, actor"

  // 1. Messages can be any type
  // a) messages must be IMMUTABLE
  // b) messages must be SERIALIZABLE // JVM can transform it into a byte stream
  // I wonder what the protocol is...

  simpleActor ! 42

  case class SpecialMessage(contents: String)
  simpleActor ! SpecialMessage("some special content")

  // 2. Actors have information about their context and about themselves
  // context.self === `this` in OOP
  // Can send messages to self

  case class SendMessageToYourself(content: String)
  simpleActor ! SendMessageToYourself("I am an actor and I am proud of it")

  // 3. Actors can REPLY to messages
  val alice = system.actorOf(Props[SimpleActor](), "alice")
  val bob = system.actorOf(Props[SimpleActor](), "bob")

  case class SayHiTo(ref: ActorRef)
  alice ! SayHiTo(bob)
  // alice will send a message to bob

  // 4. Dead letters
  alice ! "Hi"

    // 5. Forwarding messages
    case class WirelessPhoneMessage(content: String, ref: ActorRef)
    alice ! WirelessPhoneMessage("Hi", bob) // noSender
}
