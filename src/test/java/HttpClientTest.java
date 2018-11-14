import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import db.C3P0Util;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.sql.SQLException;

/**
 * Created by pandechuan on 2018/09/26.
 */
public class HttpClientTest {


    @Test
    //HTTP 请求
    public void test() throws URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.google.com")
                .setPath("/search")
                .setParameter("q", "httpclient")
                .setParameter("btnG", "Google Search")
                .setParameter("aq", "f")
                .setParameter("oq", "")
                .build();
        HttpGet httpget = new HttpGet(uri);
        System.out.println(httpget.getURI());
    }

    @Test
    //HTTP 响应
    public void test2() {
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        System.out.println(response.getProtocolVersion());
        System.out.println(response.getStatusLine().getStatusCode());
        System.out.println(response.getStatusLine().getReasonPhrase());
        System.out.println(response.getStatusLine().toString());
    }

    @Test
//    HTTP 消息头
    public void test3() {
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        response.addHeader("Set-Cookie", "c1=a; path=/; domain=localhost");
        response.addHeader("set-Cookie", "c2=b; path=\"/\", c3=c; domain=\"localhost\"");
        Header h1 = response.getFirstHeader("Set-Cookie");
        System.out.println(h1);
        Header h2 = response.getLastHeader("Set-Cookie");
        System.out.println(h2);
        Header[] hs = response.getHeaders("Set-Cookie");
        System.out.println(hs.length);
    }

    @Test
    public void test4() {
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        response.addHeader("Set-Cookie", "c1=a; path=/; domain=localhost");
        response.addHeader("set-Cookie", "c2=b; path=\"/\", c3=c; domain=\"localhost\"");

        HeaderIterator it = response.headerIterator("Set-Cookie");

        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    @Test
    public void test5() {
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        response.addHeader("Set-Cookie", "c1=a; path=/; domain=localhost");
        response.addHeader("set-Cookie", "c2=b; path=\"/\", c3=c; domain=\"localhost\"");

        HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Set-Cookie"));

        while (it.hasNext()) {
            HeaderElement elem = it.nextElement();
            System.out.println(elem.getName() + " = " + elem.getValue());

            NameValuePair[] params = elem.getParameters();
            for (int i = 0; i < params.length; i++) {
                System.out.println(" " + params[i]);
            }
        }
    }

    @Test
    public void test6() throws Exception {
        StringEntity myEntity = new StringEntity("important message", ContentType.create("text/plain", "UTF-8"));

        System.out.println(myEntity.getContentType());
        System.out.println(myEntity.getContentLength());
        System.out.println(EntityUtils.toString(myEntity));
        System.out.println(EntityUtils.toByteArray(myEntity).length);
    }

    @Test
    public void test7() throws Exception {

        //HttpClient 的实现应该是线程安全的，建议在执行多次请求时复用同一个 HttpClient 对象。
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("http://localhost/8181");
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                    // do something useful
                } finally {
                    instream.close();
                }
            }
        } finally {
            response.close();
        }
    }

    @Test
    public void test7_1() throws Exception {
        CloseableHttpResponse response;
        HttpEntity entity;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet("http://localhost/8181");
            response = httpclient.execute(httpget);
        }
        entity = response.getEntity();
        InputStream instream = entity.getContent();
    }

    /**
     * 消耗实体内容
     * 推荐一种消耗实体内容的方式，使用 HttpEntity#getContent() 或 HttpEntity#writeTo(OutputStream) 方法。
     * HttpClient 配合 EntityUtils 类，这个类用几个静态方法，能让读取实体中的内容或信息更加简单。
     * 不是直接读取 java.io.InputStream 对象，你可以用这个类的某些方法，用字符串或是字节数组来检索整个内容主题。
     * 但是强烈不推荐使用 EntityUtils 类，除非响应实体来源于可信的 HTTP 服务器，并且知道它的最大长度。
     *
     * @throws Exception
     */
    @Test
    public void test8() throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("http://www.baidu.com/");
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                long len = entity.getContentLength();
                if (len != -1 && len < 2048) {
                    System.out.println(EntityUtils.toString(entity));
                } else {
                    // Stream content out
                    System.out.println(EntityUtils.toString(entity));
                }
            }
        } finally {
            response.close();
        }
    }

    @Test
    public void test9() throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("http://www.baidu.com/");
        CloseableHttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            entity = new BufferedHttpEntity(entity);
            System.out.println(entity.getContent().toString());
            System.out.println(entity.getContentType().getElements());
        }
    }

    @Test
    public void test10() throws Exception {
        /**
         * Response handlers
         最简单、最方便的处理响应的方式是使用 ResponseHandler 接口，它包含了 handleResponse(HttpResponse response) 方法。
         这个方法彻底的解决了用户关于连接管理的担忧。使用 ResponseHandler 时，HttpClient 会自动处理连接，无论执行请求成功
         或是发生了异常，都确保连接释放到连接管理者。
         */
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("http://localhost/json");

        ResponseHandler<JsonObject> rh = new ResponseHandler<JsonObject>() {

            @Override
            public JsonObject handleResponse(final HttpResponse response) throws IOException {
                StatusLine statusLine = response.getStatusLine();
                HttpEntity entity = response.getEntity();
                if (statusLine.getStatusCode() >= 300) {
                    throw new ClientProtocolException("Response contains no content");
                }

                Gson gson = new GsonBuilder().create();
                ContentType contentType = ContentType.getOrDefault(entity);
                Charset charset = contentType.getCharset();
                Reader reader = new InputStreamReader(entity.getContent(), charset);
                return gson.fromJson(reader, JsonObject.class);
            }
        };
        JsonObject myjson = httpclient.execute(httpget, rh);
    }
}
