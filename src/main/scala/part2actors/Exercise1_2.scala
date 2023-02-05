package part2actors

import akka.actor.ActorSystem
import akka.actor.Actor
import scala.util.Success
import scala.util.Failure
import akka.actor.Props
import akka.actor.ActorRef

object BankAccount {
  case class Deposit(amount: Int)
  case class Withdraw(amount: Int)
  case class TransactionSuccess(message: String)
  case class TransactionFailure(message: String)
  case object PrintStatement
}

class BankAccount extends Actor {
  import BankAccount._

  var balance = 0

  def receive: Receive = {
    case Deposit(amount) =>
      if (amount <= 0)
        context.sender() ! TransactionFailure(
          s"Deposit must be greater than 0. Received $amount"
        )
      else {
        balance += amount
        context.sender() ! TransactionSuccess(s"Deposited $amount")
      }
    case Withdraw(amount) =>
      if (amount <= 0)
        context.sender() ! TransactionFailure(
          s"Deposit must be greater than 0. Received $amount"
        )
      else if (amount > balance)
        context.sender() ! TransactionFailure(
          "Insufficient funds"
        )
      else {
        balance -= amount
        context.sender() ! TransactionSuccess(s"Withdrew $amount")
      }
    case PrintStatement => println(balance)
  }
}

object AccountHolder {
  case class AttemptDeposit(amount: Int, ref: ActorRef)
  case class AttemptWithdraw(amount: Int, ref: ActorRef)
}

class AccountHolder extends Actor {
  import AccountHolder._
  import BankAccount._
  def receive: Receive = {
    case AttemptDeposit(amount, ref) =>
      ref ! Deposit(amount)

    case AttemptWithdraw(amount, ref) =>
      ref ! Withdraw(amount)

    case TransactionSuccess(msg) =>
      println(s"Machine says: $msg :D")

    case TransactionFailure(msg) =>
      println(s"Machine says: $msg D:")

  }
}

object Exercise1_2 extends App {
  import AccountHolder._
  import BankAccount._

  val system = ActorSystem("system");

  val myAccount = system.actorOf(Props[BankAccount](), "myAccount")
  val me = system.actorOf(Props[AccountHolder](), "me")

  me ! AttemptDeposit(200, myAccount)
  me ! AttemptDeposit(200, myAccount)
  me ! AttemptDeposit(0, myAccount)
  me ! AttemptWithdraw(500, myAccount)
  me ! AttemptDeposit(100, myAccount)

  myAccount ! PrintStatement
}
