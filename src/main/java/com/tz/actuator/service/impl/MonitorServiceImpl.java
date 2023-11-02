package com.tz.actuator.service.impl;

import com.tz.actuator.domain.vo.*;
import com.tz.actuator.service.MonitorService;
import com.tz.config.FilePathsConfig;
import com.tz.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
//import com.sun.management.OperatingSystemMXBean;
//import oshi.SystemInfo;
//import oshi.hardware.CentralProcessor;
//import java.io.File;
//import java.lang.management.ManagementFactory;
//import java.text.DecimalFormat;
//import java.util.OptionalDouble;
//import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MonitorServiceImpl implements MonitorService {

    @Autowired
    private FilePathsConfig filePathsConfig;

    @Override
    public ServerVO getByActuator() {
        AtomicReference<TopVO> cpuCompletableFuture = new AtomicReference<>();
        AtomicReference<FreeVO> memoryCompletableFuture = new AtomicReference<>();
        AtomicReference<List<FileSystemVO>> filesystemCompletableFuture = new AtomicReference<>();
        AtomicReference<List<GpuVO>> gpuCompletableFuture = new AtomicReference<>();
        AtomicReference<NetVO> netCompletableFuture = new AtomicReference<>();
        Thread cpuThread = new Thread(() ->
                cpuCompletableFuture.set(buildTop())
        );
        cpuThread.start();
        Thread memoryThread = new Thread(() ->
                memoryCompletableFuture.set(buildFree())
        );
        memoryThread.start();
        Thread fileSystemThread = new Thread(() ->
                filesystemCompletableFuture.set(buildDiskSpace())
        );
        fileSystemThread.start();
        Thread gpuThread = new Thread(() ->
                gpuCompletableFuture.set(buildGup())
        );
        gpuThread.start();
        Thread netThread = new Thread(() ->
                netCompletableFuture.set(buildNet())
        );
        netThread.start();
        try {
            cpuThread.join();
            memoryThread.join();
            fileSystemThread.join();
            gpuThread.join();
            netThread.join();
        } catch (InterruptedException e) {

        }
        /*CompletableFuture<CpuVO> cpuCompletableFuture = CompletableFuture.supplyAsync(this::buildCpu);
        CompletableFuture<MemoryVO> memoryCompletableFuture = CompletableFuture.supplyAsync(this::buildMemory);
        CompletableFuture<List<GPUVO>> gpuCompletableFuture = CompletableFuture.supplyAsync(this::buildGup);
        CompletableFuture<NetVO> netCompletableFuture = CompletableFuture.supplyAsync(this::buildNet);
        CompletableFuture.allOf(cpuCompletableFuture, memoryCompletableFuture, gpuCompletableFuture, netCompletableFuture).join();*/

        try {
            return new ServerVO()
                    .setCpu(cpuCompletableFuture.get())
                    .setMemory(memoryCompletableFuture.get())
                    .setFileSystems(filesystemCompletableFuture.get())
                    .setGpus(gpuCompletableFuture.get())
                    .setNet(netCompletableFuture.get());
        } catch (Exception e) {
            log.error("数据异常", e);
        }
        return new ServerVO();
    }

    private TopVO buildTop() {
        return CommandUtil.runTop();
    }

    private FreeVO buildFree() {
        return CommandUtil.runFree();
    }

    /*private CpuVO buildCpu() {
        SystemInfo systemInfo = new SystemInfo();
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 睡眠1s
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {

        }
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        String cpuCounts = String.valueOf(processor.getLogicalProcessorCount());
        String cpuUsages = new DecimalFormat("#.##%").format(1.0 - (idle * 1.0 / totalCpu));

        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        while (currentGroup.getParent() != null) {
            // 返回此线程组的父线程组
            currentGroup = currentGroup.getParent();
        }
        //此线程组中活动线程的估计数
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];
        //把对此线程组中的所有活动子组的引用复制到指定数组中。
        currentGroup.enumerate(lstThreads);
        *//*for (Thread thread : lstThreads) {
            log.info("线程数量：" + noThreads + " 线程id：" + thread.getId() + " 线程名称：" + thread.getName() + " 线程状态：" + thread.getState());
        }*//*

        int cpuProcess = CpuProcessUtil.GetprocessNums("");

        String cpuMHzAverage = dealCpuMHzAverage();
        CpuVO cpuVO = new CpuVO();
        cpuVO.setCpuCount(cpuCounts);
        cpuVO.setCpuUsage(cpuUsages);
        cpuVO.setCpuProcess(String.valueOf(cpuProcess));
        cpuVO.setThreadsLive(String.valueOf(noThreads));
        cpuVO.setCpuMHz(cpuMHzAverage);
        return cpuVO;
    }

    private MemoryVO buildMemory() {
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long totalPhysicalMemorySize = osmxb.getTotalPhysicalMemorySize();
        long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();
        MemoryVO memoryVO = new MemoryVO();
        memoryVO.setMemoryMax(new DecimalFormat("#.##").format(totalPhysicalMemorySize * 1.00 / 1024 / 1024 / 1024) + "GB");
        memoryVO.setMemoryUsed(new DecimalFormat("#.##").format((totalPhysicalMemorySize - freePhysicalMemorySize) * 1.00 / 1024 / 1024 / 1024) + "GB");
        memoryVO.setMemoryLeft(new DecimalFormat("#.##").format(freePhysicalMemorySize * 1.00 / 1024 / 1024 / 1024) + "GB");
        memoryVO.setMemorySpeed(dealMemoryDeviceSpeed());
        return memoryVO;
    }*/

    private List<FileSystemVO> buildDiskSpace() {
        List<FileSystemVO> fileSystemVOList = new ArrayList<>();
        filePathsConfig.getPaths().forEach(s -> {
            FileSystemVO fileSystemVO = CommandUtil.runDFParam(s);
            if (ObjectUtils.isNotEmpty(fileSystemVO)) {
                fileSystemVOList.add(fileSystemVO);
            }
        });
        return fileSystemVOList;
    }

    /*private DiskSpaceVO buildDiskSpace2() {
        File[] files = File.listRoots();
        long total = 0, free = 0, un = 0;
        for (File file : files) {
            total += file.getTotalSpace();
            free += file.getFreeSpace();
            un += file.getUsableSpace();
        }
        String diskspaceTotal = new DecimalFormat("#.#").format(total * 1.0 / 1024 / 1024 / 1024) + "GB";
        String diskspaceFree = new DecimalFormat("#.#").format(free * 1.0 / 1024 / 1024 / 1024) + "GB";
        String diskspaceUsage = new DecimalFormat("#.#").format(un * 1.0 / 1024 / 1024 / 1024) + "GB";
        List<DiskSpaceVO.DiskSpaceIOVO> diskSpaceIOVOs = IoUsageUtil.getInstance().get();
        //OptionalDouble average = diskSpaceIOVOs.stream().map(DiskSpaceVO.DiskSpaceIOVO::getUtil).map(s -> s.replace("%", "")).mapToDouble(Double::valueOf).average();
        return new DiskSpaceVO().setTotal(diskspaceTotal).setFree(diskspaceFree).setUsed(diskspaceUsage).setDiskSpaceIOVOs(diskSpaceIOVOs);
    }*/

    private List<GpuVO> buildGup() {
        Optional<List<GpuVO>> gpuVO = GpuUtil.getGpuInfo();
        return gpuVO.orElse(null);
    }

    private NetVO buildNet() {
        NetVO v = NetUsageUtil.getInstance().samplingCalculation();
        NetVO networkDownUp = NetWorkUtil.getNetworkDownUp();
        networkDownUp.setCurRate(v.getCurRate()).setNetUsage(v.getNetUsage()).setNetMax(v.getNetMax());
        return networkDownUp;
    }

    /*private String dealCpuMHzAverage() {
        List<String> cpuMHz = CommandUtil.run4List("cat /proc/cpuinfo | grep \"cpu MHz\"", ":");
        OptionalDouble average = cpuMHz.stream().mapToDouble(Double::valueOf).average();
        return average.isPresent() ? NumberFormatUtil.update(average.getAsDouble()) : "0";
    }

    private String dealMemoryDeviceSpeed() {
        List<String> cpuMHz = CommandUtil.run4List("dmidecode | grep -A16 \"Memory Device\" | grep \"Speed\"", ":");
        OptionalDouble average = cpuMHz.stream().mapToDouble(Double::valueOf).average();
        return average.isPresent() ? NumberFormatUtil.update(average.getAsDouble()) : "0";
    }*/

}
