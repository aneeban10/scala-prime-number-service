package PrimeNumber

import scala.concurrent.Future
import akka.actor.typed.ActorSystem

class PrimeNumberServiceImpl(system: ActorSystem[_]) extends PrimeNumberService {
  private implicit val sys: ActorSystem[_] = system

  override def getPrimes(request: PrimeNumberRequest): Future[PrimeNumberReply] = {
    Future.successful(PrimeNumberReply(getAllPrimes(request.number)))
  }

  private def getAllPrimes(number: Int): String = number match {
    case x if x <= 1 => "no primes"
    case x => calculateAllPrimes(x)
  }

  // Courtesy of stackoverflow
  private def calculateAllPrimes(end: Int): String = {
    val odds = LazyList.from(3, 2).takeWhile(_ <= Math.sqrt(end).toInt)
    val composites = odds.flatMap(i => LazyList.from(i * i, 2 * i).takeWhile(_ <= end))
    (2 :: LazyList.from(3, 2).takeWhile(_ <= end).diff(composites).toList).mkString(", ")
  }
}
