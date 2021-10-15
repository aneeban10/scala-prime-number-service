package PrimeNumberProxy

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.StatusCodes
import akka.util.Timeout
import PrimeNumber.PrimeNumberReply

import scala.concurrent.duration.Duration

object ProxyRouteRegistry {
  final case class Primes(number: Int, replyTo: ActorRef[Future[PrimeNumberReply]])
  def apply(): Behavior[Primes] = registry()
  private def registry(): Behavior[Primes] =
    Behaviors.receiveMessage {
      case Primes(number, replyTo) =>
        replyTo ! PrimeNumberProxyForwarder.forwardToPrimeNumberService(number)
        Behaviors.same
    }
}

class ProxyRoutes(proxyRegistry: ActorRef[ProxyRouteRegistry.Primes])(implicit val system: ActorSystem[_]) {
  val BadRequestMsg = "Please pass a positive Integer less than 10000 only"
  val InternalServerErrorMsg = "External Service seems to be down"
  private val DurationToTimeout = Duration(5, "s")
  private implicit val timeout = Timeout.durationToTimeout(DurationToTimeout)

  def getPrimes(limit: Int): Future[PrimeNumberReply] =
    proxyRegistry.ask(Primes(limit, _)).flatten

  val proxyRoutes: Route =
    pathPrefix("prime") {
      (get & path(Segment)) { limit =>
        limit matches "^(?![0]$)\\d{1,4}$" match {
          case true => onComplete(getPrimes(limit.toInt)) { response =>
            response.fold(
              _ => complete(StatusCodes.InternalServerError -> InternalServerErrorMsg),
              fb => complete(StatusCodes.OK -> fb.primeList)
            )}
          case false => complete(StatusCodes.BadRequest -> BadRequestMsg)
        }
      }
    }
}
