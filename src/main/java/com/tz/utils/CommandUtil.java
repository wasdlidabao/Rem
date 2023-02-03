package com.tz.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CommandUtil {
    public static String run(String command) throws IOException {
        Scanner input = null;
        String result = "";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            try {
                //等待命令执行完成
                process.waitFor(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            InputStream is = process.getInputStream();
            input = new Scanner(is);
            while (input.hasNextLine()) {
                result += input.nextLine() + "\n";
            }
            result = command + "\n" + result; //加上命令本身，打印出来
        } finally {
            if (input != null) {
                input.close();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    public static String run(String[] command) throws IOException {
        Scanner input = null;
        String result = "";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            try {
                //等待命令执行完成
                process.waitFor(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            InputStream is = process.getInputStream();
            input = new Scanner(is);
            while (input.hasNextLine()) {
                result += input.nextLine() + "\n";
            }
            result = command + "\n" + result; //加上命令本身，打印出来
        } finally {
            if (input != null) {
                input.close();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    public static List<String> run4List(String command, String prefix) {
        List<String> results = new ArrayList<>();
        Scanner input = null;
        Process process = null;
        try {
            String cmds[] = {"sh", "-c", command};
            process = Runtime.getRuntime().exec(cmds);
            try {
                //等待命令执行完成
                process.waitFor(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("command process waitFor faild:", e);
            }
            InputStream is = process.getInputStream();
            input = new Scanner(is);
            while (input.hasNextLine()) {
                String result = input.nextLine();
                if (!result.contains("Unknown")) {
                    result = StringUtils.isNoneBlank(prefix) ? result.substring(result.indexOf(prefix) + 1) : result;
                    results.add(result.replace("MHz", "").replace("MT/s", "").trim());
                }
            }
        } catch (IOException e) {
            log.error("command execute faild:", e);
        } finally {
            if (input != null) {
                input.close();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return results;
    }

}