package part2actors

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props

object ActorsIntro extends App {
    val actorSystem = ActorSystem("firstActorSystem")
    println(actorSystem.name)

    class WordCountActor extends Actor {

        // Internal data
        var totalWords = 0

        // Behaviour
        // Receive = PartialFunction[Any, Unit]
        def receive: Receive = {
        
            case message: String => 
                println(s"I have received: ${message}")
                totalWords += message.split(" ").length
            case msg => println(s"I can't understand ${msg.toString}")
        }
    }

    // Instantiate + send message
    val wordCounter = actorSystem.actorOf(Props[WordCountActor](), "wordCounter");
    var anotherWordCounter = actorSystem.actorOf(Props[WordCountActor](), "anotherWordCounter");

    // Infix notation
    wordCounter ! "I am learning Akka and it's pretty damn cool!" // Alias for 'tell'
    anotherWordCounter ! "A different message"
    // Async

    class Person(name: String) extends Actor {
        def receive: Receive = {
            case "hi" => println(s"Hi, my name is ${name}")
            case _ =>
        }
    }

    // This instantiation is legal
    val person = actorSystem.actorOf(Props(new Person("Bob")))
    person ! "hi"

    // Above isn't great, best practice:
    // Declare companion object with factory method
    object Person {
        def props(name: String) = Props(new Person(name))
    }
    val person2 = actorSystem.actorOf(Person.props("Bob"))
}
