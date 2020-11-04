package rest.queue.router

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import com.typesafe.config.ConfigFactory

import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

object QueueRouterApp extends App {
  val config = ConfigFactory.load("app.conf")
  implicit val system = ActorSystem.create("queue-router", config)
  implicit val materializer = ActorMaterializer()
  val conf = config.as[QueueConnectorConf]("queue")
  val router = new QueueRouter(conf)
  import router._
  val server = Http().bindAndHandle(routes, "localhost", 0)
}