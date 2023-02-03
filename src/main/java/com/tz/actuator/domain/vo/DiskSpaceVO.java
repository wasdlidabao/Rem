package com.tz.actuator.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 磁盘
 *
 * @author lxn
 * @date 2022年12月07日 13:53
 */
@Data
@Accessors(chain = true)
public class DiskSpaceVO {

    @ApiModelProperty(name = "total", value = "")
    String total;
    @ApiModelProperty(name = "free", value = "可用")
    String free;
    @ApiModelProperty(name = "used", value = "已用")
    String used;
    @ApiModelProperty(name = "diskSpaceIOVOs", value = "io相关")
    List<DiskSpaceIOVO> diskSpaceIOVOs;

    @Data
    @Accessors(chain = true)
    @ApiModel(value = "磁盘读写")
    public static class DiskSpaceIOVO {

        @ApiModelProperty(name = "kbReadPerSecond", value = "每秒读取的磁盘块的数量")
        String kbReadPerSecond;
        @ApiModelProperty(name = "kbWrtnPerSecond", value = "每秒写入的磁盘块的数量")
        String kbWrtnPerSecond;
        @ApiModelProperty(name = "svctm", value = "平均每次设备I/O操作的服务时间")
        String svctm;
        @ApiModelProperty(name = "rAwait", value = "平均每次设备I操作的等待时间")
        String rAwait;
        @ApiModelProperty(name = "wAwait", value = "平均每次设备O操作的等待时间")
        String wAwait;
        @ApiModelProperty(name = "aquSz", value = "平均I/O队列长度")
        String aquSz;
        @ApiModelProperty(name = "util", value = "传输率")
        String util;

        public void setUtil(String util) {
            this.util = util + "%";
        }
    }

}
