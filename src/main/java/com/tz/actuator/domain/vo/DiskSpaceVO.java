package com.tz.actuator.domain.vo;

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

    String total;
    String free;
    String used;
    List<DiskSpaceIOVO> diskSpaceIOVOs;

    @Data
    @Accessors(chain = true)
    public static class DiskSpaceIOVO {

        String kbReadPerSecond;
        String kbWrtnPerSecond;
        String svctm;
        String rAwait;
        String wAwait;
        String aquSz;
        String util;

        public void setUtil(String util) {
            this.util = util + "%";
        }
    }

}
