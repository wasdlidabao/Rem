package com.tz.actuator.domain.param;

import com.tz.actuator.domain.vo.ServerVO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lxn
 * @date 2022年12月07日 16:35
 */
@Data
@Accessors(chain = true)
public class RequestServerParam {
    String mac;
    ServerVO serverVO;
}
