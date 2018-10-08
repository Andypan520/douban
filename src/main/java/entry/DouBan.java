package entry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

import com.google.common.collect.Maps;
import meta.Book;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import dao.BookListDao;
import util.FileUtil;

public class DouBan {

    private static Map<String, String> cookies = Maps.newHashMap();

    private static LongAdder longAdder = new LongAdder();

    public static File errorFile = new File("./error");

    BookListDao saveBookUtil = new BookListDao();

    static {
        //book.douban.com
        cookies.put("__utma", "81379588.1625906329.1478780180.1478780180.1478780180.1");
        cookies.put("__utmb", "81379588.1.10.1478780180");
        cookies.put("__utmc", "81379588");
        cookies.put("__utmz", "81379588.1478780180.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
        cookies.put("_pk_id.100001.3ac3", "b8e7b1931da4acd1.1478780181.1.1478780181.1478780181.");
        cookies.put("_pk_ses.100001.3ac3", "*");
        //douban.com
        cookies.put("bid", "MvEsSVNL_Nc");
        //read.douban.com
        cookies.put("_ga", "GA1.3.117318709.1478747468");
        cookies.put("_pk_id.100001.a7dd", "ce6e6ea717cbd043.1478769904.1.1478769904.1478769904.");
        cookies.put("_pk_ref.100001.a7dd", "%5B%22%22%2C%22%22%2C1478769904%2C%22https%3A%2F%2Fbook.douban.com%2"
                + "Fsubject_search%3Fsearch_text%3D%25E6%258E%25A8%25E8%258D%2590%25E7%25B3%25BB%25E7%25BB%259F%25"
                + "E5%25AE%259E%25E8%25B7%25B5%26cat%3D1001%22%5D");
        //www.douban.com
        cookies.put("_pk_id.100001.8cb4", "237bb6b49215ebbc.1478749116.2.1478774039.1478749120.");
        cookies.put("_pk_ref.100001.8cb4", "%5B%22%22%2C%22%22%2C1478773525%2C%22https%3A%2F%2Fwww.baidu."
                + "com%2Flink%3Furl%3DlQ4OMngm1b6fAWeomMO7xq6PNbBlxyhdnHqz9mIYN9-ycRbjZvFb1NQyQ7hqzvI46-WThP"
                + "6A_Qo7oTQNP-98pa%26wd%3D%26eqid%3Da24e155f0000e9610000000258244a0c%22%5D");
    }

    /**
     * 抓取每本书的info
     */
    public static void getBookInfo(List<String> bookUrls) {
        bookUrls.parallelStream().forEach(e -> {
            buildBookInfo(e);
        });

    }

    private static void buildBookInfo(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .header("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)").cookies(cookies)
                    .timeout(3000).get();
            Elements titleElement = doc.getElementsByClass("subject clearfix").select("a");
            Elements scoreElement = doc.select("strong");
            Elements ratingSum = doc.getElementsByClass("rating_sum").select("a").select("span");
            Elements authorElement = doc.getElementById("info").select("span").first().select("a");
            Element pressElement = doc.getElementById("info");
            Elements tagsElement = doc.getElementsByClass("  tag");
            List<String> tags = tagsElement.eachText();

            // 书名
            String title = titleElement.attr("title").trim();
            // 评分
            String score = scoreElement.html().trim();
            // 评价人数
            String ratingNum = ratingSum.html().trim();

            // 作者
            String author = authorElement.html().trim();
            // 出版社
            String press = pressElement.text();
            if (press.indexOf("出版社:") > -1) {
                press = pressElement.text().split("出版社:")[1].split(" ")[1].trim();
            } else {
                press = "";
            }
            // 出版日期
            String date = pressElement.text();
            if (date.indexOf("出版年:") > -1) {
                date = pressElement.text().split("出版年:")[1].split(" ")[1].toLowerCase();
            } else {
                date = "";
            }
            // 价格
            String price = pressElement.text();
            if (price.indexOf("定价:") > -1) {
                price = pressElement.text().split("定价:")[1].split(" ")[1].trim();
                if (price.equals("CNY")) {
                    price = pressElement.text().split("定价:")[1].split(" ")[2].trim();
                }
            } else {
                price = "";
            }

            Book book = buildBook(tags, title, score, ratingNum, author, press, date, price);

            try {
                BookListDao.saveBook(book);
            } catch (Exception e) {
                FileUtil.append(DouBan.errorFile, book.toString());
            }

            // 睡眠防止ip被封
            try {
                Thread.currentThread().sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Book buildBook(List<String> tags, String title, String score, String ratingNum, String author, String press, String date, String price) {
        Book book = new Book();
        book.setType(1);
        book.setTitle(title);
        book.setAuthor(author);
        book.setDate(date);
        book.setTags(tags);
        book.setPress(press);
        book.setPrice(price);

        if (NumberUtils.isParsable(ratingNum)) {
            book.setRatingNum(Integer.parseInt(ratingNum));
        }
        if (NumberUtils.isParsable(score)) {
            book.setScore(Double.parseDouble(score));
        }
        return book;
    }

    /**
     * 保存书的url
     *
     * @param keyWord
     * @return
     */
    public static List<String> downloadBookUrl(String keyWord) {
        ArrayList<String> bookUrls = new ArrayList<>();
        int index = 0;
        try {
            Map<String, String> cookies = Maps.newHashMap();
            //book.douban.com
            cookies.put("__utma", "81379588.1625906329.1478780180.1478780180.1478780180.1");
            cookies.put("__utmb", "81379588.1.10.1478780180");
            cookies.put("__utmc", "81379588");
            cookies.put("__utmz", "81379588.1478780180.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
            cookies.put("_pk_id.100001.3ac3", "b8e7b1931da4acd1.1478780181.1.1478780181.1478780181.");
            cookies.put("_pk_ses.100001.3ac3", "*");
            //douban.com
            cookies.put("bid", "MvEsSVNL_Nc");
            //read.douban.com
            cookies.put("_ga", "GA1.3.117318709.1478747468");
            cookies.put("_pk_id.100001.a7dd", "ce6e6ea717cbd043.1478769904.1.1478769904.1478769904.");
            cookies.put("_pk_ref.100001.a7dd", "%5B%22%22%2C%22%22%2C1478769904%2C%22https%3A%2F%2Fbook.douban.com%2"
                    + "Fsubject_search%3Fsearch_text%3D%25E6%258E%25A8%25E8%258D%2590%25E7%25B3%25BB%25E7%25BB%259F%25"
                    + "E5%25AE%259E%25E8%25B7%25B5%26cat%3D1001%22%5D");
            //www.douban.com
            cookies.put("_pk_id.100001.8cb4", "237bb6b49215ebbc.1478749116.2.1478774039.1478749120.");
            cookies.put("_pk_ref.100001.8cb4", "%5B%22%22%2C%22%22%2C1478773525%2C%22https%3A%2F%2Fwww.baidu."
                    + "com%2Flink%3Furl%3DlQ4OMngm1b6fAWeomMO7xq6PNbBlxyhdnHqz9mIYN9-ycRbjZvFb1NQyQ7hqzvI46-WThP"
                    + "6A_Qo7oTQNP-98pa%26wd%3D%26eqid%3Da24e155f0000e9610000000258244a0c%22%5D");

            while (true) {
                // 获取cookies

                Document doc = Jsoup.connect("https://book.douban.com/tag/" + keyWord + "?start=" + index + "&type=T")
                        .header("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)").cookies(cookies)
                        .timeout(3000).get();
                Elements newsHeadlines = doc.select("ul").select("h2").select("a");
                System.out.println("本页：  " + newsHeadlines.size());
                for (Element e : newsHeadlines) {
                    System.out.println(e.attr("href"));
                    bookUrls.add(e.attr("href"));
                }
                index += newsHeadlines.size();
                System.out.println("共抓取url个数：" + index);
                if (newsHeadlines.size() == 0) {
                    System.out.println("end");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookUrls;
    }

    public static void main(String[] args) throws IOException {
        long starTime = System.currentTimeMillis();
        List<String> bookUrls;
        bookUrls = downloadBookUrl("哲学");
        getBookInfo(bookUrls);
        long endTime = System.currentTimeMillis();
        long Time = endTime - starTime;
        System.out.println("执行耗时 : " + Time + " 毫秒 ");
        System.out.println("执行耗时 : " + Time / 1000f + " 秒 ");


    }


}
