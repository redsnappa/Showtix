package com.showtix.exception

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.ExceptionHandler
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import Directives._

trait ShowTixExceptionHandler {

  implicit def showTixExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case _ =>
        extractUri { uri =>
          println(s"Request to $uri could not be handled normally")
          complete(HttpResponse(InternalServerError, entity = "Bad numbers, bad result!!!"))
        }
    }

}
