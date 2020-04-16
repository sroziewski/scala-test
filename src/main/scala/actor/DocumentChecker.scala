package actor

import akka.actor.{Actor, ActorLogging, ActorRef}
import document.CheckerProtocol.CheckMe
import document.DatabaseHandler
import document.DocumentProtocol.Document

/**
 * Created by sroziewski on 15/04/2020.
 */

class DocumentChecker extends Actor with ActorLogging {

  private var count: Int = 0
  private val notSentence = "[--]{2,}|[|]|[——]{2,}".r
  private val emailReg = "[^@]+@[^@]+\\.[^@]+".r
  private val urlReg = "\\b(https?|ftp|file|www)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".r

  def receive: Receive = {

    case CheckMe(document: Document, dbaseHandler: DatabaseHandler, sentenceWorkerRef: ActorRef) =>
      val originalSender = sender()

      log.info(s"Checker ${self} has received CheckMe for a sentence...")


//      val sentences: Array[String] = document.split("(?<=[.?!])\\s+(?=[a-zA-Z])").filter(_.split("\\s+").size>2)
//      sentences.foreach(checkSentence(_))

//      log.info(s"Checking sentence...")
//      isSentence.findFirstIn(sentence)



    //      val uniqueCharsNumber = words.par.map(_.groupBy(c => c.toLower).size)

  }

  private def checkSentence(input: String): Unit ={
    val regex = ":\\)|:\\(|:P|;\\)|\\^\\.\\^|:~\\(|:\\-o|:\\*\\-/|:\\-c|:\\-D|:'|:bow:|:whistle:|:zzz:|:kiss:|:rose:";
    val sentence = input.replaceAll(regex, "") // remove smileys
    if(notSentence.findFirstIn(sentence).isEmpty){
      val words = sentence
        .split("\\s+")
        .filter{case emailReg() => false case _ => true}
        .filter{case urlReg() => false case _ => true}
        .map(_.toLowerCase)
      val corrected = words.map(_.replaceAll("\\p{P}"," ")).map{w=>
//        if(sp.invMap.contains(w)) w else sp.correctPolish(w)
      }
      val s = corrected.foldLeft("")((r,c) => r+" "+c)
      println(s)
    }
    else
    {
      println("ELSE: "+sentence)
    }
  }

  override def postStop(): Unit = {
    log.info(s"Bouncer actor is stopped: ${self}, $count messages received")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {

    log.error(reason, "Bouncer restarting due to [{}] when processing [{}]",
      reason.getMessage(), if (message.isDefined) message.get else "")

    context.children foreach { child =>
      context.unwatch(child)
      context.stop(child)
    }
    postStop()
  }

}


