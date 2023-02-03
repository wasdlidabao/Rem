package com.tz.actuator.domain.vo;

import lombok.Data;

@Data
public class GpuProcessInfo {

    String pid;
    String name;
    String usedMemory;

}