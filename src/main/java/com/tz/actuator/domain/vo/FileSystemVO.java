package com.tz.actuator.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 磁盘
 *
 * @author lxn
 * @date 2022年12月07日 13:53
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class FileSystemVO {

    /**
     * 文件系统
     */
    String filesystem;
    /**
     * 总大小
     */
    String size;
    /**
     * 已使用
     */
    String used;
    /**
     * 可用空间
     */
    String avail;
    /**
     * 使用率
     */
    String use;
    /**
     * 挂载点
     */
    String mountedOn;

    @Override
    public String toString() {
        return "FileSystemVO{" +
                "filesystem='" + filesystem + '\'' +
                ", size='" + size + '\'' +
                ", used='" + used + '\'' +
                ", avail='" + avail + '\'' +
                ", use='" + use + '\'' +
                ", mountedOn='" + mountedOn + '\'' +
                '}';
    }
}
