package buct.jk1702.Entity;

import lombok.Data;

import java.util.List;

/**
 * @author Zednight
 * @date 2020/9/29 19:12
 */
@Data
public class Province {
    private String provinceName;
    private String provinceShortName;
    private String currentConfirmedCount;
    private String confirmedCount;
    private String suspectedCount;
    private String curedCount;
    private String deadCount;
    private String comment;
    private String locationId;
    private String statisticsData;
    private String cities;

    //实际cities对象
    private List<City> _cities;
    //实际DatState对象
    private List<DayState> _statisticsData;
}
