package com.showtix.rejection

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ RejectionHandler}

trait ShowTixRejectionHandler {

  implicit def showTixRejectionHandler = RejectionHandler.newBuilder()
    .handleNotFound {
      extractUnmatchedPath { p =>
        println(s"fuck: $p")
        complete((NotFound, s"The path you requested [${p}] does not exist."))
      }
    }
    .result()

}
