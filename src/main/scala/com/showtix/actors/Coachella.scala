package com.showtix.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.showtix.messages.Coachella.{CancelEvent, CreateEvent, Event, EventCreated, EventExists, EventNotExists, Events, GetEvent, GetEvents, GetTickets}
import com.showtix.messages.TicketSeller
import com.showtix.messages.TicketSeller.{Add, Buy, Ticket}
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Coachella(implicit timeout: Timeout = Timeout(1 seconds)) extends Actor with ActorLogging {


  val childSellerNameMap = scala.collection.mutable.Map[String, ActorRef]()

  def createTicketSeller(name: String): ActorRef = {
    val childSeller = context.actorOf(TicketSeller.props(name))
    childSellerNameMap.addOne(name -> childSeller)
    childSeller

  }
  override def receive: Receive = {
    case CreateEvent(name, ticketAllowance) =>
      if(childSellerNameMap.contains(name))
        sender() ! EventExists
      else {
        val seller = createTicketSeller(name)
        val tickets: Vector[Ticket] = (1 to ticketAllowance).map({ i => Ticket(i)}).toVector
        seller ! Add(tickets)
        sender() ! EventCreated(Event(name, ticketAllowance))
      }
    case GetTickets(eventName, numberOfTickets) =>
      def noEventExists():Unit = {
        sender() ! TicketSeller.Tickets(eventName)
      }
      def buy(seller: ActorRef): Unit = {
        seller.forward(Buy(numberOfTickets))
      }
      childSellerNameMap.get(eventName).fold(noEventExists())(buy)
    case GetEvent(name) =>
      def getEventFromSeller(seller: ActorRef): Unit = seller.forward(TicketSeller.GetEvent)
      childSellerNameMap.get(name).fold({sender() ! None})(getEventFromSeller)
    case GetEvents =>
      val fEvents: Iterable[Future[Option[Event]] ]= childSellerNameMap.values.map { childRef =>
        (childRef ? TicketSeller.GetEvent).mapTo[Option[Event]]
      }
      val events: Future[Events] = Future.sequence(fEvents).map { events:Iterable[Option[Event]] => Events(Vector.from(events.flatten)) }
      pipe(events) to sender()

    case CancelEvent(name) =>

      childSellerNameMap.get(name).fold({sender() ! None})( child => {
        child forward(TicketSeller.Cancel)
        childSellerNameMap.remove(name)
      })

  }



}
