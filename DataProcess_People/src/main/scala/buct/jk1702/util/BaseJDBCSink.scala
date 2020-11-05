package buct.jk1702.util

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.spark.sql.{ForeachWriter, Row}

/**
 * @author Zednight
 * @date 2020/10/9 14:41
 */
abstract class BaseJDBCSink(sql:String) extends ForeachWriter[Row] {
  var conn:Connection=_
  var ps:PreparedStatement=_

  def realProcess(str:String,value:Row)

  override def open(partitionId: Long, epochId: Long): Boolean = {
    conn = DriverManager.getConnection("jdbc:mysql://rm-8vb178367o06xa531do.mysql.zhangbei.rds.aliyuncs.com:3306/covid19?characterEncoding=utf8&serverTimezone=UTC","zednight","Iamxujiangyi622")
    true
  }

  override def process(value: Row): Unit = {
    realProcess(sql,value)
  }

  override def close(errorOrNull: Throwable): Unit = {
    if(conn!=null) conn.close()
    if(ps!=null) ps.close()
  }
}
