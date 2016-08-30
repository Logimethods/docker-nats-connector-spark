val natsUrl = "OUTPUT"
val inputSubject = "INPUT"
val outputSubject = "nats://nats-main:4222"

import java.util.Properties;

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.Duration
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.storage.StorageLevel;
import io.nats.client.Constants._

import com.logimethods.nats.connector.spark._

  val ssc = new StreamingContext(sc, new Duration(2000));

  val properties = new Properties();
  properties.put("servers", natsUrl)
  properties.put(PROP_URL, natsUrl)
  val messages = ssc.receiverStream(NatsToSparkConnector.receiveFromNats(properties, StorageLevel.MEMORY_ONLY, inputSubject))
  
  messages.print()
  
  ssc.start();		
  
  ssc.awaitTermination()

