package com.tz.actuator.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lxn
 * @date 2022年12月06日 17:55
 */
@Data
@Accessors(chain = true)
public class MemoryVO {

    String memoryCommitted;
    String memoryMax;
    String memoryUsed;
    String memoryLeft;

    String memorySpeed;

    public void setMemorySpeed(String memorySpeed) {
        this.memorySpeed = memorySpeed + "MT/s";
    }
}
