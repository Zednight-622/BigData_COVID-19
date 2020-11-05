package buct.jk1702.Bean

/**
 * @author Zednight
 * @date 2020/10/6 16:45
 */
case class CovidBean(
                      provinceName: String , //省份名称

                      provinceShortName: String , //省份短名

                      cityName: String ,
                      currentConfirmedCount: Int , //当前确诊人数

                      confirmedCount: Int , //累计确诊人数

                      suspectedCount: Int , //疑似病例人数

                      curedCount: Int , //治愈人数

                      deadCount: Int , //死亡人数

                      locationId: String , //位置id

                      pid: String ,
                      statisticsData: String , //每一天的统计数据

                      cities: String , //下属城市

                      datetime: String //获取时间
                    )
