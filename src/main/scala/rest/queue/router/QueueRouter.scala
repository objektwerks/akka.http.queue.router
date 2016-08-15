package rest.queue.router

import java.nio.charset.StandardCharsets

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Envelope
import org.slf4j.LoggerFactory
import spray.json.DefaultJsonProtocol

import scala.collection.mutable.ArrayBuffer

case class PushToQueue(id: String, body: String)

case class PullFromQueue(id: String)
case class PulledFromQueue(id: String, body: String)

case class ConsumeFromQueue(id: String)
case class ConsumedFromQueue(id: String, responses: Array[String])

class QueueRouteConsumer(connector: QueueConnector) extends QueueConsumer(connector) {
  val log = LoggerFactory.getLogger(this.getClass)
  val responses = new ArrayBuffer[String]()

  override def handleDelivery(consumerTag: String,
                              envelope: Envelope,
                              properties: BasicProperties,
                              body: Array[Byte]): Unit = {
    val message = new String(body, StandardCharsets.UTF_8)
    responses += message
    log.debug(s"consume for queue consumer handleDeliver: $message")
    connector.ackAllMessages(envelope.getDeliveryTag)
  }
}

class QueueRouter(conf: QueueConnectorConf) extends DefaultJsonProtocol with SprayJsonSupport {
  import akka.http.scaladsl.marshalling._
  import akka.http.scaladsl.model.HttpResponse
  import akka.http.scaladsl.server.Directives._
  val log = LoggerFactory.getLogger(this.getClass)
  implicit val pushToQueueFormat = jsonFormat2(PushToQueue)
  implicit val pullFromQueueFormat = jsonFormat1(PullFromQueue)
  implicit val pulledFromQueueFormat = jsonFormat2(PulledFromQueue)
  implicit val consumeFromQueueFormat = jsonFormat1(ConsumeFromQueue)
  implicit val consumedFromQueueFormat = jsonFormat2(ConsumedFromQueue)

  val pushToQueueRoute = path("push") {
    post {
      entity(as[PushToQueue]) { request =>
        val id = request.id
        log.debug(s"push to queue request id: $id")
        val body = request.body
        val queue = new QueueConnector(QueueConnectorConf.copy(id, conf))
        val isComfirmed = queue.push(body)
        log.debug(s"push to queue route: is push confirmed = $isComfirmed")
        queue.close()
        if (isComfirmed) complete(HttpResponse(OK)) else complete(HttpResponse(InternalServerError))
      }
    }
  }

  val pullFromQueueRoute = path("pull") {
    post {
      entity(as[PullFromQueue]) { request =>
        val id = request.id
        val queue = new QueueConnector(QueueConnectorConf.copy(id, conf))
        log.debug(s"pull from queue request id: $id")
        val optionalMessage = queue.pull
        optionalMessage match {
          case Some(message) =>
            val body = new String(message.getBody, StandardCharsets.UTF_8)
            log.debug(s"pull from queue route: response is: $body")
            val deliveryTag = optionalMessage.get.getEnvelope.getDeliveryTag
            queue.ack(deliveryTag)
            val response = PulledFromQueue(id, body)
            queue.close()
            complete(ToResponseMarshallable[PulledFromQueue](response))
          case None =>
            log.debug(s"pull from queue route: response is empty message: $optionalMessage")
            queue.close()
            complete(HttpResponse(InternalServerError))
        }
      }
    }
  }

  val consumeFromQueueRoute = path("consume") {
    post {
      entity(as[ConsumeFromQueue]) { request =>
        val id = request.id
        log.debug(s"consume from queue request id: $id")
        val queue = new QueueConnector(QueueConnectorConf.copy(id, conf))
        val consumer = new QueueRouteConsumer(queue)
        val consumed = queue.consume(prefetchCount = 1, consumer)
        log.debug(s"consume from queue route: $consumed")
        val response = ConsumedFromQueue(id, consumer.responses.toArray)
        queue.close()
        complete(ToResponseMarshallable[ConsumedFromQueue](response))
      }
    }
  }

  val routes = pushToQueueRoute ~ pullFromQueueRoute ~ consumeFromQueueRoute
}