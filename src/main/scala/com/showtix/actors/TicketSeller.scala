package com.showtix.actors

import akka.actor.{Actor, PoisonPill}
import com.showtix.messages.Coachella
import com.showtix.messages.TicketSeller.{Add, Buy, Cancel, GetEvent, Ticket, Tickets}

class TicketSeller(private val event: String) extends Actor {


  override def receive: Receive = ticketHandler()

  def ticketHandler(tickets: Vector[Ticket] = Vector.empty[Ticket] ): Receive = {
    case GetEvent => sender() ! Coachella.Event(event, tickets.size)
    case Add(newTickets: Vector[Ticket]) =>
       context.become(ticketHandler(tickets ++ newTickets))
    case Buy(amount: Int) =>
      if(tickets.size < amount)
        sender() ! Tickets(event)
      else
        sender() ! Tickets(event, tickets.take(amount))
        context.become(ticketHandler(tickets.drop(amount)))
    case Cancel =>
      sender() ! Coachella.Event(event, tickets.size)
      context.self ! PoisonPill
  }
}

