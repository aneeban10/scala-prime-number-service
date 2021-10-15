package PrimeNumber

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PrimeNumberSpec extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  val conf = ConfigFactory.parseString("akka.http.server.preview.enable-http2 = on")
    .withFallback(ConfigFactory.defaultApplication())
  lazy val testKit = ActorTestKit(conf)
  val serverSystem: ActorSystem[_] = testKit.system
  val service = new PrimeNumberServiceImpl(serverSystem)

  override def afterAll(): Unit =
    testKit.shutdownTestKit()

  "Prime Number Service  " should {
    "say no primes when -1 is passed" in {
      service.getPrimes(PrimeNumberRequest(-1)).futureValue.primeList should === ("no primes")
    }
    "say no primes when 0 is passed" in {
      service.getPrimes(PrimeNumberRequest(0)).futureValue.primeList should === ("no primes")
    }
    "say no primes when 1 is passed" in {
      service.getPrimes(PrimeNumberRequest(1)).futureValue.primeList should === ("no primes")
    }
    "give 2 as an only prime number when 2 is passed" in {
      service.getPrimes(PrimeNumberRequest(2)).futureValue.primeList should === ("2")
    }
    "give a list of prime numbers including itself when a passed number is prime" in {
      service.getPrimes(PrimeNumberRequest(7)).futureValue.primeList should === ("2, 3, 5, 7")
    }
    "give a list of prime numbers when a passed number is not prime" in {
      service.getPrimes(PrimeNumberRequest(8)).futureValue.primeList should === ("2, 3, 5, 7")
    }
  }
}

