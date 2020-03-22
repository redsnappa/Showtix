package com.showtix.routes

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{MessageEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import com.showtix.messages.Coachella.{Event}
import com.showtix.messages.EventDescription
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

    "create an event" in {


      val eventName = "tinthepark"
      val eventDescription = Marshal(EventDescription(20)).to[MessageEntity].futureValue // futureValue is from ScalaFutures

      Post(s"/show-tix/v1/events/$eventName").withEntity(eventDescription) ~>  routes ~>  check {

        status shouldEqual StatusCodes.Created
        entityAs[Event] shouldEqual Event(eventName, 20)
      }
    }

  }
}
