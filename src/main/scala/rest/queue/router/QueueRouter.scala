package rest.queue.router

import java.nio.charset.StandardCharsets

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import spray.json.DefaultJsonProtocol

case class QueueRequest(body: String)

case class QueueResponse(body: String)

class QueueRouter(requestQueue: QueueConnector, responseQueue: QueueConnector) extends DefaultJsonProtocol with SprayJsonSupport {
  import akka.http.scaladsl.server.Directives._
  import akka.http.scaladsl.marshalling._
  import akka.http.scaladsl.model.HttpResponse
  implicit val queueRequestFormat = jsonFormat1(QueueRequest)
  implicit val queueResponseFormat = jsonFormat1(QueueResponse)

  val queueRequestRoute = path("push") {
    post {
      entity(as[QueueRequest]) { request =>
        val isComfirmed = requestQueue.push(request.body)
        println(s"confirmed push: $isComfirmed")
        if (isComfirmed) complete(HttpResponse(OK)) else complete(HttpResponse(InternalServerError))
      }
    }
  }

  val queueResponsetRoute = path("pull") {
    get {
      val option = responseQueue.pull
      if (option.nonEmpty) {
        val body = new String(option.get.getBody, StandardCharsets.UTF_8)
        println(s"response is: $body")
        val response = QueueResponse(body)
        complete(ToResponseMarshallable[QueueResponse](response))
      } else {
        println(s"response is empty message: $option")
        complete(HttpResponse(InternalServerError))
      }
    }
  }

  val routes = queueRequestRoute ~ queueResponsetRoute
}