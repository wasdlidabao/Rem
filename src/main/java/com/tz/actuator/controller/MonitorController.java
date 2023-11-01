package com.tz.actuator.controller;

import com.tz.actuator.domain.vo.ServerVO;
import com.tz.actuator.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxn
 * @date 2022年12月06日 16:16
 */
@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    MonitorService monitorService;

    @GetMapping("/getByActuator")
    public ResponseEntity<ServerVO> getByActuator() {
        return ResponseEntity.ok(monitorService.getByActuator());
    }

}
