package rest.queue.router

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class QueueRouterTest extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterAll with QueueRouter {
  val config = ConfigFactory.load("test.conf")
  val actorRefFactory = ActorSystem.create("now", config)
  val server = Http().bindAndHandle(routes, "localhost", 0)

  override protected def afterAll(): Unit = {
    server.flatMap(_.unbind()).onComplete(_ ⇒ system.terminate())
  }

  "QueueRouter" should {
    "handle post and get." in {
      Post("/request", QueueRequest("body")) ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
      Get("/response") ~> routes ~> check {
        responseAs[QueueResponse].body.nonEmpty shouldBe true
      }
    }
  }
}