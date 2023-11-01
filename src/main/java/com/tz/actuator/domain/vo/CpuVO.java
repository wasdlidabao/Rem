package com.tz.actuator.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lxn
 * @date 2022年12月06日 17:55
 */
@Data
@Accessors(chain = true)
public class CpuVO {

    String cpuCount;
    String cpuUsage;

    String threadsLive;
    String threadsStates;
    String threadsPeak;

    String cpuProcess;
    String cpuMHz;

    public void setCpuMHz(String cpuMHz) {
        this.cpuMHz = cpuMHz + "MHz";
    }
}
