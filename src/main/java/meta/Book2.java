package meta;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by pandechuan on 2018/09/20.
 */
public class Book2 {

    private String title;
    /**
     * 1:计算机类，2，经济金融类 3，哲学心理学 4，自然科学科普类，5 人文历史社科
     */
    private int type;
    private String author;
    private double score;
    private int ratingNum;
    private String tags;
    private String price;
    private String date;
    private String press;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getRatingNum() {
        return ratingNum;
    }

    public void setRatingNum(int ratingNum) {
        this.ratingNum = ratingNum;
    }


    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", type=" + type +
                ", author='" + author + '\'' +
                ", score=" + score +
                ", ratingNum=" + ratingNum +
                ", tags=" + tags +
                ", price='" + price + '\'' +
                ", date='" + date + '\'' +
                ", press='" + press + '\'' +
                '}';
    }

    public static void main(String[] args) {
        Book2 book = new Book2();
        book.setTitle("aaa");
        book.setAuthor("bb");
        book.setDate("2019");
        book.setTags("ff");
        book.setPress("ddd");
        book.setPrice("47");
        book.setRatingNum(98);
        book.setScore(9.8);
        System.out.println(book);


        String title = "title + \"\\t\" + author + \"\\t\" + score + \"\\t\" + ratingNum + \"\\t\" + press + \"\\t\" + price + \"\\t\" + date + \"\\t\" + tags";
        System.out.println(title);
    }


}
