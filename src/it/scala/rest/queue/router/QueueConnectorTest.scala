package rest.queue.router

import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicInteger

import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Envelope
import com.typesafe.config.ConfigFactory

import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

import org.scalatest.funsuite.AnyFunSuite
import org.slf4j.LoggerFactory

class TestQueueConsumer(connector: QueueConnector) extends QueueConsumer() {
  val log = LoggerFactory.getLogger(this.getClass)

  override def handleDelivery(consumerTag: String,
                              envelope: Envelope,
                              properties: BasicProperties,
                              body: Array[Byte]): Unit = {
    val message = new String(body, StandardCharsets.UTF_8)
    log.debug(s"test queue consumer handleDeliver: $message")
    connector.ackAllMessages(envelope.getDeliveryTag)
  }
}

class QueueConnectorTest extends AnyFunSuite {
  val log = LoggerFactory.getLogger(this.getClass)

  test("push pull") {
    val queueConf = ConfigFactory.load("test.queue.conf").as[QueueConnectorConf]("queue")
    val queue = new QueueConnector(queueConf)
    clearQueue(queue)
    log.debug("push pull test: test rabbitmq queue cleared!")
    pushMessagesToRequestQueue(queue, 10)
    pullMessagesFromRequestQueue(queue, 10)
    queue.close()
  }

  test("consume") {
    val queueConf = ConfigFactory.load("test.queue.conf").as[QueueConnectorConf]("queue")
    val queue = new QueueConnector(queueConf)
    clearQueue(queue)
    log.debug("consume test: test rabbitmq queue cleared!")
    pushMessagesToRequestQueue(queue, 10)
    val consumer = new TestQueueConsumer(queue)
    consumeMessagesFromRequestQueue(queue, 10, consumer)
    queue.close()
  }

  private def pushMessagesToRequestQueue(queue: QueueConnector, number: Int): Unit = {
    val counter = new AtomicInteger()
    val confirmed = new AtomicInteger()
    for (_ <- 1 to number) {
      val message = s"test.request: ${counter.incrementAndGet}"
      val isComfirmed = queue.push(message)
      if (isComfirmed) confirmed.incrementAndGet
    }
    assert(confirmed.intValue == number)
    ()
  }

  private def pullMessagesFromRequestQueue(queue: QueueConnector, number: Int): Unit = {
    val pulled = new AtomicInteger()
    for (_ <- 1 to number) {
      if(queue.pull.nonEmpty) pulled.incrementAndGet
    }
    assert(pulled.intValue == number)
    ()
  }

  private def consumeMessagesFromRequestQueue(queue: QueueConnector, number: Int, consumer: QueueConsumer): Unit = {
    val consumed = queue.consume(number, consumer)
    log.debug(s"consumed: $consumed")
    assert(queue.pull.isEmpty)
    ()
  }

  private def clearQueue(queue: QueueConnector): Unit = {
    var queueIsEmpty = false
    while (!queueIsEmpty) {
      queueIsEmpty = queue.pull.isEmpty
    }
  }
}