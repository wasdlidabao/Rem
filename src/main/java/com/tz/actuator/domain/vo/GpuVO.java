package com.tz.actuator.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class GpuVO {

    String name;
    String totalMemory;
    String usedMemory;
    String freeMemory;
    String usageRate;
    List<GpuProcessInfo> gpuProcessInfos;

    @Override
    public String toString() {
        return "GPUInfo{" +
                "name='" + name + '\'' +
                ", totalMemory='" + totalMemory + '\'' +
                ", usedMemory='" + usedMemory + '\'' +
                ", freeMemory='" + freeMemory + '\'' +
                ", usageRate=" + usageRate +
                ", gpuProcessInfos=" + gpuProcessInfos +
                '}';
    }
}