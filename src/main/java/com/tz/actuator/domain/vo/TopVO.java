package com.tz.actuator.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TopVO {
    // 5.9%us — 用户空间占用CPU的百分比。
    private String us;
    // 3.4% sy — 内核空间占用CPU的百分比。
    private String sy;
    // 0.0% ni — 改变过优先级的进程占用CPU的百分比
    private String ni;
    // 90.4% id — 空闲CPU百分比
    private String id;
    // 0.0% wa — IO等待占用CPU的百分比
    private String wa;
    // 0.0% hi — 硬中断（Hardware IRQ）占用CPU的百分比
    private String hi;
    // 0.2% si — 软中断（Software Interrupts）占用CPU的百分比
    private String si;
    private String st;
    /**
     * Tasks: 1330 total,   1 running, 1022 sleeping,   0 stopped,   0 zombie
     * 系统现在共有1330个进程，其中处于运行中的有1个，1022个在休眠（sleep），stoped状态的有0个，zombie状态（僵尸）的有0个。
     */
    private String taskTotal;
    private String taskRunning;
    private String taskSleeping;
    private String taskStopped;
    private String taskZombie;
    /**
     * 内存
     */
    private String memTotal;
    private String memFree;
    private String memUsed;
    private String memCache;

}
