package buct.jk1702.crawler;

import buct.jk1702.Entity.CovidBean;
import buct.jk1702.Entity.WorldBean;
import buct.jk1702.utils.HttpUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zednight
 * @date 2020/10/12 12:20
 */
@Component
public class WorldCrawler {
    public static void main(String[] args) {
        new WorldCrawler().getData();
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    //@Scheduled(initialDelay = 1000,fixedDelay = 1000*60*60*24)
    public void getData() {
        String format = SimpleDateFormat.getInstance().format(new Date(System.currentTimeMillis()));
        String html = HttpUtils.getHtml("http://ncov.dxy.cn/ncovh5/view/pneumonia");
//        System.out.println(html);
        Document document = Jsoup.parse(html);
        String s = document.select("script[id=getListByCountryTypeService2true]").toString();
        Pattern pattern = Pattern.compile("\\[(.*)\\]");
        String res = "";
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            res = matcher.group(0);
        }else{
            System.out.println("No Match");
        }
        List<WorldBean> pCovidBeans = JSON.parseArray(res, WorldBean.class);
        for (WorldBean pBean : pCovidBeans){ //pBean为省份
//            System.out.println(pBean);
            //先设置时间字段
            pBean.setDatetime(format);
            //6.获取第一层json(省份数据)中的每一天的统计数据
            String statisticsDataUrl = pBean.getStatisticsData();
            String statisticsDataStr = HttpUtils.getHtml(statisticsDataUrl);
            //获取statisticsDataStr中的data字段对应的数据
            JSONObject jsonobject = JSON.parseObject(statisticsDataStr);
            String dataStr = jsonobject.getString("data");
            pBean.setStatisticsData(dataStr);
            String jsonString = JSON.toJSONString(pBean);
            System.out.println("发送省份数据"+jsonString);
            kafkaTemplate.send("COVID_19", pBean.getId(), jsonString);
        }
    }
}
