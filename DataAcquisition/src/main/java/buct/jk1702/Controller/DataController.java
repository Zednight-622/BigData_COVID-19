package buct.jk1702.Controller;

import buct.jk1702.crawler.DataCrawler;
import buct.jk1702.crawler.WorldCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * @author Zednight
 * @date 2020/10/9 15:39
 */
@RestController
public class DataController {
    @Autowired
    private DataCrawler dataCrawler;
    @Autowired
    private WorldCrawler worldCrawler;

    @RequestMapping("/getData")
    public String init(){
        dataCrawler.getData();
        return "Success!";
    }
    @RequestMapping("/getAllData")
    public String initData(){
        worldCrawler.getData();
        return "Success!";
    }

}
