package rest.queue.router

import java.time.LocalTime

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import spray.json.DefaultJsonProtocol

case class Now(time: String = LocalTime.now.toString)

trait QueueRouter extends DefaultJsonProtocol with SprayJsonSupport {
  import akka.http.scaladsl.server.Directives._
  import akka.http.scaladsl.marshalling._
  import akka.http.scaladsl.model.HttpResponse
  implicit val nowFormat = jsonFormat1(Now)

  val routes = path("now") {
    get {
      complete(ToResponseMarshallable[Now](Now()))
    } ~
      post {
        entity(as[Now]) { now =>
          if (now.time.isEmpty) complete(HttpResponse(NotFound)) else complete(HttpResponse(OK))
        }
      }
  }
}