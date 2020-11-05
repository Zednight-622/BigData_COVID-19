import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Zednight
 * @date 2020/10/6 15:51
 */
public class TimeTest {
    public static void main(String[] args) {
        String format = SimpleDateFormat.getInstance().format(new Date(System.currentTimeMillis()));
        System.out.println(format);
    }
}
