package cn.zednight.Conntroller;

import cn.zednight.Entity.StatisticsDO;
import cn.zednight.Mapper.StatisticsDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Zednight
 * @date 2020/10/12 18:18
 */
@RestController
@RequestMapping("/bigdata")
public class DataController {
    @Autowired
    private StatisticsDOMapper statisticsDOMapper;

//    @RequestMapping("/worldData/{dateId}/{province}")
//    public List<StatisticsDO> getList(@PathVariable("dateId")String date, @PathVariable("province")String name) throws ParseException {
//        Date d = new SimpleDateFormat("yyyyMMdd").parse(date);
//        System.out.println(d.toString());
//        return statisticsDOMapper.selectByDateAndName(d,name);
//    }

    @RequestMapping("/worldData/getById/{id}")
    public StatisticsDO getById(@PathVariable("id")Integer id){
        return statisticsDOMapper.selectByPrimaryKey(id);
    }

    @RequestMapping("/worldData/getByName/{name}")
    public List<StatisticsDO> getCountryData(@PathVariable("name")String name) throws ParseException {
        return statisticsDOMapper.selectByName(name);
    }
}
