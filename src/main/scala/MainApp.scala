import actor.DocumentMaster
import akka.actor.{ActorSystem, Props}
import com.datastax.driver.core.{Cluster, ProtocolOptions, Row, SocketOptions}
import document.DocumentProtocol.StartIteratingOverDocuments

import scala.collection.JavaConversions._
import scala.concurrent.Future

object MainApp extends App{

    val system = ActorSystem("Document-Extractor-Manager")

    private def config = system.settings.config

    private val cassandraConfig = config.getConfig("akka.main.db.cassandra")
    private val port = cassandraConfig.getInt("port")
    private val hosts = cassandraConfig.getStringList("hosts")

//    val conf: SparkConf = new SparkConf().setAppName("scala_streaming_test").set("spark.cassandra.connection.host", "127.0.0.1")
//    val ssc: StreamingContext = new StreamingContext(conf, Seconds(10))

    val socketOptions = new SocketOptions()
    socketOptions.setReadTimeoutMillis(10000000)
    socketOptions.setConnectTimeoutMillis(10000000)
    socketOptions.setKeepAlive(true)

    lazy val dbaseHandler: Cluster =
      Cluster.builder().
        addContactPoints(hosts: _*).
//        withCompression(ProtocolOptions.Compression.SNAPPY).
        withPort(port).
        withSocketOptions(socketOptions).
        build()




    lazy val schemaGetDocument =
        """
          |SELECT * FROM ngramspace.text
          |LIMIT 1
    """.stripMargin
//    lazy val getDocumentStatement = session.prepare(schemaGetDocument)

//    val resultSet = session.execute("SELECT * FROM ngramspace.text limit 1")

//    val columns = resultSet.getColumnDefinitions

//    println(columns)

    val i = 1

    val documentMaster = system.actorOf(Props(new DocumentMaster(dbaseHandler)), name = "DocumentMaster")
    val numberOfActors = 1 //8 //24 // 3 times more than #cores

    documentMaster ! StartIteratingOverDocuments(numberOfActors)

    system.whenTerminated.wait()
}
