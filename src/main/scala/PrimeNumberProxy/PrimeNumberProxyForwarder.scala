package PrimeNumberProxy

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.grpc.GrpcClientSettings
import PrimeNumber.{PrimeNumberReply, PrimeNumberRequest, PrimeNumberServiceClient}

import scala.concurrent.{ExecutionContext, Future}

object PrimeNumberProxyForwarder {

  implicit val sys: ActorSystem[_] = ActorSystem(Behaviors.empty, "PrimeNumberProxyForwarder")
  implicit val ec: ExecutionContext = sys.executionContext

  def forwardToPrimeNumberService(number: Int): Future[PrimeNumberReply] =  {
    val client = PrimeNumberServiceClient(GrpcClientSettings.fromConfig("primeNumber.primeNumberService"))
    client.getPrimes(PrimeNumberRequest(number))
  }
}
