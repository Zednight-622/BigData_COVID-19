package buct.jk1702.Entity;

import lombok.Data;

/**
 * @author Zednight
 * @date 2020/9/29 19:13
 */
@Data
public class City {
    private String pId;
    private String cityName;
    private String currentConfirmedCount;
    private String confirmedCount;
    private String suspectedCount;
    private String curedCount;
    private String deadCount;
    private String locationId;
}
