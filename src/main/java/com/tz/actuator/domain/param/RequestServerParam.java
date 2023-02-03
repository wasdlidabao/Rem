package com.tz.actuator.domain.param;

import com.tz.actuator.domain.vo.ServerVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lxn
 * @date 2022年12月07日 16:35
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "服务器性能")
public class RequestServerParam {
    @ApiModelProperty(name = "mac", value = "mac地址")
    String mac;
    @ApiModelProperty(name = "serverVO", value = "资源监控数据")
    ServerVO serverVO;
}
