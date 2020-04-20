import actor.DocumentMaster
import akka.actor.{ActorSystem, Props}
import com.datastax.driver.core.{Cluster, SocketOptions}
import document.DocumentProtocol.StartIteratingOverDocuments

object MainApp extends App{

    val system = ActorSystem("Document-Extractor-Manager")

    private def config = system.settings.config

    private val cassandraConfig = config.getConfig("akka.main.db.cassandra")
    private val port = cassandraConfig.getInt("port")
    private val hosts = cassandraConfig.getStringList("hosts")

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

    val documentMaster = system.actorOf(Props(new DocumentMaster(dbaseHandler)), name = "DocumentMaster")
    val numberOfActors = 100 //8 //24 // 3 times more than #cores

    documentMaster ! StartIteratingOverDocuments(numberOfActors)

    system.whenTerminated.wait()
}
