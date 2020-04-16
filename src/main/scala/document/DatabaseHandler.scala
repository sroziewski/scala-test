package document

import java.util.concurrent.Executor

import com.datastax.driver.core._
import com.google.common.util.concurrent.{FutureCallback, Futures, ListenableFuture}
import document.DocumentProtocol.Document
import monix.eval.Task
import monix.reactive.Observable

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by Szymon Roziewski 15/04/2020
 */
class DatabaseHandler(dbaseHandler: Cluster) {

  import scala.collection.JavaConversions._

  def parseRow(r: Row): Document = {
    val key = r.getString("key")
    val content = r.getString("content")
    Document(key, content)
  }

  val session: Session = dbaseHandler.connect(Keyspaces.ngramKeyspace)
  val documentsQuery: PreparedStatement = session.prepare("select * from text limit 100;")

  def getSession: Session = session

  def getDocuments: Iterable[Document] = {
    session.execute(documentsQuery.bind()).map(row => parseRow(row))
  }

  def getDocumentsAsFuture: Future[ResultSet] = {
    Future {
      session.execute(documentsQuery.bind())
    }
  }

  def query()(
    implicit executionContext: ExecutionContext, cassandraSession: Session
  ): Observable[Row] = {

    val observable = Observable.fromAsyncStateAction[Future[ResultSet], ResultSet](
      nextResultSet =>
        Task.fromFuture(nextResultSet).flatMap { resultSet =>
          Task((resultSet, resultSet.fetchMoreResults))
        }
    )(getDocumentsAsFuture)

    observable
      .takeWhile(rs => !rs.isExhausted)
      .flatMap { resultSet =>
        val rows = (1 to resultSet.getAvailableWithoutFetching) map (_ => resultSet.one)
        Observable.fromIterable(rows)
      }
  }

  implicit def toScalaFuture[T](lFuture: ListenableFuture[T]): Future[T] = {
    val p = Promise[T]
    Futures.addCallback(lFuture,
      new FutureCallback[T] {
        def onSuccess(result: T) = p.success(result)
        def onFailure(t: Throwable) = p.failure(t)
      }, ExecutionContext.global)
    p.future
  }

}
