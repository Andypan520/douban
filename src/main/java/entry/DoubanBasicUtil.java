package entry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.sun.org.apache.bcel.internal.generic.NEW;
import db.RedisUtil;
import httpbrowser.MyHttpResponse;
import ip.IPUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import util.FileUtil;
import util.JsonUtil;

import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by pandechuan on 2018/09/25.
 */
public class DoubanBasicUtil {
    private static final String doubanTagPage = "https://book.douban.com/tag/";

    public static void main(String[] args) {
        String tagsKey = "doubantags";
        String tagMapKey = "doubantagmap";
        Jedis jedis = RedisUtil.getJedis();
        // clear list
//        jedis.ltrim(tagKey, -1, 0);
        List<String> tags = grabTags();
//        System.out.println(tags);
//        Long result = jedis.rpush(tagKey, tags.toArray(new String[]{}));
//        System.out.println(result);
//        List<String> tagValues = jedis.lrange(tagKey, 0, -1);
//        System.out.println(tagValues);
//
//        Map<String, List<String>> map = tagMap();
//        System.out.println(map);
//        // jedis.set(tagMapKey, JsonUtil.toJson(map));
//        System.out.println(jedis.get(tagMapKey));
//        Map<String, List<String>> map2 = JsonUtil.readObject(jedis.get(tagMapKey), Map.class);
//
//        map2.forEach((k, v) -> System.out.println(k + "===" + v));
//        System.out.println(map2);
        RedisUtil.close(jedis);
    }


    private static List<String> grabTags() {
        // String html = MyHttpResponse.getHtml(doubanTagPage);
//        String html = MyHttpResponse.getHtml(doubanTagPage, "118.190.95.35", "9001");
       // String html = MyHttpResponse.getHtml("http://www.xinhuanet.com/politics/leaders/2018-09/25/c_1123481011.htm");
        String html = MyHttpResponse.getHtml("http://report.igame.163.com/m/report/my/edit");
//        String html = null;
        if (html == null) {
            List<String> lines = FileUtil.readAllLines("/Users/pandechuan/Desktop/豆瓣图书标签.html");
            StringBuilder stringBuilder = new StringBuilder();
            lines.stream().forEach(e -> stringBuilder.append(e));
            html = stringBuilder.toString();
        }
        //将html解析成DOM结构
        Document document = Jsoup.parse(html);

        //提取所需要的数据
        List<String> tagList = document.getElementsByClass("tagCol")
                .select("a").stream().map(e -> e.text()).collect(Collectors.toList());
        return tagList;
    }

    private static Map<String, List<String>> tagMap() {
        // String html = MyHttpResponse.getHtml(doubanTagPage);
        String html = null;
        if (html == null) {
            List<String> lines = FileUtil.readAllLines("/Users/pandechuan/Desktop/豆瓣图书标签.html");
            StringBuilder stringBuilder = new StringBuilder();
            lines.stream().forEach(e -> stringBuilder.append(e));
            html = stringBuilder.toString();
        }
        //将html解析成DOM结构
        Document document = Jsoup.parse(html);

        List<String> parentTags = document.getElementsByClass("tag-title-wrapper").select("h2").eachText();
        Map<String, List<String>> tagsMap = Maps.newHashMap();

        Elements childTags = document.getElementsByClass("tagCol");

        for (int i = 0; i < parentTags.size(); i++) {
            tagsMap.put(parentTags.get(i).substring(0, 2), childTags.get(i).select("a").eachText());
        }
        return tagsMap;
    }
}
