package buct.jk1702.Entity;

import lombok.Data;

/**
 * @author Zednight
 * @date 2020/10/12 12:17
 */
@Data
public class WorldBean {
    private String provinceName;//省份名称
    private Integer currentConfirmedCount;//当前确诊人数
    private Integer confirmedCount;//累记确诊人数
    private Integer suspectedCount;//疑似病例人数
    private Integer curedCount;//治愈人数
    private Integer deadCount;//死亡人数
    private String locationId;//位置id
    private String id;//id
    private String statisticsData;//每一天的统计数据
    private String countryShortCode;//
    private String countryFullName;
    private String datetime;//获取时间
}
