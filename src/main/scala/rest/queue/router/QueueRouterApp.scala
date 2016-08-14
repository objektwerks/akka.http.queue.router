package rest.queue.router

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

object QueueRouterApp extends App with QueueRouter {
  val config = ConfigFactory.load("app.conf")
  implicit val system = ActorSystem.create("queue-router", config)
  implicit val materializer = ActorMaterializer()
  val server = Http().bindAndHandle(routes, "localhost", 0)
}