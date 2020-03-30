package com.showtix.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.showtix.messages.Coachella
import com.showtix.messages.Coachella.{CancelEvent, CreateEvent, Event, EventCreated, EventExists, EventNotExists, Events, GetEvent, GetEvents, GetTickets}
import com.showtix.messages.TicketSeller.{Ticket, Tickets}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class CoachellaTest extends TestKit(ActorSystem("CoachellaTestActorSystem"))
  with AnyWordSpecLike
  with ImplicitSender
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "a coachella actor" should {

    val eventName = "T in the Park"
    "create an event given an event name and ticket allowance" in {

      val coachella = system.actorOf(Coachella.props())
      val ticketsAllocated = 5

      coachella ! CreateEvent(eventName, ticketsAllocated)
      expectMsg(EventCreated(Event(eventName, ticketsAllocated)))
    }

    "not create multiple events with the same name" in {

      val coachella = system.actorOf(Coachella.props())
      val ticketsAllocated = 5

      coachella ! CreateEvent(eventName, ticketsAllocated)
      expectMsg(EventCreated(Event(eventName, ticketsAllocated)))

      coachella ! CreateEvent(eventName, ticketsAllocated)
      expectMsg(EventExists)

    }

    "sell tickets for it's event" in {

      val coachella = system.actorOf(Coachella.props())
      val ticketsAllocated = 5

      coachella ! CreateEvent(eventName, ticketsAllocated)
      expectMsg(EventCreated(Event(eventName, ticketsAllocated)))

      coachella ! GetTickets(eventName, 2)

      expectMsg(Tickets(eventName, Vector(Ticket(1), Ticket(2))))

    }

    "return no tickets for event which doesn't exist" in {

      val coachella = system.actorOf(Coachella.props())

      coachella ! GetTickets(eventName, 2)

      expectMsg(Tickets(eventName, Vector.empty))

    }


    "return EventNotExists if event does not exist" in {

      val coachella = system.actorOf(Coachella.props())

      coachella ! GetEvent(eventName)

      expectMsg(None)

    }

    "return the event if it exists" in {

      val coachella = system.actorOf(Coachella.props())
      val ticketsAllocated = 5

      coachella ! CreateEvent(eventName, ticketsAllocated)
      expectMsg(EventCreated(Event(eventName, ticketsAllocated)))

      coachella ! GetEvent(eventName)

      expectMsg(Some(Event(eventName, ticketsAllocated)))

    }


    "return a list of all events" in {
      val coachella = system.actorOf(Coachella.props())
      val glasto = "Glastonbury 2020"

      val ticketsAllocated = 5

      coachella ! CreateEvent(eventName, ticketsAllocated)
      coachella ! CreateEvent(glasto, ticketsAllocated)

      expectMsg(EventCreated(Event(eventName, ticketsAllocated)))
      expectMsg(EventCreated(Event(glasto, ticketsAllocated)))

      coachella ! GetEvents

      expectMsg(Events(Vector(Event(eventName, ticketsAllocated), Event(glasto, ticketsAllocated))))
    }

    "cancel an existing event" in {
      val coachella = system.actorOf(Coachella.props())
      val glasto = "Glastonbury 2020"

      val ticketsAllocated = 5

      coachella ! CreateEvent(eventName, ticketsAllocated)
      coachella ! CreateEvent(glasto, ticketsAllocated)

      expectMsg(EventCreated(Event(eventName, ticketsAllocated)))
      expectMsg(EventCreated(Event(glasto, ticketsAllocated)))

      coachella ! CancelEvent(glasto)
      expectMsg(Some(Event(glasto, ticketsAllocated)))

      coachella ! GetEvents

      expectMsg(Events(Vector(Event(eventName, ticketsAllocated))))

    }
  }

}
