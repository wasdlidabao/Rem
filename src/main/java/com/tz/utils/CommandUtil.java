package com.tz.utils;

import com.tz.actuator.domain.vo.FileSystemVO;
import com.tz.actuator.domain.vo.FreeVO;
import com.tz.actuator.domain.vo.TopVO;
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

    public static List<FileSystemVO> runDiskSpace4List() {
        List<FileSystemVO> results = new ArrayList<>();
        Scanner input = null;
        Process process = null;
        try {
            String cmds[] = {"sh", "-c", "df -h"};
            process = Runtime.getRuntime().exec(cmds);
            try {
                //等待命令执行完成
                process.waitFor(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("command process waitFor faild:", e);
            }
            InputStream is = process.getInputStream();
            input = new Scanner(is);
            while (input.hasNextLine()) {
                String result = input.nextLine();
                String[] parts = result.split("\\s+"); // 使用空格作为分隔符拆分每一行数据
                String fileSystem = parts[0];
                String size = parts[1];
                String used = parts[2];
                String available = toGB(parts[3]) + "G";
                String usePercentage = parts[4];
                String mountPoint = parts[5];
                if (!StringUtils.equals(fileSystem, "Filesystem")) {
                    // 对解析的数据进行进一步处理或存储
                    results.add(new FileSystemVO(fileSystem, size, used, available, usePercentage, mountPoint));
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
        results.forEach(System.out::println);
        return results;
    }

    public static FileSystemVO runDFParam(String param) {
        FileSystemVO fileSystemVO = null;
        Scanner input = null;
        Process process = null;
        try {
            String cmds = String.format("df -h %s", param);
            process = Runtime.getRuntime().exec(cmds);
            try {
                //等待命令执行完成
                process.waitFor(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("command process waitFor faild:", e);
            }
            InputStream is = process.getInputStream();
            input = new Scanner(is);
            while (input.hasNextLine()) {
                String result = input.nextLine();
                String[] parts = result.split("\\s+"); // 使用空格作为分隔符拆分每一行数据
                String fileSystem = parts[0];
                String size = parts[1];
                String used = parts[2];
                String available = toGB(parts[3]) + "G";
                String usePercentage = parts[4];
                String mountPoint = parts[5];
                if (!StringUtils.equals(fileSystem, "Filesystem")) {
                    // 对解析的数据进行进一步处理或存储
                    fileSystemVO = new FileSystemVO(fileSystem, size, used, available, usePercentage, param);
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
        return fileSystemVO;
    }

    public static TopVO runTop() {
        TopVO topVO = new TopVO();
        Scanner input = null;
        Process process = null;
        try {
            String cmds[] = {"sh", "-c", "top -b -n 1"};
            process = Runtime.getRuntime().exec(cmds);
            try {
                //等待命令执行完成
                process.waitFor(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("command process waitFor faild:", e);
            }
            InputStream is = process.getInputStream();
            input = new Scanner(is);
            while (input.hasNextLine()) {
                String result = input.nextLine();
                if (result.contains("Tasks: ")) {
                    String[] parts = result.split("\\s+"); // 使用空格作为分隔符拆分每一行数据
                    topVO.setTaskTotal(parts[1])
                            .setTaskRunning(parts[3])
                            .setTaskSleeping(parts[5])
                            .setTaskStopped(parts[7])
                            .setTaskZombie(parts[9]);
                } else if (result.contains("%Cpu(s)")) {
                    result = result.substring(result.lastIndexOf("%Cpu(s): "));
                    String[] parts = result.split("\\s+"); // 使用空格作为分隔符拆分每一行数据
                    topVO.setUs(parts[1])
                            .setSy(parts[3])
                            .setNi(parts[5])
                            .setId(parts[7])
                            .setWa(parts[9])
                            .setHi(parts[11])
                            .setSi(parts[13]);
                } else if (result.contains("KiB Mem : ")) {
                    result = result.substring(result.lastIndexOf(":"));
                    String[] split = result.split(",");
                    topVO.setMemTotal(split[0].replace(" ", "").replace(":", "").replace("total", "").replace("free", "").replace("used", "").replace("buff/cache", ""))
                            .setMemFree(split[1].replace(" ", "").replace(":", "").replace("total", "").replace("free", "").replace("used", "").replace("buff/cache", ""))
                            .setMemUsed(split[2].replace(" ", "").replace(":", "").replace("total", "").replace("free", "").replace("used", "").replace("buff/cache", ""))
                            .setMemCache(split[3].replace(" ", "").replace(":", "").replace("total", "").replace("free", "").replace("used", "").replace("buff/cache", ""));
                }
            }
            log.info(topVO.toString());
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
        return topVO;
    }

    public static FreeVO runFree() {
        FreeVO freeVO = new FreeVO();
        Scanner input = null;
        Process process = null;
        try {
            String cmds[] = {"sh", "-c", "free -h"};
            process = Runtime.getRuntime().exec(cmds);
            try {
                //等待命令执行完成
                process.waitFor(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("command process waitFor faild:", e);
            }
            InputStream is = process.getInputStream();
            input = new Scanner(is);
            while (input.hasNextLine()) {
                String result = input.nextLine();
                if (result.contains("Mem:")) {
                    String[] parts = result.split("\\s+"); // 使用空格作为分隔符拆分每一行数据
                    freeVO.setTotal(parts[1])
                            .setUsed(parts[2])
                            // 需要处理单位,默认统一为GB
                            .setFree(toGB(parts[3]) + "G")
                            .setCache(parts[5])
                            .setAvailable(parts[6]);
                }
            }
            log.info(freeVO.toString());
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
        return freeVO;
    }

    // KiB Mem : 32825272 total,  2301564 free, 14787824 used, 15735884 buff/cache
    // KiB Mem : 39472169+total, 22054708+free, 82035488 used, 92139104 buff/cache
    public static void main2(String[] args) {
        String result1 = "KiB Mem : 32825272 total,  2301564 free, 14787824 used, 15735884 buff/cache";
        String result2 = "KiB Mem : 39472169+total, 22054708+free, 82035488 used, 92139104 buff/cache";
        print(result1);
        log.info("-------------");
        print(result2);
    }

    private static void print(String result) {
        result = result.substring(result.lastIndexOf(" : "));
        String[] split = result.split(",");
        for (String s : split) {
            log.info(s.replace(" ", "").replace(":", "").replace("total", "").replace("free", "").replace("used", "").replace("buff/cache", ""));
        }
    }

    private static String toGB(String resource) {
        if (StringUtils.isBlank(resource)) return null;
        String firstStr = resource.substring(0, resource.length() - 1);
        String lastStr = resource.substring(resource.length() - 1);
        switch (lastStr) {
            case "T":
                firstStr = String.valueOf((Float.valueOf(firstStr)) * 1024);
                break;
            case "G":
                break;
            case "K":
                firstStr = String.valueOf((Float.valueOf(firstStr)) / 1024);
                break;
            case "M":
                firstStr = String.valueOf((Float.valueOf(firstStr)) / (1024 * 2));
                break;
            case "B":
                firstStr = String.valueOf((Float.valueOf(firstStr)) / (1024 * 3));
                break;
            case "i":
                firstStr = resource.substring(0, resource.length() - 2);
                lastStr = resource.substring(resource.length() - 2);
                switch (lastStr) {
                    case "Ti":
                        firstStr = String.valueOf((Float.valueOf(firstStr)) * 1024);
                        break;
                    case "Gi":
                        break;
                    case "Ki":
                        firstStr = String.valueOf((Float.valueOf(firstStr)) / 1024);
                        break;
                    case "Mi":
                        firstStr = String.valueOf((Float.valueOf(firstStr)) / (1024 * 2));
                        break;
                    case "Bi":
                        firstStr = String.valueOf((Float.valueOf(firstStr)) / (1024 * 3));
                        break;
                    default:
                }
                break;
            default:
        }
        return firstStr;
    }
}