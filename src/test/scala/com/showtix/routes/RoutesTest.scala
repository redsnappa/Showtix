package com.showtix.routes

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{MessageEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import com.showtix.messages.Coachella.{Event, Events}
import com.showtix.messages.{EventDescription, TicketRequests, TicketSeller}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._
import scala.language.postfixOps

class RoutesTest extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  "Showtix" should {

    val restApi = new RestApi(system, Timeout(2 seconds))
    val routes = restApi.routes

    import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
    import com.showtix.json.JsonFormats._

    val eventName = "tinthepark"

    "create an event" in {

      val eventDescription = Marshal(EventDescription(20)).to[MessageEntity].futureValue // futureValue is from ScalaFutures

      Post(s"/show-tix/v1/events/$eventName").withEntity(eventDescription) ~> Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.Created
        entityAs[Event] shouldEqual Event(eventName, 20)
      }
    }

    "get all events" in {

      Get(s"/show-tix/v1/events") ~> Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.OK
        entityAs[Events] shouldEqual Events(Vector(Event(eventName, 20)))

      }

      Get(s"/show-tix/v1/events/") ~> Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.OK
        entityAs[Events] shouldEqual Events(Vector(Event(eventName, 20)))

      }
    }

    "get a single event" in {

      Get(s"/show-tix/v1/events/$eventName/") ~> Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.OK
        entityAs[Event] shouldEqual Event(eventName, 20)
      }
    }

    "purchase tickets" in {

      val numberOfTickets = 10
      val ticketRequest = Marshal(TicketRequests(numberOfTickets)).to[MessageEntity].futureValue // futureValue is from ScalaFutures

      Post(s"/show-tix/v1/events/$eventName/tickets").withEntity(ticketRequest) ~> Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.Created
        entityAs[TicketSeller.Tickets] shouldBe a[TicketSeller.Tickets]
      }
    }

    "delete an event" in {

      Delete(s"/show-tix/v1/events/$eventName") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        entityAs[Event] shouldBe a[Event]
      }

    }


    "delete an event which isnt' there " in {

      Delete(s"/show-tix/v1/events/$eventName") ~> routes ~> check {
        status shouldEqual StatusCodes.NotFound

      }

    }


  }
}
