package rest.queue.router

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

import scala.io.Source

object QueueLoader {
  def load(source: String, total: Int): Unit = {
    val message = Source.fromInputStream(getClass.getResourceAsStream(source)).mkString
    val queueConf = ConfigFactory.load("test.conf").as[QueueConnectorConf]("queue")
    val queue = new QueueConnector(queueConf)
    for (i <- 1 to total) {
      queue.push(message)
    }
    queue.close()
  }
}