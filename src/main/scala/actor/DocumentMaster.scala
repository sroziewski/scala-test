package actor

import actor.GenericRouter._
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.datastax.driver.core.{Cluster, Row}
import document.CheckerProtocol.CheckMe
import document.DatabaseHandler
import document.DocumentProtocol.{ProcessDocuments, StartIteratingOverDocuments}
import monix.execution.{Ack, Scheduler}
import monix.reactive.Observable

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

class DocumentMaster(cluster: Cluster) extends Actor with ActorLogging {

  val databaseHandler = new DatabaseHandler(cluster)

  private val checkerRouter = makeRouter[DocumentChecker, CheckMe]("DocumentCheckerRouter", context)

  override def receive: Receive = {
    case StartIteratingOverDocuments(numActors) =>
      implicit val cs = databaseHandler.getSession
      implicit val scheduler = Scheduler.Implicits.global

      log.info(s"${self} message received, creating ${numActors} actors")

      val observable: Observable[Row] = databaseHandler.query()

      // nothing happens until we subscribe to this observable
      var i = 0
      val workers = createWorkers(numActors)
      var rows = new ListBuffer[Row]()

      observable.subscribe { row =>
        rows += row
        if(rows.length==5000){
          val feedingChunks = rows.grouped(5000/numActors).toList
          val f = Future {
            beginProcessing(feedingChunks, workers)
          }
          f.wait()
          log.info(s"${self} beginProcessing done...")
          rows = new ListBuffer[Row]()
        }
        i+=1
        Ack.Continue
      }
  }

  private[this] def createWorkers(numActors: Int): Seq[ActorRef] = {
    for (i <- 0 until numActors) yield context.actorOf(Props(new DocumentWorker(databaseHandler, checkerRouter)), name = s"DocumentWorker-${i}")
  }

  private[this] def beginProcessing(documents: List[ListBuffer[Row]], workers: Seq[ActorRef]) {
    documents.zipWithIndex.foreach(e => {
      workers(e._2 % workers.size) ! ProcessDocuments(e._1)
    })

  }
}
