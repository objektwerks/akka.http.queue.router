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
  val queueConf = config.as[QueueConnectorConf]("queue")
  val queue = new QueueConnector(queueConf)
  val router = new QueueRouter(queue)
  import router._
  val server = Http().bindAndHandle(routes, "localhost", 0)
}