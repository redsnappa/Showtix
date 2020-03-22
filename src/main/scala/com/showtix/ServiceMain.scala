package com.showtix

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.showtix.routes.RestApi
import com.typesafe.config.ConfigFactory

import scala.language.postfixOps
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

object ServiceMain extends App {
  // this configs are in the application.conf file
  val config = ConfigFactory.load()
  val host = config.getString("http.host") // Gets the host and a port from the configuration
  val port = config.getInt("http.port")

  implicit val system: ActorSystem = ActorSystem()  // ActorMaterializer requires an implicit ActorSystem
  implicit val ec: ExecutionContextExecutor = system.dispatcher  // bindingFuture.map requires an implicit ExecutionContext
  implicit val materializer: ActorMaterializer = ActorMaterializer()  // bindAndHandle requires an implicit materializer

  val api = new RestApi(system, Timeout(3 seconds)).routes

  val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(api, host, port) // starts the HTTP server
  val log = Logging(system.eventStream, "show-tix")
  try {
    //    Here we start the HTTP server and log the info
    bindingFuture.map { serverBinding ⇒
      log.info(s"RestApi bound to ${serverBinding.localAddress}")
    }
  }catch {
    //    If the HTTP server fails to start, we throw an Exception and log the error and close the system
    case ex: Exception ⇒
      log.error(ex, "Failed to bind to {}:{}!", host, port)
      //      System shutdown
      system.terminate()
  }

}
