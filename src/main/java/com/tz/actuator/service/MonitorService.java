package com.tz.actuator.service;

import com.tz.actuator.domain.vo.ServerVO;

public interface MonitorService {

    /**
     * actuator结果
     *
     * @return ServerVO
     * @date 2022/12/8 11:24
     */
    ServerVO getByActuator();

}
