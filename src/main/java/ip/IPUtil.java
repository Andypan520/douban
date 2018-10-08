package ip;

import httpbrowser.MyHttpResponse;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by pandechuan on 2018/09/25.
 */
public class IPUtil {

    public static void main(String[] args) {
        //xici 首页
        String url = "http://www.xicidaili.com/nt/1";
        grabIpByLocal(url).stream().forEach(System.out::println);
    }

    /**
     * grab Ip By Local
     *
     * @param url
     * @return
     */
    public static List<IPMeta> grabIpByLocal(String url) {
        //调用一个类使其返回html源码
        String html = MyHttpResponse.getHtml(url);
        //将html解析成DOM结构
        Document document = Jsoup.parse(html);

        //提取所需要的数据
        Elements trs = document.select("table[id=ip_list]").select("tbody").select("tr");
        return trs.stream().skip(1).map(IPUtil::buildIP).collect(Collectors.toList());
    }

    /**
     * grab Ip By Proxy
     *
     * @param url
     * @param ip
     * @param port
     * @return
     */
    public static List<IPMeta> grabIpByProxy(String url, String ip, String port) {

        String html = MyHttpResponse.getHtml(url, ip, port);
        Document document = Jsoup.parse(html);

        //提取所需要的数据
        Elements trs = document.select("table[id=ip_list]").select("tbody").select("tr");
        return trs.stream().skip(1).map(IPUtil::buildIP).collect(Collectors.toList());
    }


    /**
     * @param ipMetaList
     */
    public static void removeUnavaliableIp(List<IPMeta> ipMetaList) {
        try {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                ipMetaList = ipMetaList.stream().map(e -> {
                    String ip = e.getIPAddress();
                    String port = e.getIPPort();
                    HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
                    RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(5000).
                            setSocketTimeout(5000).build();
                    HttpGet httpGet = new HttpGet("https://www.baidu.com");
                    httpGet.setConfig(config);

                    httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;" +
                            "q=0.9,image/webp,*/*;q=0.8");
                    httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
                    httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
                    httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit" +
                            "/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
                    try {
                        try (CloseableHttpResponse response2 = httpClient.execute(httpGet)) {
                            return e;
                        }
                    } catch (IOException ex) {
                        System.out.println("不可用代理已删除" + e.getIPAddress()
                                + ": " + e.getIPPort());
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 过滤低质量ip
     *
     * @param ipList
     * @return
     */
    public static List<IPMeta> removeLowQualityIp(List<IPMeta> ipList) {
        return ipList.stream().filter(e -> {
            String ipSpeed = e.getIPSpeed();
            ipSpeed = ipSpeed.substring(0, ipSpeed.indexOf('秒'));
            if (e.getIPType().equalsIgnoreCase("HTTPS") && Double.parseDouble(ipSpeed) <= 2.0) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
    }

    // build IP from html element
    private static IPMeta buildIP(Element e) {
        IPMeta ipMeta = new IPMeta();
        String ipAddress = e.select("td").get(1).text();
        String ipPort = e.select("td").get(2).text();
        String ipType = e.select("td").get(5).text();
        String ipSpeed = e.select("td").get(6).select("div[class=bar]").
                attr("title");
        ipMeta.setIPAddress(ipAddress);
        ipMeta.setIPPort(ipPort);
        ipMeta.setIPType(ipType);
        ipMeta.setIPSpeed(ipSpeed);
        return ipMeta;
    }
}
