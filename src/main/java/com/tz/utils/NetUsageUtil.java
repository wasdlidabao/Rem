package com.tz.utils;

import com.tz.actuator.domain.vo.NetVO;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 采集网络带宽使用率
 * <p>
 * ****************************
 * cat /proc/net/dev
 * Inter-|   Receive                                                |  Transmit
 * face |bytes    packets errs drop fifo frame compressed multicast|bytes    packets errs drop fifo colls carrier compressed
 * lo: 341566047 4020765    0    0    0     0          0         0 341566047 4020765    0    0    0     0       0          0
 * enp0s31f6: 7222870592 9003564    0    0    0     0          0    125359 680013336 4352932    0    0    0     0       0          0
 * <p>
 * ****************************
 * ethtool enp0s31f6
 * Settings for enp0s31f6:
 * Supported ports: [ TP ]
 * Supported link modes:   10baseT/Half 10baseT/Full
 * 100baseT/Half 100baseT/Full
 * 1000baseT/Full
 * Supported pause frame use: No
 * Supports auto-negotiation: Yes
 * Advertised link modes:  10baseT/Half 10baseT/Full
 * 100baseT/Half 100baseT/Full
 * 1000baseT/Full
 * Advertised pause frame use: No
 * Advertised auto-negotiation: Yes
 * Speed: 1000Mb/s
 * Duplex: Full
 * Port: Twisted Pair
 * PHYAD: 1
 * Transceiver: internal
 * Auto-negotiation: on
 * MDI-X: on (auto)
 */
@Slf4j
public class NetUsageUtil {
    private static NetUsageUtil instance = new NetUsageUtil();
    private static Runtime r = Runtime.getRuntime();
    private static double bandWidth = 1000;
    private static final String NET_COMMAND = "cat /proc/net/dev";
    private static final String NET_NAME = "en";
    private static final String SPEED = "Speed";
    private static final String ETH_COMMAND = "ethtool " + NET_NAME;

    private NetUsageUtil() {

    }

    public static NetUsageUtil getInstance() {
        return instance;
    }

    static {
        java.lang.Process process = null;
        try {
            process = r.exec(ETH_COMMAND);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(SPEED)) {
                    String[] temp = line.split("\\s+");
                    bandWidth = Double.parseDouble(temp[1].split("Mb/s")[0]);
                    break;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        process.destroy();
    }

    private static class Net {
        private Long time;
        private Long inSize;
        private Long outSize;

        public Net() {
            this.time = System.currentTimeMillis();
            this.inSize = 0L;
            this.outSize = 0L;
        }

        public Long getTime() {
            return time;
        }

        public void setTime(Long time) {
            this.time = time;
        }

        public Long getInSize() {
            return inSize;
        }

        public void setInSize(Long inSize) {
            this.inSize = inSize;
        }

        public Long getOutSize() {
            return outSize;
        }

        public void setOutSize(Long outSize) {
            this.outSize = outSize;
        }
    }

    public NetVO samplingCalculation() {
        Net start = new Net();
        Net end = new Net();

        try {
            //第一次采集流量数据
            takeInAndOutSize(start);

            //间隔 1s
            Thread.sleep(1000);

            //第二次采集流量数据
            takeInAndOutSize(end);

            //计算传输指标
            return compute(start, end);

        } catch (Exception e) {
            log.error("网速计算异常:", e);
        }
        return null;
    }

    public NetVO compute(Net start, Net end) {
        double netUsage;
        double interval = (float) (end.getTime() - start.getTime()) / 1000;
        double curRate = (float) (Math.abs(end.getInSize() - start.getInSize()) + Math.abs(end.getOutSize() - start.getOutSize())) / (131072 * interval);
        netUsage = curRate / bandWidth;
        NetVO net = new NetVO();
        net.setNetUsage(NumberFormatUtil.update(netUsage * 100) + "%").setCurRate(NumberFormatUtil.update(curRate) + "Mbps").setNetMax(bandWidth + "Mb/s");
        return net;
    }

    public void takeInAndOutSize(Net net) throws IOException {
        net.setTime(System.currentTimeMillis());
        String command[] = {"sh", "-c", NET_COMMAND};
        Process process = r.exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(NET_NAME)) {
                String[] temp = line.split("\\s+");
                if (Long.parseLong(temp[1]) > 0) {
                    net.setInSize(Long.parseLong(temp[1]));
                    net.setOutSize(Long.parseLong(temp[9]));
                    break;
                }
            }
        }
        reader.close();
        process.destroy();
    }

}

