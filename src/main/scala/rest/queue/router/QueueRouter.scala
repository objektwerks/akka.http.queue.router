package rest.queue.router

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import spray.json.DefaultJsonProtocol

case class QueueRequest(body: String)

case class QueueResponse(body: String)

trait QueueRouter extends DefaultJsonProtocol with SprayJsonSupport {
  import akka.http.scaladsl.server.Directives._
  import akka.http.scaladsl.marshalling._
  import akka.http.scaladsl.model.HttpResponse
  implicit val queueRequestFormat = jsonFormat1(QueueRequest)
  implicit val queueResponseFormat = jsonFormat1(QueueResponse)

  val queueRequestRoute = path("request") {
    post {
      entity(as[QueueRequest]) { request =>
        complete(HttpResponse(OK))
      }
    }
  }

  val queueResponsetRoute = path("response") {
    get {
      complete(ToResponseMarshallable[QueueResponse](QueueResponse("response")))
    }
  }

  val routes = queueRequestRoute ~ queueResponsetRoute
}