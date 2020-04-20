# scala-test
An App for  Cassandra document processing via Akka

I make use of Akka library for efficient and scalable document processing.
I use Akka library for building Actor Model architecture.
There is a Manager Actor, which creates Document Worker Actors (30 actors) and there exists a router of Document Checker Actors (100 actors).

Documents read from Cassandra DB are processed.
Document Workers get a bunch of documents containing website texts. The textual context is further queried for another tasks i.e. 
sentences extraction and spelling correction.
All in all, processed documents with useful content are stored into Cassandra DB.

I have more than 50 million of documents to be done.
