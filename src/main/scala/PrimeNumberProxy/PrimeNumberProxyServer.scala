package PrimeNumberProxy

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

import scala.util.Failure
import scala.util.Success

object PrimeNumberProxyServer {
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext
    val futureBinding = Http().newServerAt("localhost", 8080).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info(s"Server online at http://${address.getHostString}:${address.getPort}/")
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  def main(args: Array[String]): Unit = {
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val proxyRegistryActor = context.spawn(ProxyRouteRegistry(), "ProxyRegistryActor")
      context.watch(proxyRegistryActor)
      val routes = new ProxyRoutes(proxyRegistryActor)(context.system)
      startHttpServer(routes.proxyRoutes)(context.system)
      Behaviors.empty
    }
    val _ = ActorSystem[Nothing](rootBehavior, "HelloAkkaHttpServer")
  }
}
