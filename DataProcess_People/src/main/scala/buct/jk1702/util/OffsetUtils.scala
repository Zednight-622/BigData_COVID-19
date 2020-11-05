package buct.jk1702.util

import java.sql.{DriverManager, PreparedStatement}

import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.kafka010.OffsetRange

import scala.collection.mutable

/**
 * @author Zednight
 * @date 2020/10/5 18:23
 */
object OffsetUtils {
  def getOffset(gourpid: String, topic: String): mutable.Map[TopicPartition, Long] = {
    val connection = DriverManager.getConnection("jdbc:mysql:://localhost:3306/bigdata?characterEncoding=UTF-8", "root", "root")
    //2.编写sqL
    val sql = "select partition,offset from t_offset where groupid = ? and topic = ?"
    //3.创建预编译语句对象
    val ps: PreparedStatement = connection.prepareStatement(sql)
    ps.setString(1,gourpid)
    ps.setString(2,topic)
    val set = ps.executeQuery()
    val offsetMap:mutable.Map[TopicPartition, Long] = mutable.Map[TopicPartition, Long]()
    while(set.next()){ 
      val partition = set.getInt("partition")
      val offset = set.getInt("offset")
      offsetMap += new TopicPartition(topic,partition) -> offset
    }
    set.close()
    ps.close()
    connection.close()
    offsetMap
  }

  /**
   *
   * @param groupId
   * @param offsets
   * CREATE TABLE t_offset(
   * `topic` varchar(255) NOT NULL,
   * `partition` int(11)NOT NULL,
   * `groupid` varchar(255)NOT NULL,
   * `offset` bigint(20)DEFAULT NULL,
   * PRIMARY KEY (`topic`,`partition`,`groupid`)
   * )ENGINE=InnoDB DEFAULT CHARSET=utf8;
   */
  def saveOffset(groupId:String,offsets:Array[OffsetRange]): Unit ={
    val connection = DriverManager.getConnection("jdbc:mysql:://localhost:3306/bigdata?characterEncoding=UTF-8", "root", "root")
    //2.编写sqL

    //3.创建预编译语句对象
    val ps: PreparedStatement = connection.prepareStatement("replace into t_offset (topic,partition,groupid,offset) values (?,?,?,?)")
    //4.设置参数并执行
    for (o<-offsets){
      ps.setString(1,o.topic)
      ps.setInt( 2,o.partition)
      ps.setString( 3,groupId)
      ps.setLong(  4,o.untilOffset)
      ps.executeUpdate()
    }
    ps.close()
    connection.close()
  }
}
