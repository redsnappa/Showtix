package com.showtix.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestKitBase}
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
    val ticketSeller = system.actorOf(TicketSeller.props(eventName))

    "be instantiated with no tickets and have an event name" in {

      ticketSeller ! GetEvent
      expectMsg(Coachella.Event(eventName,0))
    }

    "add new tickets for it's event" in {
      val newTickets = Vector(Ticket(1), Ticket(2), Ticket(3), Ticket(4))
      ticketSeller ! Add(newTickets)
      ticketSeller ! GetEvent
      expectMsg(Coachella.Event(eventName, newTickets.size))
    }

  }

}
