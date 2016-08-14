package rest.queue.router

import java.nio.charset.StandardCharsets

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import org.slf4j.LoggerFactory
import spray.json.DefaultJsonProtocol

case class QueueRequest(body: String)

case class QueueResponse(body: String)

class QueueRouter(requestQueue: QueueConnector, responseQueue: QueueConnector) extends DefaultJsonProtocol with SprayJsonSupport {
  import akka.http.scaladsl.marshalling._
  import akka.http.scaladsl.model.HttpResponse
  import akka.http.scaladsl.server.Directives._
  val log = LoggerFactory.getLogger(this.getClass)
  implicit val queueRequestFormat = jsonFormat1(QueueRequest)
  implicit val queueResponseFormat = jsonFormat1(QueueResponse)

  val queuePushRoute = path("push") {
    post {
      entity(as[QueueRequest]) { request =>
        val isComfirmed = requestQueue.push(request.body)
        log.debug(s"queue request route: is push confirmed = $isComfirmed")
        if (isComfirmed) complete(HttpResponse(OK)) else complete(HttpResponse(InternalServerError))
      }
    }
  }

  val queuePullRoute = path("pull") {
    get {
      val optionalMessage = responseQueue.pull
      optionalMessage match {
        case Some(body) =>
          val deliveryTag = optionalMessage.get.getEnvelope.getDeliveryTag
          responseQueue.ack(deliveryTag)
          val body = new String(optionalMessage.get.getBody, StandardCharsets.UTF_8)
          log.debug(s"queue response route: response is: $body")
          val response = QueueResponse(body)
          complete(ToResponseMarshallable[QueueResponse](response))
        case None =>
          log.debug(s"queue response route: response is empty message: $optionalMessage")
          complete(HttpResponse(InternalServerError))
      }
    }
  }

  val routes = queuePushRoute ~ queuePullRoute
}