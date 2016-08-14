package rest.queue.router

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import org.slf4j.LoggerFactory

class QueueRouterTest extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterAll {
  val log = LoggerFactory.getLogger(this.getClass)
  val actorRefFactory = ActorSystem.create("queue-router", ConfigFactory.load("test.akka.conf"))
  val requestQueue = new QueueConnector(ConfigFactory.load("test.queue.conf").as[QueueConnectorConf]("queue"))
  val responseQueue = new QueueConnector(ConfigFactory.load("test.queue.conf").as[QueueConnectorConf]("queue"))
  val router = new QueueRouter(requestQueue, responseQueue)
  import router._
  val server = Http().bindAndHandle(routes, "localhost", 0)

  override protected def beforeAll(): Unit = {
    val queue = new QueueConnector(ConfigFactory.load("test.queue.conf").as[QueueConnectorConf]("queue"))
    var queueIsEmpty = false
    while (!queueIsEmpty) {
      queueIsEmpty = queue.pull.isEmpty
    }
    log.debug("before all tests: test rabbitmq queue cleared!")
    queue.close()
  }

  override protected def afterAll(): Unit = {
    server.flatMap(_.unbind()).onComplete(_ ⇒ system.terminate())
  }

  "push" should {
    "push message to queue." in {
      Post("/push", QueueRequest("body")) ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  "pull" should {
    "pull message from queue" in {
      Get("/pull") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[QueueResponse].body.nonEmpty shouldBe true
      }
    }
  }
}