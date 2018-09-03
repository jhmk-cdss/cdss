package com.jhmk.cloudpage.service;

import com.jhmk.cloudentity.earlywaring.entity.SmHospitalLog;
import com.jhmk.cloudentity.page.bean.ClickRate;
import com.jhmk.cloudentity.page.service.ClickRateRepService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Map;

/**
 * @author ziyu.zhou
 * @date 2018/8/29 17:23
 */

@Component
public class SchedulerTask {
    static final Logger logger = LoggerFactory.getLogger(SchedulerTask.class);

    @Autowired
    ClickRateRepService clickRateRepService;

    /**
     * 23时59分59秒执行次任务
     * 统计医生点击cdss小界面次数入库
     */
    @Scheduled(cron = "59 59 23 * * ?")
    public void addClickCount2DateTable() {
        Map<String, Integer> clickRateMap = ClickRateService.clickRateMap;
        for (Map.Entry<String, Integer> entry : clickRateMap.entrySet()) {
            Integer value = entry.getValue();
            String key = entry.getKey();
            String[] split = key.split("&&&");
            if (split.length == 3) {
                String doctorId = split[0];
                String type = split[1];
                String createTime = split[2];
                ClickRate clickRate = new ClickRate();
                clickRate.setCount(value);
                clickRate.setDoctorId(doctorId);
                clickRate.setType(type);
                clickRate.setCreateTime(createTime);
                ClickRate old = clickRateRepService.findByDoctorIdAndCreateTimeAndType(doctorId, createTime, type);
                if (old != null) {
                    old.setCount(old.getCount() + value);
                    clickRateRepService.save(old);
                } else {
                    clickRateRepService.save(clickRate);
                }
            } else {
                logger.info("添加统计次数出现错误，数据为：{}", key);
            }
            ClickRateService.initMaps();
            System.out.println("end================");
        }
    }
}
