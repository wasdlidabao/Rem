package com.tz.utils;

import com.tz.actuator.domain.vo.GpuVO;
import com.tz.actuator.domain.vo.GpuProcessInfo;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author lxn
 * @date 2022年12月07日 16:05
 */
@Slf4j
public class GpuUtil {

    /**
     * 获取gpu信息
     *
     * @return gpu信息集合
     */
    public static Optional<List<GpuVO>> getGpuInfo() {
        try {
            String gpuXmlInfo = getGpuXmlInfo();
            if (StringUtils.isNoneBlank(gpuXmlInfo)) {
                List<GpuVO> gpuInfos = convertXmlToGpuObject(gpuXmlInfo);
                return Optional.of(gpuInfos);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("获取gpu信息error , message : {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * 通过命令xml格式显卡信息
     *
     * @return xml字符串
     * @throws IOException 获取显卡信息错误
     */
    public static String getGpuXmlInfo() throws IOException {
        Process process;
        String result = "";
        String command[] = {"sh", "-c", "nvidia-smi -q -x"};
        process = Runtime.getRuntime().exec(command);
        try (InputStream inputStream = process.getInputStream()) {
            result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }

        if (process.isAlive()) {
            process.destroy();
        }
        return result;
    }

    private static final String REG = "<!DOCTYPE.*.dtd\">";

    /**
     * 获取gpu信息（暂时只支持nvidia-smi）
     *
     * @return gpu信息集合
     * @throws DocumentException xml解析错误
     */
    public static List<GpuVO> convertXmlToGpuObject(String xmlGpus) throws DocumentException {
        //忽略dtd
        xmlGpus = xmlGpus.replaceAll(REG, "");
        Document document = DocumentHelper.parseText(xmlGpus);
        List<Element> gpu = document.getRootElement().elements("gpu");
        List<GpuVO> gpuInfoList = new ArrayList<>();
        gpu.forEach(element -> {
            GpuVO gpuInfo = new GpuVO();
            String uuid = element.element("uuid").getText();
            Element fbMemoryUsage = element.element("fb_memory_usage");
            String total = fbMemoryUsage.element("total").getText().replace(" ", "");
            String used = fbMemoryUsage.element("used").getText().replace(" ", "");
            String free = fbMemoryUsage.element("free").getText().replace(" ", "");
            gpuInfo.setTotalMemory(total);
            gpuInfo.setUsedMemory(used);
            gpuInfo.setFreeMemory(free);
            gpuInfo.setName(uuid);
            Element processes = element.element("processes");
            List<Element> infos = processes.elements("process_info");
            List<GpuProcessInfo> gpuProcessInfos = new ArrayList<>();
            infos.forEach(info -> {
                GpuProcessInfo gpuProcessInfo = new GpuProcessInfo();
                String pid = info.element("pid").getText();
                String name = info.element("process_name").getText();
                String usedMemory = info.element("used_memory").getText().trim();
                gpuProcessInfo.setPid(pid);
                gpuProcessInfo.setName(name);
                gpuProcessInfo.setUsedMemory(usedMemory);
                gpuProcessInfos.add(gpuProcessInfo);
            });
            gpuInfo.setGpuProcessInfos(gpuProcessInfos);
            int intTotal = Integer.parseInt(total.replace("MiB", "").split(" ")[0]);
            int intUsed = Integer.parseInt(used.replace("MiB", "").split(" ")[0]);
            gpuInfo.setUsageRate((int) ((float) intUsed / intTotal * 100) + "%");
            gpuInfoList.add(gpuInfo);
        });
        return gpuInfoList;
    }

}
