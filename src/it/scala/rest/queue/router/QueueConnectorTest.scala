package rest.queue.router

import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicInteger

import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Envelope
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class TestQueueConsumer(connector: QueueConnector) extends QueueConsumer(connector) {
  override def handleDelivery(consumerTag: String,
                              envelope: Envelope,
                              properties: BasicProperties,
                              body: Array[Byte]): Unit = {
    val message = new String(body, StandardCharsets.UTF_8)
    println(s"handleDeliver: $message")
    connector.ackAllMessages(envelope.getDeliveryTag)
  }
}

class RestQueueRouterTest extends FunSuite with BeforeAndAfterAll {
  val queueConf = ConfigFactory.load("test.conf").as[QueueConnectorConf]("queue")
  val queue = new QueueConnector(queueConf)

  override protected def beforeAll(): Unit = {
    var queueIsEmpty = false
    while (!queueIsEmpty) {
      queueIsEmpty = queue.pull.isEmpty
    }
  }

  test("pull push") {
    pushMessagesToRequestQueue(10)
    pullMessagesFromRequestQueue(10)
  }

  test("consume") {
    pushMessagesToRequestQueue(10)
    consumeMessagesFromRequestQueue(10)
  }

  private def pushMessagesToRequestQueue(number: Int): Unit = {
    val counter = new AtomicInteger()
    val confirmed = new AtomicInteger()
    for (i <- 1 to number) {
      val message = s"test.request: ${counter.incrementAndGet}"
      val isComfirmed = queue.push(message)
      if (isComfirmed) confirmed.incrementAndGet
    }
    queue.close()
    assert(confirmed.intValue == number)
  }

  private def pullMessagesFromRequestQueue(number: Int): Unit = {
    val pulled = new AtomicInteger()
    for (i <- 1 to number) {
      if(queue.pull.nonEmpty) pulled.incrementAndGet
    }
    queue.close()
    assert(pulled.intValue == number)
  }

  private def consumeMessagesFromRequestQueue(number: Int): Unit = {
    val consumer = new TestQueueConsumer(queue)
    queue.consume(number, consumer)
    Thread.sleep(1000)
    assert(queue.pull.isEmpty)
    queue.close()
  }
}