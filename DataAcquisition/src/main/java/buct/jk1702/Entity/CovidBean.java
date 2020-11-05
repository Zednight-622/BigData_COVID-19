package buct.jk1702.Entity;


import lombok.Data;

@Data
public class CovidBean {
    private String provinceName;//省份名称
    private String provinceShortName;//省份短名
    private String cityName;
    private Integer currentConfirmedCount;//当前确诊人数
    private Integer confirmedCount;//累记确诊人数
    private Integer suspectedCount;//疑似病例人数
    private Integer curedCount;//治愈人数
    private Integer deadCount;//死亡人数
    private String locationId;//位置id
    private String pid;//位置id
    private String statisticsData;//每一天的统计数据
    private String cities;//下属城市
    private String datetime;//获取时间
}
