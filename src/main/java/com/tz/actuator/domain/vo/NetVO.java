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
@ApiModel(value = "网络")
@Accessors(chain = true)
public class NetVO {

    @ApiModelProperty(name = "txPercent", value = "上行速率")
    String txPercent;
    @ApiModelProperty(name = "rxPercent", value = "下行速率")
    String rxPercent;
    @ApiModelProperty(name = "speed", value = "当前吞吐速率")
    String curRate;
    @ApiModelProperty(name = "speed", value = "网络带宽使用率")
    String netUsage;
    @ApiModelProperty(name = "netMax", value = "网卡上限")
    String netMax;

}
