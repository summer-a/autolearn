package com.autolearn.icve.utils;

import com.xiaoleilu.hutool.system.OsInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 视频工具
 *
 * @author 胡江斌
 * @version 1.0
 * @title: VideoUtil
 * @projectName autolearn
 * @description: TODO
 * @date 2020/5/15 18:40
 */
@Slf4j
public class VideoUtil {

    /** 当前环境 */
    private static final boolean IS_LINUX;

    static {
        OsInfo info = new OsInfo();
        IS_LINUX = info.isLinux();
    }

    /**
     * 获取https链接的视频时长
     *
     * @param httpUrl
     * @return 秒
     */
    public static double getHttpsVideoLong(String httpUrl) {

        List<String> commands = new ArrayList<>();
        if (IS_LINUX) {
            commands.add("docker");
            commands.add("run");
            commands.add("--rm");
            commands.add("-v=pwd:/tmp/ffmpeg");
            commands.add("opencoconut/ffmpeg");
        } else {
            commands.add("ffmpeg");
        }

        commands.add("-i");
        commands.add(httpUrl);

        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);

            final Process p = builder.start();

            //从输入流中读取视频信息
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            //从视频信息中解析时长
            String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";
            Pattern pattern = Pattern.compile(regexDuration);
            Matcher m = pattern.matcher(sb.toString());
            if (m.find()) {
                String group = m.group(1);
                return getTimelen(group);
            }
        } catch (Exception e) {
            log.error("视频长度获取出错", e);
        }

        return 0;
    }

    /**
     * 格式:"00:00:10.68"
     *
     * @param timelen
     * @return
     */
    private static double getTimelen(String timelen) {
        double second = 0;
        String[] timeStr = timelen.split(":");
        if (timeStr[0].compareTo("0") > 0) {
            second += Integer.valueOf(timeStr[0]) * 60 * 60;
        }
        if (timeStr[1].compareTo("0") > 0) {
            second += Integer.valueOf(timeStr[1]) * 60;
        }
        if (timeStr[2].compareTo("0") > 0) {
            second += Double.valueOf(timeStr[2]);
        }
        return second;
    }
}
