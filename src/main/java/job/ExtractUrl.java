package job;

import util.FileUtil;

import java.util.List;

/**
 * Created by pandechuan on 2018/10/09.
 */
public class ExtractUrl {

    public static void main(String[] args) {
        List<String> lines = FileUtil.readAllLines("/Users/pandechuan/Desktop/BookMarks-bak.txt");
//        lines.stream().filter(e -> e.contains(" \"name\":") || e.contains(" \"url\":"))
//                .forEach(e -> System.out.println(e.trim()));

        lines.stream().filter(e -> e.contains("netease")).forEach(e -> {
//            if (e.contains("\"name\":")) {
//                System.out.println(e.replace("\"name\":", ""));
//            } else if (e.contains("\"url\":")) {
//                System.out.println(e.replace("\"url\":", ""));
//            }
            System.out.println(e.replace("\"", ""));
        });
    }

}
