package rest.queue.router

import akka.actor.ActorSystem
import akka.http.scaladsl.Http

import com.typesafe.config.ConfigFactory

import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

object QueueRouterApp extends App {
  val config = ConfigFactory.load("app.conf")
  implicit val system = ActorSystem.create("queue-router", config)
  val conf = config.as[QueueConnectorConf]("queue")
  val router = new QueueRouter(conf)
  val server = Http()
    .newServerAt("localhost", 0)
    .bindFlow(router.routes)
}