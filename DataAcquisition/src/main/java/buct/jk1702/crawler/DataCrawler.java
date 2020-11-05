package buct.jk1702.crawler;

import buct.jk1702.Entity.City;
import buct.jk1702.Entity.CovidBean;
import buct.jk1702.Entity.DayState;
import buct.jk1702.Entity.Province;
import buct.jk1702.Producer.KafkaProducer;
import buct.jk1702.utils.HttpUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zednight
 * @date 2020/9/29 19:02
 */
@Component
public class DataCrawler {
    public static void main(String[] args) {
        new DataCrawler().getData();
    }


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(initialDelay = 1000,fixedDelay = 1000*60*60*24)
    public void getData() {
        String format = SimpleDateFormat.getInstance().format(new Date(System.currentTimeMillis()));
        String html = HttpUtils.getHtml("http://ncov.dxy.cn/ncovh5/view/pneumonia");
        Document document = Jsoup.parse(html);
        String s = document.select("script[id=getAreaStat]").toString();
        Pattern pattern = Pattern.compile("\\[(.*)\\]");
        String res = "";
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            res = matcher.group(0);
        }else{
            System.out.println("No Match");
        }
        List<CovidBean> pCovidBeans = JSON.parseArray(res, CovidBean.class);
        for (CovidBean pBean : pCovidBeans){ //pBean为省份
            System.out.println(pBean);
            //先设置时间字段
            pBean.setDatetime(format);
            //获取cities
            String citysStr = pBean.getCities();
            //将第二层json(城市数据)解析为avaBean
            List<CovidBean> covidBeans = JSON.parseArray(citysStr,CovidBean.class);
            for (CovidBean bean : covidBeans) {
                // bean为城市
                //  System.out.println(bean);
                bean.setDatetime(format);
                bean.setPid(pBean.getLocationId());//把省份的d作为城市的pid
                bean.setProvinceShortName(pBean.getProvinceShortName());
                String string = JSON.toJSONString(bean);
                System.out.println("发送城市数据："+string);
                kafkaTemplate.send("COVID_19",bean.getPid(),string);
            }
            //6.获取第一层json(省份数据)中的每一天的统计数据
            String statisticsDataUrl = pBean.getStatisticsData();
            String statisticsDataStr = HttpUtils.getHtml(statisticsDataUrl);
            //获取statisticsDatastr中的data字段对应的数据
            JSONObject jsonobject = JSON.parseObject(statisticsDataStr);
            String datastr = jsonobject.getString("data" );
            pBean.setStatisticsData(datastr);
            pBean.setCities(null);
            String jsonString = JSON.toJSONString(pBean);
            System.out.println("发送省份数据"+jsonString);
            kafkaTemplate.send("COVID_19", pBean.getLocationId(), jsonString);
        }
    }
}

