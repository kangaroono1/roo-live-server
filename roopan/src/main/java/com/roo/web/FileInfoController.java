package com.roo.web;

import org.junit.platform.commons.util.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

/**
 * 文件信息 Controller
 */
@RestController
@RequestMapping("/file")
public class FileInfoController {

    /** ========================================== 1) 上传模块 ======================================================= **/

    /**
     * 上传磁盘根目录
     */
    private static final String UPLOAD_PATH = "H:\\Uploads";

    @GetMapping("ping")
    public HashMap<String, Object> testPing() {
        HashMap<String, Object> map = new HashMap<>();

        File uploadFolder = new File(UPLOAD_PATH);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        map.put("code", 200);
        map.put("free_space", new File(UPLOAD_PATH).getFreeSpace());
        map.put("msg", "ping success!");
        return map;
    }

    /**
     * 文件整个上传（非分块）（TODO：响应信息补充）
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public HashMap<String, Object> handleFileUpload(@RequestParam MultipartFile file) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            // 创建 "uploads" 文件夹（如果不存在）
            File uploadFolder = new File(UPLOAD_PATH);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            if (uploadFolder.getFreeSpace() <= file.getSize()) {
                System.out.println("空间不足");
                map.put("code", 500);
                map.put("msg", "上传失败！空间不足！");
                return map;
            }

            // 处理文件上传逻辑
            String fileName = file.getOriginalFilename();
            // 构建目标文件对象
            File targetFile = new File(uploadFolder, fileName);
            // 将上传文件保存到目标文件
            file.transferTo(targetFile);

            map.put("code", 200);
            map.put("free_space", uploadFolder.getFreeSpace()); // 返回剩余空间
            map.put("msg", "上传成功");
            return map;
        } catch (IOException e) {

            e.printStackTrace();

            map.put("code", 500);
            map.put("msg", "上传失败！");
            return map;
        }
    }

    /**
     * 文件分块上传（TODO：响应信息补充）
     * @param chunkNumber 当前分块数
     * @param chunkTotal 总分块数
     * @param file 文件
     * @param fileName 文件名
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadBigFile")
    public String uploadBigFile(int chunkNumber, int chunkTotal, MultipartFile file, String fileName) throws IOException {
        fileName = URLDecoder.decode(fileName, "UTF-8");
        System.out.println("Uploading: " + fileName);
        file.transferTo(Paths.get(UPLOAD_PATH + "\\" + fileName + ".part" + chunkNumber));
        Double process = (chunkNumber * 1.0) / (chunkTotal * 1.0) * 100;
        DecimalFormat df = new DecimalFormat("0.00");
        String format = df.format(process) + "%";
        return format;
    }

    /**
     * 合并分块（TODO：响应信息补充）
     * @param chunkTotal 总的分块数
     * @param fileName 文件名称
     */
    @PutMapping("/merge")
    public void mergeBigFile(int chunkTotal, String fileName) throws IOException{
        fileName = URLDecoder.decode(fileName, "UTF-8");

        System.out.println("Merging: " + fileName);
        System.out.println("Merging * 2: " + URLDecoder.decode(fileName, "UTF-8"));

        try (FileOutputStream os = new FileOutputStream(UPLOAD_PATH + "\\" + fileName)) {
            for (int i = 1; i  <= chunkTotal; i++) {
                // D:\testuplosd\这个文件路径，在程序中可以配置在springboot的配置文件yml中。这里就不配置了
                Path part = Paths.get(UPLOAD_PATH + "\\" + fileName + ".part" + i);
                // 这个会自动关闭流的
                Files.copy(part, os);
                part.toFile().delete(); // 删除part文件
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件，防止不需要的文件还存在服务器中
     * @param fileName 文件名称
     * @return
     */
    @DeleteMapping("/deleteByFileName")
    public String deletyByFileName(String fileName) {
        // 这里不严谨，这个应该是通过文件的md5值和文件名一起判断来删除，会好一点，这里只是试着删除
        Path part = Paths.get(UPLOAD_PATH + "\\" + fileName);
        part.toFile().delete();
        return "删除文件成功";
    }

    /** =========================================== 2) 展示文件列表 ================================================== **/


    /**
     * 展示磁盘根目录
     */
    private static final String MEDIA_PATH = "H:\\Movie";


    /**
     * 展示指定目录下文件
     */
    @PostMapping("/showList")
    public ResponseEntity<Object> showList(@RequestBody List<String> pathMatch) {
        String Folder_PATH;

        if (pathMatch.size() == 0) {
            Folder_PATH = MEDIA_PATH;
        } else {
            String SUB_FIX = String.join("\\", pathMatch);
            try {
                Folder_PATH = MEDIA_PATH + "\\" + URLDecoder.decode(SUB_FIX, "UTF-8"); // 正反斜杠无所谓，只是这么写有高亮
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body("处理异常-3");
            }
            /** ==================================== 防目录攻击 ===================================================== **/
            if (!(new File(Folder_PATH).exists())) {
                System.err.println(Folder_PATH + " 路径文件不存在");
                return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("路径文件不存在");
            }


            if (StringUtils.isBlank(SUB_FIX) || SUB_FIX.contains("..")) {
                // 禁止空字符串或包含".."的输入
                System.err.println("Invalid SUB_FIX");
                return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid SUB_FIX");
            }

            Path baseDir = Paths.get(MEDIA_PATH); // 假设MEDIA_PATH已正确定义
            Path fullPath = baseDir.resolve(SUB_FIX); // 解析相对于baseDir的路径

            // 确保fullPath仍在baseDir的子目录下
            if (!fullPath.startsWith(baseDir)) {
                System.err.println(SUB_FIX);
                System.err.println("Invalid SUB_FIX: attempts to traverse outside base directory");
                return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid SUB_FIX: attempts to traverse outside base directory");
            }

            // 检查fullPath是否指向一个文件
            if (Files.isRegularFile(Paths.get(Folder_PATH))) {
                System.err.println("Invalid SUB_FIX: points to a file, not a directory");
                return ResponseEntity.status(HttpServletResponse.SC_ACCEPTED).body("Invalid SUB_FIX: points to a file, not a directory");
            }

            /** ================================================================================================== **/
        }

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

                        // 获取并添加文件的最后修改日期
                        try {
                            FileTime lastModifiedTime = Files.getLastModifiedTime(path);
                            // 将FileTime转换为Instant，然后可以转换为其他格式，如字符串
                            Instant instant = lastModifiedTime.toInstant();
                            String modifiedDateString = DateTimeFormatter.ISO_INSTANT.format(instant);
                            fileInfo.put("lastModified", modifiedDateString);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        fileList.add(fileInfo);
                    });
            return ResponseEntity.status(HttpServletResponse.SC_OK).body(fileList);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body("处理异常-2");
        }
    }

    /** ======================================== 3) 获取文件流 ======================================================= **/

    private static final int BUFFER_SIZE = 1024 * 1024 * 50; // 1MB * 50 = 50 MB
    private static final int CONTENT_LENGTH = 1024 * 1024 * 100; // 100 MB


    /**
     * 获取视频流（TODO：实现定位不明确，目前是该接口提供的是获取视频流和文件流的能力，待拆分）
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/getFileStream/**")
    public void getFileStream(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 在URI编码（也称为百分号编码或URL编码）中，加号（+）通常被用作空格（ ）的编码。
        // 但是，当解码一个URI组件时，尤其是当该组件被视为文件路径或查询参数的一部分时，这种转换规则可能会有所不同。
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String filePath = MEDIA_PATH + requestURI.substring(contextPath.length() + "/file/getFileStream".length());
        filePath = URLDecoder.decode(filePath, "UTF-8");

        File file = new File(filePath);

        // 检查文件是否存在
        if (!file.exists()) {
            System.err.println(filePath + " File Not Found");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (file.isDirectory()) {
            System.err.println(filePath + " ... it's a dir!");
            return;
        }

        if (filePath.substring(filePath.lastIndexOf('.') + 1).equals("mp4")) {
            response.setContentType("video/mp4"); // 假设是MP4视频
        }

        // 获取请求的Range头部
        String range = request.getHeader("Range");
        if (range == null) {
            // 没有Range头部，则发送整个文件
            response.setHeader("Accept-Ranges", "bytes");
            response.setContentLengthLong(file.length());
            try (FileInputStream fis = new FileInputStream(file)) {
                ServletOutputStream sos = response.getOutputStream();
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    sos.write(buffer, 0, bytesRead);
                }
                sos.flush();
            }
            return;
        }

        // 解析Range头部并确定开始和结束的字节位置
        // 例如: Range: bytes=0-499
        String[] rangeParts = range.replaceFirst("bytes=", "").split("-");
        long start = Long.parseLong(rangeParts[0]);
        long end = rangeParts.length > 1 ? Long.parseLong(rangeParts[1]) : file.length() - 1;

        // 验证范围
        if (start >= file.length() || end >= file.length() || start > end) {
            response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            response.setHeader("Content-Range", "bytes */" + file.length());
            return;
        }

        // 设置响应头部
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + file.length());
        int contentLength = (end-start+1) > CONTENT_LENGTH ? CONTENT_LENGTH : (int) (end-start+1);
        response.setContentLength(contentLength);
        System.out.printf("%s && s:%d-e:%d | slice: %d | content-length: %d\n", filePath, start, end, end-start, contentLength);

        // 使用RandomAccessFile来读取文件的特定部分
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(start);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            ServletOutputStream sos = response.getOutputStream();

            // 这里的检查是多余的，因为raf.read()在到达文件末尾时会返回-1，这时循环就会自然结束。
            while ((bytesRead = raf.read(buffer, 0, (int) Math.min(buffer.length, end - raf.getFilePointer() + 1))) != -1) {
                sos.write(buffer, 0, bytesRead);
                if (raf.getFilePointer() > end) {
                    break;
                }
            }
            sos.flush();
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }
    /** =========================================== End ============================================================ **/
}

