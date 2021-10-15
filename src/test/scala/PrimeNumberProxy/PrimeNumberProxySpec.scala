package PrimeNumberProxy

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import PrimeNumber.PrimeNumberServer
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PrimeNumberProxySpec extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  lazy val testKit1 = ActorTestKit()
  implicit val typedSystem = testKit1.system
  override def createActorSystem(): akka.actor.ActorSystem = testKit1.system.classicSystem
  val userRegistry = testKit1.spawn(ProxyRouteRegistry())
  lazy val ProxyRoutes = new ProxyRoutes(userRegistry)

  val conf = ConfigFactory.parseString("akka.http.server.preview.enable-http2 = on")
    .withFallback(ConfigFactory.defaultApplication())
  lazy val testKit2 = ActorTestKit(conf)
  val serverSystem: ActorSystem[_] = testKit2.system
  val bound = new PrimeNumberServer(serverSystem).run()
  bound.futureValue

  override def afterAll(): Unit = {
    testKit1.shutdownTestKit()
    testKit2.shutdownTestKit()
  }

  "ProxyRoutes Fail and " should {
    "give an error message on wrong input" in {
      val request = HttpRequest(uri = "/prime/abcd")
      request ~> ProxyRoutes.proxyRoutes ~> check {
        status should ===(StatusCodes.BadRequest)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should === ( ProxyRoutes.BadRequestMsg)
      }
    }
    "give an error message when 0 is passed" in {
      val request = HttpRequest(uri = "/prime/0")
      request ~> ProxyRoutes.proxyRoutes ~> check {
        status should ===(StatusCodes.BadRequest)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should === (ProxyRoutes.BadRequestMsg)
      }
    }
    "give an error message when a number greater than 4 digits is passed" in {
      val request = HttpRequest(uri = "/prime/10000")
      request ~> ProxyRoutes.proxyRoutes ~> check {
        status should ===(StatusCodes.BadRequest)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should === (ProxyRoutes.BadRequestMsg)
      }
    }
    "give an error message when a negative number is passed" in {
      val request = HttpRequest(uri = "/prime/-10")
      request ~> ProxyRoutes.proxyRoutes ~> check {
        status should ===(StatusCodes.BadRequest)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should === (ProxyRoutes.BadRequestMsg)
      }
    }
    "give an error message when a decimal number is passed" in {
      val request = HttpRequest(uri = "/prime/10.10")
      request ~> ProxyRoutes.proxyRoutes ~> check {
        status should ===(StatusCodes.BadRequest)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should === (ProxyRoutes.BadRequestMsg)
      }
    }
  }

  "ProxyRoutes Pass and " should {
    "give a list of prime numbers when a passed number is not prime" in {
      val request = HttpRequest(uri = "/prime/8")
      request ~> ProxyRoutes.proxyRoutes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should ===("2, 3, 5, 7")
      }
    }
    "give a list of prime numbers including itself when a passed number is prime" in {
      val request = HttpRequest(uri = "/prime/7")
      request ~> ProxyRoutes.proxyRoutes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should ===("2, 3, 5, 7")
      }
    }
    "give a 2 as an only prime number when 2 is passed" in {
      val request = HttpRequest(uri = "/prime/2")
      request ~> ProxyRoutes.proxyRoutes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should ===("2")
      }
    }
    "say no primes when 1 is passed" in {
      val request = HttpRequest(uri = "/prime/1")
      request ~> ProxyRoutes.proxyRoutes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should ===("no primes")
      }
    }
  }
}

