package com.tz.actuator.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "gpu")
public class GpuVO {

    @ApiModelProperty(name = "name", value = "名称")
    String name;
    @ApiModelProperty(name = "totalMemory", value = "总内存")
    String totalMemory;
    @ApiModelProperty(name = "usedMemory", value = "已用内存")
    String usedMemory;
    @ApiModelProperty(name = "freeMemory", value = "空闲内存")
    String freeMemory;
    @ApiModelProperty(name = "usageRate", value = "使用率 整形，最大为100")
    String usageRate;
    @ApiModelProperty(name = "gpuProcessInfos", value = "进程信息")
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