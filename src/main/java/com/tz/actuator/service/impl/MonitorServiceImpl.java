package com.tz.actuator.service.impl;

import com.sun.management.OperatingSystemMXBean;
import com.tz.actuator.domain.vo.*;
import com.tz.actuator.service.MonitorService;
import com.tz.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class MonitorServiceImpl implements MonitorService {

    @Override
    public ServerVO getByActuator() {
        AtomicReference<CpuVO> cpuCompletableFuture = new AtomicReference<>();
        AtomicReference<MemoryVO> memoryCompletableFuture = new AtomicReference<>();
        AtomicReference<List<GpuVO>> gpuCompletableFuture = new AtomicReference<>();
        AtomicReference<NetVO> netCompletableFuture = new AtomicReference<>();
        Thread cpuThread = new Thread(() ->
                cpuCompletableFuture.set(buildCpu())
        );
        cpuThread.start();
        Thread memoryThread = new Thread(() ->
                memoryCompletableFuture.set(buildMemory())
        );
        memoryThread.start();
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
                    .setDiskSpace(buildDiskSpace())
                    .setGpus(gpuCompletableFuture.get())
                    .setNet(netCompletableFuture.get());
        } catch (Exception e) {
            log.error("????????????", e);
        }
        return new ServerVO().setDiskSpace(buildDiskSpace());
    }

    private CpuVO buildCpu() {
        SystemInfo systemInfo = new SystemInfo();
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // ??????1s
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
            // ?????????????????????????????????
            currentGroup = currentGroup.getParent();
        }
        //???????????????????????????????????????
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];
        //??????????????????????????????????????????????????????????????????????????????
        currentGroup.enumerate(lstThreads);
        /*for (Thread thread : lstThreads) {
            log.info("???????????????" + noThreads + " ??????id???" + thread.getId() + " ???????????????" + thread.getName() + " ???????????????" + thread.getState());
        }*/

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
    }

    private DiskSpaceVO buildDiskSpace() {
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
    }

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

    private String dealCpuMHzAverage() {
        List<String> cpuMHz = CommandUtil.run4List("cat /proc/cpuinfo | grep \"cpu MHz\"", ":");
        OptionalDouble average = cpuMHz.stream().mapToDouble(Double::valueOf).average();
        return average.isPresent() ? NumberFormatUtil.update(average.getAsDouble()) : "0";
    }

    private String dealMemoryDeviceSpeed() {
        List<String> cpuMHz = CommandUtil.run4List("dmidecode | grep -A16 \"Memory Device\" | grep \"Speed\"", ":");
        OptionalDouble average = cpuMHz.stream().mapToDouble(Double::valueOf).average();
        return average.isPresent() ? NumberFormatUtil.update(average.getAsDouble()) : "0";
    }

}
