package com.showtix.messages

// message containing the initial number of tickets for the event
case class EventDescription(tickets: Int) {
  require(tickets > 0)
}

// message containing the required number of tickets
case class TicketRequests(tickets: Int) {
  require(tickets > 0)
}

// message containing an error
case class Error(message: String)

