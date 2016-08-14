package rest.queue.router

import java.nio.charset.StandardCharsets

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import spray.json.DefaultJsonProtocol

case class QueueRequest(body: String)

case class QueueResponse(body: String)

class QueueRouter(queue: QueueConnector) extends DefaultJsonProtocol with SprayJsonSupport {
  import akka.http.scaladsl.server.Directives._
  import akka.http.scaladsl.marshalling._
  import akka.http.scaladsl.model.HttpResponse
  implicit val queueRequestFormat = jsonFormat1(QueueRequest)
  implicit val queueResponseFormat = jsonFormat1(QueueResponse)

  val queueRequestRoute = path("push") {
    post {
      entity(as[QueueRequest]) { request =>
        val isComfirmed = queue.push(request.body)
        if (isComfirmed) complete(HttpResponse(OK)) else complete(HttpResponse(InternalServerError))
      }
    }
  }

  val queueResponsetRoute = path("pull") {
    get {
      val option = queue.pull
      if (option.nonEmpty) {
        val body = new String(option.get.getBody, StandardCharsets.UTF_8)
        val response = QueueResponse(body)
        complete(ToResponseMarshallable[QueueResponse](response))
      } else {
        println("empty message")
        complete(HttpResponse(InternalServerError))
      }
    }
  }

  val routes = queueRequestRoute ~ queueResponsetRoute
}