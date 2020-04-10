import akka.actor.{ActorSystem, Props}
import com.datastax.driver.core.{ProtocolOptions, Cluster}

import scala.collection.JavaConversions._

object MainApp extends App{

    val system = ActorSystem("Sentence-Extractor-Manager")

    private def config = system.settings.config

    private val cassandraConfig = config.getConfig("akka.main.db.cassandra")
    private val port = cassandraConfig.getInt("port")
    private val hosts = cassandraConfig.getStringList("hosts").toList

//    val conf: SparkConf = new SparkConf().setAppName("scala_streaming_test").set("spark.cassandra.connection.host", "127.0.0.1")
//    val ssc: StreamingContext = new StreamingContext(conf, Seconds(10))

    lazy val dbaseHandler: Cluster =
      Cluster.builder().
        addContactPoints(hosts: _*).
        withCompression(ProtocolOptions.Compression.SNAPPY).
        withPort(port).
        build()

    println(dbaseHandler.getClusterName)

    val i = 1

/*    val m = system.actorOf(Props(new SentenceMaster(dbaseHandler)), name = "Sentence-Master")
    val numberOfActors = 1 //8 //24 // 3 times more than #cores

    m ! StartIteratingOverWebsites(numberOfActors)

    system.awaitTermination()*/
}
