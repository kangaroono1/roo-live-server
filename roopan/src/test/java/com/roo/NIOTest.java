package com.roo;

import org.junit.platform.commons.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class NIOTest {

    private static final String MEDIA_PATH = "H:/Movie"; // 例如："/path/to/media"

    private static void getFileNameAndType() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> fileList = new ArrayList<>(); // 创建一个包含文件信息的列表

        try (Stream<Path> paths = Files.list(Paths.get(MEDIA_PATH))) {
            paths
                    .forEach(path -> {
                        Map<String, Object> fileInfo = new HashMap<>();
                        String fileName = path.getFileName().toString();
                        fileInfo.put("fileName", fileName);

                        if (Files.isDirectory(path)) {
                            fileInfo.put("type", "folder");
                        } else {
                            String fileExtension = Optional.ofNullable(fileName.lastIndexOf('.'))
                                    .filter(index -> index > 0)
                                    .map(index -> fileName.substring(index + 1))
                                    .orElse(null);
                            fileInfo.put("type", fileExtension == null ? "unknown" : fileExtension);
                        }

                        fileList.add(fileInfo);
                    });

            map.put("code", 200);
            map.put("msg", "获取成功");
            map.put("fileList", fileList); // 返回文件列表

            System.out.println(map.toString());

        } catch (IOException e) {
            e.printStackTrace();
            map.put("code", 500);
            map.put("msg", "获取失败！");
        }
    }

    private static void subDotOfMp4() {
        String fileName = "xxxxxx.mp4";
        String str = fileName.substring(fileName.lastIndexOf('.') + 1);
        System.out.println(str);
    }

    private static void showList(String SUB_FIX) {
        String Folder_PATH = MEDIA_PATH + "/" + SUB_FIX;

        /** ==================================== 防目录攻击 ===================================================== **/
        if (!(new File(Folder_PATH).exists())) {
            System.err.println("路径文件不存在");
            return;
        }

        if (StringUtils.isBlank(SUB_FIX) || SUB_FIX.contains("..")) {
            // 禁止空字符串或包含".."的输入
            System.err.println("Invalid SUB_FIX");
            return ;
//            throw new IllegalArgumentException("Invalid SUB_FIX");
        }

        Path baseDir = Paths.get(MEDIA_PATH); // 假设MEDIA_PATH已正确定义
        Path fullPath = baseDir.resolve(SUB_FIX); // 解析相对于baseDir的路径

        // 确保fullPath仍在baseDir的子目录下
        if (!fullPath.startsWith(baseDir)) {
            System.err.println("Invalid SUB_FIX: attempts to traverse outside base directory");
            return ;
//            throw new IllegalArgumentException("Invalid SUB_FIX: attempts to traverse outside base directory");
        }

        /** ================================================================================================== **/
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> fileList = new ArrayList<>(); // 创建一个包含文件信息的列表

        try (Stream<Path> paths = Files.list(Paths.get(Folder_PATH))) {
            paths
                    .forEach(path -> {
                        Map<String, Object> fileInfo = new HashMap<>();
                        String fileName = path.getFileName().toString();
                        fileInfo.put("fileName", fileName);

                        if (Files.isDirectory(path)) {
                            fileInfo.put("type", "folder");
                        } else {
                            String fileExtension = Optional.ofNullable(fileName.lastIndexOf('.'))
                                    .filter(index -> index > 0)
                                    .map(index -> fileName.substring(index + 1))
                                    .orElse(null);
                            fileInfo.put("type", fileExtension == null ? "unknown" : fileExtension);
                        }

                        fileList.add(fileInfo);
                    });
            System.out.println(fileList.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        showList("/../.../..");
//        String pathInfo = "/file/getFileStream/show-all-files/%E7%89%A9%E8%AF%AD%E7%B3%BB%E5%88%97ALL/06%E7%8C%AB%E7%89%A9%E8%AF%AD%20%E7%99%BD02.mp4";
//        pathInfo = pathInfo.substring("/file/getFileStream/show-all-".length());
//        System.out.println(pathInfo);
        try {
            System.out.println(URLDecoder.decode("H:\\Movie\\%E7%89%A9%E8%AF%AD%E7%B3%BB%E5%88%97ALL\\10%E6%81%8B%E7%89%A9%E8%AF%AD\\物语系列ALL", "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
