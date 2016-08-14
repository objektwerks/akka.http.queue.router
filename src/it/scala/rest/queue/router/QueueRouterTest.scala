package rest.queue.router

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class QueueRouterTest extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterAll {
  val config = ConfigFactory.load("test.conf")
  val actorRefFactory = ActorSystem.create("queue-router", config)
  val queueConf = ConfigFactory.load("test.conf").as[QueueConnectorConf]("queue")
  val queue = new QueueConnector(queueConf)
  val router = new QueueRouter(queue)
  import router._
  val server = Http().bindAndHandle(routes, "localhost", 0)

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