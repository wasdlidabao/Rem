package com.tz.actuator.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author lxn
 * @date 2022年12月07日 16:35
 */
@Data
@Accessors(chain = true)
public class ServerVO {

    TopVO cpu;
    FreeVO memory;
    List<FileSystemVO> fileSystems;
    List<GpuVO> gpus;
    NetVO net;
}
