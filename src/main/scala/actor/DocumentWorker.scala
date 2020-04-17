package actor

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.util.Timeout
import document.DatabaseHandler

import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit.SECONDS

import com.datastax.driver.core.Row
import document.CheckerProtocol.CheckMe
import document.DocumentProtocol.{Document, ProcessDocuments}

import scala.collection.mutable.ListBuffer

class DocumentWorker(dbaseHandler: DatabaseHandler, checker: ActorRef) extends Actor with ActorLogging  {
  implicit val askTimeout = Timeout(Duration(5, SECONDS))

  private var counterOfWrittenDocuments: Int = 0

  override def receive: Receive = {
    case ProcessDocuments(documents: ListBuffer[Row]) =>
      processDocuments(documents)
  }

  private def processDocuments(documents: ListBuffer[Row]): Unit = {
    documents.foreach(document=>checker ! CheckMe(document, dbaseHandler, self))
    log.info(s"$self : Processing documents ...")
  }
}
