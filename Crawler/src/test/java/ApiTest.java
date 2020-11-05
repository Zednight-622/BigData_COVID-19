import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Zednight
 * @date 2020/9/26 20:19
 */
public class ApiTest {
    @Test
    public void test() throws IOException {
        URL url = new URL("http://www.baidu.com");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(3000);
    }
}
