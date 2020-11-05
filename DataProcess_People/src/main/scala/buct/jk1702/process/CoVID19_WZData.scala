package buct.jk1702.process

import buct.jk1702.util.OffsetUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, ConsumerStrategies, HasOffsetRanges, KafkaUtils, LocationStrategies, OffsetRange}

import scala.collection.mutable
/**
 * @author Zednight
 * */
object CoVID19_WZData {
  def main(args: Array[String]): Unit = {
    //准备SparkStreaming环境
    val sparkConf: SparkConf = new SparkConf().setAppName("CoVID19_WZData").setMaster("local[*]")
    val context: SparkContext = new SparkContext(sparkConf)
    context.setLogLevel("WARN")
    val streamingContext: StreamingContext = new StreamingContext(context, Seconds(5))
    streamingContext.checkpoint("./streamingContextCKP")
    //准备Kafka连接参数
    val kafkaParam = Map[String, Object](
      "bootstrap.servers" -> "hadoop1:9092,hadoop2:9092,hadoop3:9092", //集群地址
      "group.id" -> "SparkKafka",
      //latest标示从已标识的偏移量出开始，没有就从最新/最后出开始消费
      //earliest标示从已标识的偏移量出开始，没有就从最开始/最早出开始消费
      //none 标示从已标识的偏移量出开始，没有就报错
      "auto.offset.reset" -> "latest", //偏移量重置
      "enable.auto.commit" -> (true: java.lang.Boolean),
      "key.deserializer"->classOf[StringDeserializer],
      "value.deserializer"->classOf[StringDeserializer]
    )
    //kafka连接参数
    val topics: Array[String] = Array("COVID_19_WZ")

    //从MySQL中查出offset的信息
    val offsetsMap:mutable.Map[TopicPartition,Long] = OffsetUtils.getOffset("SparkKafka", "COVID_19_WZ")
    //连接Kafka获取消息
    val kafkaDS: InputDStream[ConsumerRecord[String,String]] = if(offsetsMap.nonEmpty){
      println("获取到Offset数据，从offset处消费")
      KafkaUtils.createDirectStream[String, String](
        streamingContext,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String, String](topics, kafkaParam,offsetsMap))
    }else{
      println("未获取到Offset数据，从latest消费")
      KafkaUtils.createDirectStream[String, String](
        streamingContext,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String, String](topics, kafkaParam))
 }
    //实时处理消息
    val value = kafkaDS.map(_.value())//消费的数据
    value.print()
    kafkaDS.foreachRDD(rdd=>{
      if(rdd.count()>0){
        rdd.foreach(record=>print("从kafka中消费的数据"+record))
        val ranges: Array[OffsetRange] = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        for(o<-ranges){
          println(s"topic=${o.topic},partition=${o.partition},offset=${o.fromOffset},until=${o.untilOffset}")
        }
        //手动提交offset到Kafka的_consumer_offsets
        //kafkaDS.asInstanceOf[CanCommitOffsets].commitAsync(ranges)
        OffsetUtils.saveOffset("SparkKafka", ranges)
      }
    })
    //存入到MySQL
    //开启SparkStreaming任务等待结束
    streamingContext.start()
    streamingContext.awaitTermination()
  }
}
