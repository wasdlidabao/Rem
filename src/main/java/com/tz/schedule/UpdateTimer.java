package com.tz.schedule;

import com.tz.actuator.domain.param.RequestServerParam;
import com.tz.actuator.domain.vo.ServerVO;
import com.tz.actuator.service.MonitorService;
import com.tz.utils.CommandUtil;
import com.tz.utils.MacTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
public class UpdateTimer {

    @Autowired
    MonitorService monitorService;
    @Autowired
    RestTemplate httpRestTemplate;
    @Value("${timer.url}")
    String url;

    @Scheduled(cron = "${timer.upload}")
    public void uploadLog() {
        try {
            List<String> ips = CommandUtil.run4List("hostname -I | awk '{print $1}'", null);
            log.info("ip:" + ips.get(0));
            List<String> macList = MacTools.getMacList(ips.get(0));
            //String macAddr = ComputerInfo.getMacAddress();
            log.info("mac地址:" + macList.get(0));
            ServerVO serverVO = monitorService.getByActuator();

            RequestServerParam param = new RequestServerParam();
            param.setMac(macList.get(0)).setServerVO(serverVO);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RequestServerParam> entity = new HttpEntity<>(param, httpHeaders);
            httpRestTemplate.postForEntity(url + "/server/uploadData", entity, String.class);
        } catch (Exception e) {
            log.error("定时上传数据", e);
        }
    }

}