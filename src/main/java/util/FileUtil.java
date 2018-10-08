package util;

/**
 * Created by pandechuan on 2018/09/18.
 */

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.primitives.Chars;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {

//    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 获取某个目录下的子文件名
     *
     * @param dirName
     * @return
     * @throws IOException
     */
    public static List<String> getSubDirNames(String dirName) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dirName))) {
            return stream.map(String::valueOf)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    /**
     * 读取某个文件
     *
     * @param fileName
     * @return
     */
    public static List<String> readAllLines(String fileName) {
        List<String> lines = Lists.newArrayList();
        try {
            //  lines = Files.readAllLines(Paths.get(fileName));
            lines = com.google.common.io.Files.readLines(new File(fileName), Charsets.UTF_8);
        } catch (Exception e) {
//            logger.error("readAllLines error!");
        }
        return lines;
    }

    /**
     * 从某个目录下按照最大深度查找模糊匹配的文件名
     *
     * @param root
     * @param maxDepth
     * @param containContent
     * @throws IOException
     */
    public static String findVagueFileNameWithDepth(String root, int maxDepth, String containContent) throws IOException {
        Path start = Paths.get(root);
        try (Stream<Path> stream = Files.find(start, maxDepth, (path, attr) ->
                String.valueOf(path).endsWith(containContent))) {
            String joined = stream
                    .sorted()
                    .map(String::valueOf)
                    .collect(Collectors.joining("; "));
            return joined;
        }
    }

    public static void write(String file, String content) throws IOException {
        Path path = Paths.get(file);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(content);
        }
    }

    /**
     * delete the content of text file without deleting itself
     *
     * @param file
     */
    public static void clear(File file) {
        try (PrintWriter printWriter = new PrintWriter(file)) {
            printWriter.write("");
        } catch (IOException e) {
//            logger.error("clear file {} error", file.getName(), e);
        }
    }

    public static void append(File file, String line) throws IOException {
        com.google.common.io.Files.append(line + "\n", file, Charsets.UTF_8);

    }


    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

}