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
@ApiModel(value = "内存")
@Accessors(chain = true)
public class MemoryVO {

    @ApiModelProperty(name = "memoryCommitted", value = "提交的内存（以字节为单位）")
    String memoryCommitted;
    @ApiModelProperty(name = "memoryMax", value = "最大内存（以字节为单位）")
    String memoryMax;
    @ApiModelProperty(name = "memoryUsed", value = "已用内存（以字节为单位）")
    String memoryUsed;
    @ApiModelProperty(name = "memoryLeft", value = "可用内存（以字节为单位）")
    String memoryLeft;

    @ApiModelProperty(name = "memorySpeed", value = "速度（自带单位）")
    String memorySpeed;

    public void setMemorySpeed(String memorySpeed) {
        this.memorySpeed = memorySpeed + "MT/s";
    }
}
