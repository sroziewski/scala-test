package actor

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.util.Timeout
import document.DatabaseHandler

import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit.SECONDS

import document.CheckerProtocol.CheckMe
import document.DocumentProtocol.{Document, ProcessDocument}

class DocumentWorker(dbaseHandler: DatabaseHandler, checker: ActorRef) extends Actor with ActorLogging  {
  implicit val askTimeout = Timeout(Duration(5, SECONDS))

  private var counterOfWrittenDocuments: Int = 0

  override def receive: Receive = {
    case ProcessDocument(document: Document) =>
      processDocument(document)
  }

  private def processDocument(document: Document): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global

    checker ! CheckMe(document, dbaseHandler, self)

    log.info(s"Processing document ${document} ...")
  }
}
