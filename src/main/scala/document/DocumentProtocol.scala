/**
 * Created by Szymon Roziewski on 15/04/2020.
 */
package document

import akka.actor.Status._

object DocumentProtocol {

  case class StartIteratingOverDocuments(numActors: Int)
  case class ProcessDocument(document: Document)
  case class NumberOfWrittenChunks(count: Int)
  case class ProcessingFinished(url: String, data: Data)
  case class Data(url: String, status: Status, completed: Int, all: Int)
  case class Document(key: String, content: String)

}
