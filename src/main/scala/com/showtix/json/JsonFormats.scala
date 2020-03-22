package com.showtix.json

import com.showtix.messages.Coachella.{Event, EventCreated, EventResponse, Events}
import com.showtix.messages.{Error, EventDescription, TicketRequests, TicketSeller}
import spray.json.RootJsonFormat

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import spray.json.DefaultJsonProtocol._

  implicit val eventDescriptionFormat: RootJsonFormat[EventDescription] =  jsonFormat1(EventDescription)
  implicit val ticketRequests: RootJsonFormat[TicketRequests] = jsonFormat1(TicketRequests)
  implicit val errorFormat: RootJsonFormat[Error] = jsonFormat1(Error)
  implicit val eventFormat: RootJsonFormat[Event] = jsonFormat2(Event)
  implicit val eventsFormat: RootJsonFormat[Events] = jsonFormat1(Events)
  implicit val ticketFormat: RootJsonFormat[TicketSeller.Ticket] = jsonFormat1(TicketSeller.Ticket)
  implicit val ticketsFormat: RootJsonFormat[TicketSeller.Tickets] = jsonFormat2(TicketSeller.Tickets)

}
