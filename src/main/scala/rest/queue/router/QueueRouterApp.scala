package rest.queue.router

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import com.typesafe.config.ConfigFactory

object QueueRouterApp extends App {
  val config = ConfigFactory.load("app.conf")
  implicit val system = ActorSystem.create("queue-router", config)
  implicit val materializer = ActorMaterializer()
  val requestQueue = new QueueConnector(config.as[QueueConnectorConf]("request-queue"))
  val responseQueue = new QueueConnector(config.as[QueueConnectorConf]("response-queue"))
  val router = new QueueRouter(requestQueue, responseQueue)
  import router._
  val server = Http().bindAndHandle(routes, "localhost", 0)
}