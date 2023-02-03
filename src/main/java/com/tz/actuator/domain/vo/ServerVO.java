package com.tz.actuator.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author lxn
 * @date 2022年12月07日 16:35
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "服务器性能")
public class ServerVO {

    @ApiModelProperty(name = "cpu", value = "cpu")
    CpuVO cpu;
    @ApiModelProperty(name = "memory", value = "内存")
    MemoryVO memory;
    @ApiModelProperty(name = "diskSpace", value = "磁盘")
    DiskSpaceVO diskSpace;
    @ApiModelProperty(name = "gpus", value = "gpu")
    List<GpuVO> gpus;
    @ApiModelProperty(name = "net", value = "网络")
    NetVO net;

}
