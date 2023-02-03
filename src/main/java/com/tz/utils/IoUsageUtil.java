package com.tz.utils;

import com.tz.actuator.domain.vo.DiskSpaceVO;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 采集磁盘IO使用率
 */
@Slf4j
public class IoUsageUtil {

    private static final IoUsageUtil INSTANCE = new IoUsageUtil();

    private IoUsageUtil() {

    }

    public static IoUsageUtil getInstance() {
        return INSTANCE;
    }

    /**
     * @return float, 磁盘IO使用率, 小于1
     * @Purpose:采集磁盘IO使用率
     */
    public List<DiskSpaceVO.DiskSpaceIOVO> get() {
        List<DiskSpaceVO.DiskSpaceIOVO> diskSpaceVOList = new ArrayList<>();
        //float ioUsage = 0.0f;
        Process pro = null;
        Runtime r = Runtime.getRuntime();
        try {
            String[] command = {"sh", "-c", "iostat -d -x -k"};
            pro = r.exec(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = null;
            int count = 0;
            while ((line = in.readLine()) != null) {
                if (++count >= 4 && !line.contains("Device")) {
                    //log.info(line);
                    String[] temp = line.split("\\s+");
                    //for (String s : temp) {                        log.info("s:" + s);                    }
                    if (temp.length > 12) {
                        String kbReadPerSecond = temp[5];
                        String kbWrtnPerSecond = temp[6];
                        String util = temp[13];
                        String svctm = temp[12];
                        String rAwait = temp[10];
                        String wAwait = temp[11];
                        String aquSz = temp[8];
                        //float util =  Float.parseFloat(temp[temp.length-1]);
                        //ioUsage = Math.max(ioUsage, util);
                        DiskSpaceVO.DiskSpaceIOVO diskSpaceIOVO = new DiskSpaceVO.DiskSpaceIOVO();
                        diskSpaceIOVO.setKbReadPerSecond(kbReadPerSecond);
                        diskSpaceIOVO.setKbWrtnPerSecond(kbWrtnPerSecond);
                        diskSpaceIOVO.setUtil(util);
                        diskSpaceIOVO.setAquSz(aquSz);
                        diskSpaceIOVO.setSvctm(svctm);
                        diskSpaceIOVO.setRAwait(rAwait);
                        diskSpaceIOVO.setWAwait(wAwait);
                        diskSpaceVOList.add(diskSpaceIOVO);
                    }
                }
            }
            in.close();
            pro.destroy();
        } catch (Exception e) {
            log.error("收集磁盘IO使用率", e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            log.error("IoUsage发生InstantiationException. " + e.getMessage());
        }
        return diskSpaceVOList;
    }

}