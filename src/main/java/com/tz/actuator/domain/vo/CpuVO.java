package com.tz.actuator.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lxn
 * @date 2022年12月06日 17:55
 */
@Data
@ApiModel(value = "cpu性能")
@Accessors(chain = true)
public class CpuVO {

    @ApiModelProperty(name = "cpuCount", value = "cpu数量")
    String cpuCount;
    @ApiModelProperty(name = "cpuUsage", value = "cpu使用率")
    String cpuUsage;

    @ApiModelProperty(name = "threadsLive", value = "线程数")
    String threadsLive;
    @ApiModelProperty(name = "threadsStates", value = "BLOCKED线程")
    String threadsStates;
    @ApiModelProperty(name = "threadsPeak", value = "峰值活动线程数")
    String threadsPeak;

    @ApiModelProperty(name = "cpuProcess", value = "进程数")
    String cpuProcess;
    @ApiModelProperty(name = "cpuMHz", value = "速度")
    String cpuMHz;

    public void setCpuMHz(String cpuMHz) {
        this.cpuMHz = cpuMHz + "MHz";
    }
}
