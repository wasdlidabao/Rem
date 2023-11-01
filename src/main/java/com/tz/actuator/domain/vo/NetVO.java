package com.tz.actuator.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lxn
 * @date 2022年12月06日 17:55
 */
@Data
@Accessors(chain = true)
public class NetVO {

    String txPercent;
    String rxPercent;
    String curRate;
    String netUsage;
    String netMax;

}
