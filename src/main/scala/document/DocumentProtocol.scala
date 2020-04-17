/**
 * Created by Szymon Roziewski on 15/04/2020.
 */
package document

import akka.actor.Status._
import com.datastax.driver.core.Row

import scala.collection.mutable.ListBuffer

object DocumentProtocol {

  case class StartIteratingOverDocuments(numActors: Int)
  case class ProcessDocuments(documents: ListBuffer[Row])
  case class NumberOfWrittenChunks(count: Int)
  case class ProcessingFinished(url: String, data: Data)
  case class Data(url: String, status: Status, completed: Int, all: Int)
  case class Document(key: String, content: String)

}
