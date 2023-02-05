package part2actors

import akka.actor.ActorSystem
import akka.actor.Actor
import scala.util.Success
import scala.util.Failure
import akka.actor.Props
import akka.actor.ActorRef

case class Deposit(amount: Int)
case class Withdraw(amount: Int)
case class PrintStatement()
class InsufficientFundsError extends Exception("Insufficient funds")

class BankAccount extends Actor {
  var balance = 0

  def receive: Receive = {
    case Deposit(amount) if amount > 0 =>
      balance += amount
      println("changing balance")
      context.sender() ! Success("Deposit success")
    case Deposit(amount) =>
      context.sender() ! Failure(new Exception(s"Deposit must be positive, received $amount"))
    case Withdraw(amount) if amount > 0 =>
      val provisionalBalance = balance - amount
      if (provisionalBalance < 0)
        context.sender() ! Failure(new InsufficientFundsError)
      else
        balance = provisionalBalance;
        context.sender () ! Success("Withdrawal success")
    case Withdraw(amount) =>
      Failure(new Exception(s"Withdrawal must be positive, received $amount"))
    case PrintStatement => println(s"[${self.path}] Balance: ${balance}")
    }
}

case class AttemptDeposit(amount: Int, ref: ActorRef)
case class AttemptWithdraw(amount: Int, ref: ActorRef)

class AccountHolder extends Actor {
    def receive: Receive = {
        case AttemptDeposit(amount, ref) =>
            println("Attempting deposit")
            ref ! Deposit(amount)
        case AttemptWithdraw(amount, ref) =>
            println("Attempting withdrawal")
            ref ! Withdraw(amount) 
            
        case Success(f) => 
            println(s"[${self.path}] :D")
            context.sender() ! PrintStatement
        
        case Failure(e: InsufficientFundsError) => 
            println(s"[${self.path}] D: No monies!")
            context.sender() ! PrintStatement
        
        case Failure(e) =>
              println(s"[${self.path}] D:")
              println(e.toString())
    
        }
} 

object Exercise1_2 extends App {
  val system = ActorSystem("system");


  val myAccount = system.actorOf(Props[BankAccount](), "myAccount")
  val me = system.actorOf(Props[AccountHolder](), "me")

  me ! AttemptDeposit(200, myAccount)
    me ! AttemptDeposit(200, myAccount)
    me ! AttemptDeposit(0, myAccount)
    me ! AttemptWithdraw(500, myAccount)
    me ! AttemptDeposit(100, myAccount)
    me ! AttemptWithdraw(500, myAccount)
//   system.terminate()
}
