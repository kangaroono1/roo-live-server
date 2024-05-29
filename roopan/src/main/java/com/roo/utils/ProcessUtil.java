package com.roo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


public class ProcessUtil {
    private static final Logger logger = LoggerFactory.getLogger(ProcessUtil.class);

    public static String excuteCommand(String cmd, Boolean printLog) throws Exception {
//        if (StringTool.isEmpty(cmd)) {
//            logger.error("--- 指令执行失败，执行的ffmpeg命令为空！ ---");
//            return null;
//        }
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            PrintStream errorStream = new PrintStream(process.getErrorStream());
            PrintStream inputSream = new PrintStream(process.getInputStream());
            errorStream.start();
            inputSream.start();
            process.waitFor();
            String result = errorStream.stringBuffer.append(inputSream.stringBuffer + "\n").toString();

            if (printLog) {
                logger.info("执行命令：{}，已执行完成，执行结果：{}", cmd, result);
            } else {
                logger.info("执行命令：{}，已执行完成", cmd);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("视频转换失败");
        } finally {
            if (null != process) {
                ProcessKiller ffmpegKilleer = new ProcessKiller(process);
                runtime.addShutdownHook(ffmpegKilleer);
            }
        }
    }
    /**
     * 程序退出前结束ffmpeg进程
     */
    private static class ProcessKiller extends Thread {
        private Process process;

        public ProcessKiller(Process process) { this.process = process; }

        @Override
        public void run() {
            this.process.destroy();
        }
    }

    /**
     * 用于取出ffmpeg线程执行过程中产生的各种输出和错误流信息
     */
    private static class PrintStream extends Thread {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = new StringBuffer();

        public PrintStream(InputStream inputStream) { this.inputStream = inputStream; }

        @Override
        public void run() {
            try {
                if (null == inputStream) {
                    return;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
            } catch (Exception e) {
                logger.error("读取输入流出错了！错误信息：{}", e.getMessage());
            } finally {
                try {
                    if (null != bufferedReader) {
                        bufferedReader.close();
                    }
                    if (null != inputStream) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    logger.error("调用PrintStream读取输出流后，关闭流时出错！");
                }

            }
        }
    }
}
