package buct.jk1702.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Zednight
 * @date 2020/9/29 18:22
 */
public abstract class HttpUtils {

    //httpclient 连接池
    private static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

    private static List<String> userAgent;

    private static RequestConfig requestConfig;

    static {
        poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(20);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(10);

        requestConfig = RequestConfig.custom()
                        .setConnectionRequestTimeout(3000)
                        .setSocketTimeout(3000)
                        .setConnectTimeout(1000)
                        .build();

        userAgent = new ArrayList<String>();
        userAgent.add("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
        userAgent.add("Mozilla/5.0 (Windows NT 6.2; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
        userAgent.add("Mozilla/5.0 (compatible; WOW64; MSIE 10.0; Windows NT 6.2)");
        userAgent.add("Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/533.20.25 (KHTML, like Gecko) Version/5.0.4 Safari/533.20.27");
        userAgent.add("Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3");
    }

    public static String getHtml(String url) {
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("User-Agent",userAgent.get(new Random().nextInt(userAgent.size())));
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                String html = "";
                if (response.getEntity() != null) {
                    html = EntityUtils.toString(response.getEntity(), "UTF-8");
                }
                return html;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
