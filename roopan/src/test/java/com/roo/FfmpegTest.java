package com.roo;

import com.roo.utils.ProcessUtil;
import org.slf4j.Logger;

import java.io.File;

public class FfmpegTest {

    private static void cutFile4Video(String fileId, String videoFilePath) {
        // 创建同名切片目录
        File tsFolder = new File(
                videoFilePath.substring(
                        0,
                        videoFilePath.lastIndexOf(".")
                )
        );

        if (!tsFolder.exists()) {
            tsFolder.mkdir();
        }
        final String CMD_TRANSFER_2TS = "ffmpeg -y -i %s -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s";
        final String CMD_CUT_TS = "ffmpeg -i %s -c copy -map 0 -f segment -segment_list %s -segment_time 30 %s/%s_%%4d.ts";
        String tsPath = tsFolder + "/" + "index.ts";
        // 生成ts
        String cmd = String.format(CMD_TRANSFER_2TS, videoFilePath, tsPath);
        try {
            ProcessUtil.excuteCommand(cmd, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //生成索引文件.m3u8和切片.ts
        cmd = String.format(CMD_CUT_TS, tsPath, tsFolder.getPath() + "/" + "index.m3u8", tsFolder.getPath(), fileId);
        try {
            ProcessUtil.excuteCommand(cmd, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new File(tsPath).delete();
    }

    public static void main(String[] args) {
        cutFile4Video("100L", "H:\\Uploads\\index (4).mp4");
    }
}
