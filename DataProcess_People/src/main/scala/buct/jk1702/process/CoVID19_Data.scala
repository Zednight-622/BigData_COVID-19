package buct.jk1702.process

import java.util.Date
import java.text.SimpleDateFormat

import buct.jk1702.Bean.{CovidBean, StatisticsDataBean}
import buct.jk1702.util.BaseJDBCSink
import com.alibaba.fastjson.JSON
import org.apache.spark.SparkContext
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

import scala.collection.mutable

/**
 * @author Zednight
 * @date 2020/10/6 14:00
 */
object CoVID19_Data {
  def main(args: Array[String]): Unit = {
      val spark: SparkSession = SparkSession
      .builder()
      .master("local[*]")
      .appName("Covid19_Data")
      .getOrCreate()
  
      val sparkContext: SparkContext = spark.sparkContext
    sparkContext.setLogLevel("WARN")
    //导入隐式转换方便后续使用
    import spark.implicits._
    import org.apache.spark.sql.functions._
    import scala.collection.JavaConversions._
    //2.连接Kafka
    //从kafka接收消息
    val kafkaDF: DataFrame = spark.readStream
      .format( source = "kafka")
      .option("kafka.bootstrap.servers", "zednight.cn:9092,60.205.1.139:9092,101.201.123.177:9092")
      .option("subscribe", "COVID_19")
      .load()
    //取出消息中的vaLue
    val jsonStrDS: Dataset[String]= kafkaDF.selectExpr( exprs = "CAST(value AS STRING)").as[String]
    println("===========取出消息===========")
//    jsonStrDS.writeStream
//      .format( source = "console")//输出目的地
//      .outputMode( outputMode = "append")//输出模式，默认就是append表示显示新增行
//      .trigger(Trigger.ProcessingTime( 0))//触发间隔,0表示尽可能快的执行
//      .option("truncate",false)//表示如果列名过长不进行截断
//      .start()
    //3.处理数据
    //3.处理数据
    //将jsonStr转为样例类
    println("===========转化消息===========")
      val value: Dataset[CovidBean] = jsonStrDS.map(jsonStr => {
      //注意:Scala中获取cLass对象使用cLassOf[类名]
      //Java中使用类名.cLass/CLass.forName(全类路径)/对象.getCLass()
      JSON.parseObject(jsonStr, classOf[CovidBean])
    })
    //分离省份数据
      val provinceDS: Dataset[CovidBean] = value.filter(_.statisticsData != null)
      val statisticsDS: Dataset[StatisticsDataBean] = provinceDS.flatMap(p => {
        val data: String = p.statisticsData
        val beans: mutable.Buffer[StatisticsDataBean] = JSON.parseArray(data, classOf[StatisticsDataBean])
        beans.map(s => {
          s.provinceName = p.provinceName
          s.locationId = p.locationId
          s
      })
    })
    //4.统计分析
      // 1.通过全国疫情数据
//    println("===========统计全国消息===========")
//        val provinceDF: DataFrame = provinceDS.groupBy('datetime)
//      .agg(sum('currentConfirmedCount) as "currentConfirmedCount", //当前累计确诊
//        sum('suspectedCount) as "suspectedCount",//累计疑似
//        sum('curedCount) as "curedCount",//累计治愈
//        sum('deadCount) as "deadCount"//累计死亡
//      )
//    println("===========各省份消息===========")
      //2.各省份累计确诊数据
        //val allPDF: DataFrame = provinceDS.select('datetime, 'locationId, 'provinceShortName, 'currentConfirmedCount, 'confirmedCount, 'suspectedCount, 'curedCount, 'deadCount)
      //3.全国疫情趋势
        val result3: DataFrame = statisticsDS.select('dateId,'provinceName,'currentConfirmedCount, 'confirmedCount, 'suspectedCount, 'curedCount, 'deadCount)
      //4.境外输入排行
//    println("===========境外消息===========")
//        val result4: Dataset[Row] = cityDS
//        .filter(_.cityName.contains("境外"))
//        .groupBy('datetime, 'provinceShortName, 'pid).agg(
//        sum('confirmedCount) as "confirmedCount"
//      ).sort('confirmedCount.desc)
//
//      //5.统计北京市的累计确诊地图
//        val result5: DataFrame = cityDS.filter(_.provinceShortName.equals("北京")).select('datetime, 'locationId, 'provinceShortName, 'cityName, 'currentConfirmedCount, 'confirmedCount, 'suspectedCount, 'curedCount, 'deadCount)
    //5.结果输出
    result3.writeStream
      .format("console")
      .outputMode("append")
      .trigger(Trigger.ProcessingTime(0))
      .option("truncate",value = false)
      .start()
      //.awaitTermination()
    //6.结果保存至MySQL
    println("==========保存至MySQL============")
    result3.writeStream
      .foreach(new BaseJDBCSink("replace into statistics_data (date_id,province_name,current_confirmed_count, confirmed_count, suspected_count, cured_count, dead_count) values(?,?,?,?,?,?,?)") {
        override def realProcess(str: String, value: Row): Unit = {
          val date_id :String = value.getAs[String]("dateId")
          val dateId = new java.sql.Date(DateTime.parse(date_id, DateTimeFormat.forPattern("yyyyMMdd")).getMillis)
          val provinceName = value.getAs[String]("provinceName")
          val currentConfirmedCount = value.getAs[Int]("currentConfirmedCount")
          val confirmedCount = value.getAs[Int]("confirmedCount")
          val suspectedCount = value.getAs[Int]("suspectedCount")
          val curedCount = value.getAs[Int]("curedCount")
          val deadCount = value.getAs[Int]("deadCount")
          ps = conn.prepareStatement(str)
          ps.setDate(1,dateId)
          ps.setString(2,provinceName)
          ps.setInt(3,currentConfirmedCount)
          ps.setInt(4,confirmedCount)
          ps.setInt(5,suspectedCount)
          ps.setInt(6,curedCount)
          ps.setInt(7,deadCount)
          ps.execute()
        }
      })
      //.format("console")
      .outputMode("append")
      .trigger(Trigger.ProcessingTime(0))
      .option("truncate",value = false)
      .start()
      .awaitTermination()
  }
}
