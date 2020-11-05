package buct.jk1702.Bean

/**
 * @author Zednight
 * @date 2020/10/6 16:54
 */
case class StatisticsDataBean(
                               var dateId:String,
                               var provinceName:String,
                               var locationId:String,
                               var confirmedCount:Int,
                               var currentConfirmedCount:Int,
                               var currentConfirmedIncr:Int,
                               var confirmedIncr:Int,
                               var curedCount:Int,
                               var curedIncr:Int,
                               var suspectedCountIncr:Int,
                               var suspectedCount:Int,
                               var deadCount:Int,
                               var deadIncr:Int
                             )
