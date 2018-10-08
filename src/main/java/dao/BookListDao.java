package dao;

import com.google.common.collect.Lists;
import meta.Book;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.StringUtils;
import db.C3P0Util;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CREATE TABLE `BookList` (
 * `id` bigint(11)  NOT NULL PRIMARY KEY AUTO_INCREMENT,
 * `title` varchar(128)  COMMENT 'title',
 * `author` varchar(128)  COMMENT 'author',
 * `price` varchar(128)  COMMENT 'author',
 * `date` varchar(128)  COMMENT 'author',
 * `press` varchar(128)  COMMENT 'author',
 * `score` double(10,1)  COMMENT 'score',
 * `ratingNum` int(11)  COMMENT 'rateNum',
 * `tags` varchar(500) COMMENT 'tags'
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 */
public class BookListDao {

    public static void saveBook(Book book) {
        try {
            QueryRunner queryRunner = new QueryRunner();
            Connection connection = C3P0Util.getConnection();
            String sql = "insert into BookList(title,type,author,price,date,press,score,ratingNum,tags) values(?,?,?,?,?,?,?,?,?)";

            Object[] params = buildParamArray(book);
            queryRunner.update(connection, sql, params);
            DbUtils.close(connection);
        } catch (Exception e) {
//            FileUtil.append(ParsePage.errorFile, book.toString());
            e.printStackTrace();
        }
    }

    private static Object[] buildParamArray(Book book) {
        List<Object> paramList = Lists.newArrayList(
                book.getTitle(),
                book.getType(),
                book.getAuthor(),
                book.getPrice(),
                book.getDate(),
                book.getPress(),
                book.getScore(),
                book.getRatingNum(),
                StringUtils.join(book.getTags(), ",")
        );
        return paramList.toArray(new Object[paramList.size()]);
    }

    private static Object[][] build2DimArrayParams(List<Book> books) {
        Object[][] params = new Object[books.size()][];
        List<Object[]> paramList = books.stream().map(BookListDao::buildParamArray).collect(Collectors.toList());
        for (int index = 0; index < paramList.size(); index++) {
            params[index] = paramList.get(index);
        }
        return params;
    }


    public static void saveBooks(List<Book> books) {
        try {
            QueryRunner queryRunner = new QueryRunner();
            Connection connection = C3P0Util.getConnection();
            String sql = "insert into BookList(title,type,author,price,date,press,score,ratingNum,tags) values(?,?,?,?,?,?,?,?,?)";
            Object[][] params = build2DimArrayParams(books);
            queryRunner.batch(connection, sql, params);
            DbUtils.close(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Book book = new Book("555");
        book.setTags(Lists.newArrayList("rr", "tt", "ff"));
        Book book2 = new Book("666");
        BookListDao.saveBook(book);
//        saveBooks(Lists.newArrayList(book, book2));
    }


}
