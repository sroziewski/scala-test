package actor

import java.util.concurrent.TimeUnit.SECONDS

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.util.Timeout
import com.datastax.driver.core.Row
import document.CheckerProtocol.CheckMe
import document.DatabaseHandler
import document.DocumentProtocol.ProcessDocuments
import utils.SpellCorrector

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration

class DocumentWorker(dbaseHandler: DatabaseHandler, checker: ActorRef) extends Actor with ActorLogging  {
  implicit val askTimeout = Timeout(Duration(5, SECONDS))

  val corpus = "nkjp-corpus.txt"
  val sp = new SpellCorrector(corpus)

  override def receive: Receive = {
    case ProcessDocuments(documents: ListBuffer[Row]) =>
      processDocuments(documents)
  }

  private def processDocuments(documents: ListBuffer[Row]): Unit = {
    documents.foreach(document=>checker ! CheckMe(document, dbaseHandler, sp, self))
    log.info(s"$self : Processing documents ...")
  }
}
