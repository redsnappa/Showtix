package com.showtix.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.showtix.messages.{Coachella, TicketSeller}
import com.showtix.messages.TicketSeller._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class TicketSellerTest extends TestKit(ActorSystem("TicketSellerTestActorSystem"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }


  "A ticket seller" should {

    val eventName = "T in the Park"

    "be instantiated with no tickets and have an event name" in {
      val ticketSeller = system.actorOf(TicketSeller.props(eventName))

      ticketSeller ! GetEvent

      expectMsg(Some(Coachella.Event(eventName, 0)))
    }

    "add new tickets for it's event" in {
      val ticketSeller = system.actorOf(TicketSeller.props(eventName))
      val newTickets = Vector(Ticket(1), Ticket(2), Ticket(3), Ticket(4))

      ticketSeller ! Add(newTickets)
      ticketSeller ! GetEvent

      expectMsg(Some(Coachella.Event(eventName, newTickets.size)))
    }

    "sell tickets if they are available" in {
      val ticketSeller = system.actorOf(TicketSeller.props(eventName))
      val newTickets = Vector(Ticket(1), Ticket(2), Ticket(3), Ticket(4))
      ticketSeller ! Add(newTickets)

      ticketSeller ! Buy(3)

      expectMsg(Tickets(eventName, Vector(Ticket(1), Ticket(2), Ticket(3))))

      ticketSeller ! GetEvent

      expectMsg(Some(Coachella.Event(eventName, 1)))
    }

    "return no tickets if there are none to sell" in {
      val ticketSeller = system.actorOf(TicketSeller.props(eventName))


      ticketSeller ! Buy(3)

      expectMsg(Tickets(eventName, Vector.empty[Ticket]))
    }

    "return no tickets if there there are tickets available but less that the requested amount" in {
      val ticketSeller = system.actorOf(TicketSeller.props(eventName))

      ticketSeller ! Add(Vector(Ticket(1), Ticket(2)))
      ticketSeller ! Buy(3)

      expectMsg(Tickets(eventName, Vector.empty[Ticket]))
    }

    "return how many tickets it had left and kills itself on event cancellation" in {

      val ticketSeller = system.actorOf(TicketSeller.props(eventName))
      val probe = TestProbe()
      probe.watch(ticketSeller)

      val tickets = Vector(Ticket(1), Ticket(2))
      ticketSeller ! Add(tickets)

      ticketSeller ! Cancel

      expectMsg(Some(Coachella.Event(eventName, tickets.size)))

      probe.expectTerminated(ticketSeller)

    }
  }
}
