import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import sun.net.www.http.HttpClient;

import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Zednight
 * @date 2020/9/26 20:27
 */
public class HttpClientTest {
    @Test
    public void HCTest() throws IOException {
        Document document = Jsoup.parse(new URL("https://www.baidu.com"),3000);
        Elements elements = document.getAllElements();
        System.out.println(elements.toString());
    }
    @Test
    public void pool() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(20); //最大连接数
        connectionManager.setDefaultMaxPerRoute(10);//每个主机并发数
        try {
            doGet(connectionManager);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void doGet(PoolingHttpClientConnectionManager connectionManager) throws Exception {
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(connectionManager).build();
        HttpGet httpGet = new HttpGet("http://ncov.dxy.cn/ncovh5/view/pneumonia");
        CloseableHttpResponse response = client.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == 200) {
            String html = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println(html);
        }
        response.close();
    }
}
