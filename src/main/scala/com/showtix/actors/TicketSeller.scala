package com.showtix.actors

import akka.actor.{Actor, Props}
import com.showtix.messages.Coachella
import com.showtix.messages.TicketSeller.{Add, GetEvent, Ticket}

class TicketSeller(event: String) extends Actor {

  var tickets = Vector.empty[Ticket]

  override def receive: Receive = {
    case GetEvent => sender() ! Coachella.Event(event, tickets.size)
    case Add(newTickets: Vector[Ticket]) =>
       tickets = tickets ++ newTickets
  }
}

