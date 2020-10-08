package buct.jk1702.Entity;

import lombok.Data;

/**
 * @author Zednight
 * @date 2020/9/29 19:39
 */
@Data
public class DayState {
    private String confirmedCount;
    private String currentConfirmedCount;
    private String curedCount;
    private String currentConfirmedIncr;
    private String confirmedIncr;
    private String curedIncr;
    private String dateId;
    private String suspectedCountIncr;
    private String deadCount;
    private String deadIncr;
    private String suspectedCount;
}
