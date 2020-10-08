package buct.jk1702.crawler;

import buct.jk1702.Entity.City;
import buct.jk1702.Entity.DayState;
import buct.jk1702.Entity.Province;
import buct.jk1702.utils.HttpUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.internal.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sun.misc.Regexp;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zednight
 * @date 2020/9/29 19:02
 */
public class DataCrawler {
    public static void main(String[] args) {
        new DataCrawler().getData();
    }

    public void getData() {
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
        List<Province> provinces = JSON.parseArray(res, Province.class);
        for (Province province : provinces) {
            //解析城市对象
            List<City> cities = JSON.parseArray(province.getCities(), City.class);
            //爬取每日数据
            String statisticsData = HttpUtils.getHtml(province.getStatisticsData());
            if(statisticsData==null) continue;
            JSONObject jsonObject = JSON.parseObject(statisticsData);
            String data = jsonObject.getString("data");
            //解析每日数据
            List<DayState> dayStates = JSON.parseArray(data, DayState.class);
            //注入
            province.set_cities(cities);
            province.set_statisticsData(dayStates);
        }
    }
}
