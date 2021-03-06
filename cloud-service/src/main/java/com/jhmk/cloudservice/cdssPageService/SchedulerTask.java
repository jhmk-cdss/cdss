package com.jhmk.cloudservice.cdssPageService;

import com.alibaba.fastjson.JSONObject;
import com.jhmk.cloudentity.earlywaring.entity.SmDepts;
import com.jhmk.cloudentity.earlywaring.entity.SmUsers;
import com.jhmk.cloudentity.earlywaring.entity.repository.service.SmDeptsRepService;
import com.jhmk.cloudentity.earlywaring.entity.repository.service.SmUsersRepService;
import com.jhmk.cloudentity.page.bean.ClickRate;
import com.jhmk.cloudentity.page.bean.repository.service.ClickRateRepService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.Map;

/**
 * @author ziyu.zhou
 * @date 2018/8/29 17:23
 */

@Component
public class SchedulerTask {
    static final Logger logger = LoggerFactory.getLogger(SchedulerTask.class);

    @Autowired
    SmDeptsRepService smDeptsRepService;
    @Autowired
    SmUsersRepService smUsersRepService;
    @Autowired
    ClickRateRepService clickRateRepService;

    /**
     * 23时59分59秒执行次任务
     * 统计医生点击cdss小界面次数入库
     */
    @Scheduled(cron = "59 59 23 * * ?")
    public void addClickCount2DateTable() {
        Map<String, Integer> clickRateMap = ClickRateService.clickRateMap;
        logger.info("点击事件数量{}", clickRateMap.size());
        for (Map.Entry<String, Integer> entry : clickRateMap.entrySet()) {
            Integer value = entry.getValue();
            String key = entry.getKey();
            String[] split = key.split("&&&");
            if (split.length == 3) {
                String doctorId = split[0];
                String type = split[1];
                String createTime = split[2];
                Date createDate = Date.valueOf(createTime);
                ClickRate clickRate = new ClickRate();
                clickRate.setCount(value);
                clickRate.setDoctorId(doctorId);
                clickRate.setType(type);
                clickRate.setCreateTime(createDate);
                SmUsers one = smUsersRepService.findOne(doctorId);
                //科室code码
                if (one != null) {
                    String deptCode = one.getUserDept();
                    clickRate.setDeptCode(deptCode);
                    SmDepts firstByDeptCode = smDeptsRepService.findFirstByDeptCode(deptCode);
                    if (firstByDeptCode != null) {
                        String deptName = firstByDeptCode.getDeptName();
                        logger.info("部门名称：{}", deptName);
                        clickRate.setDeptName(deptName);
                    } else {
                        clickRate.setDeptName("空");
                    }
                }
                ClickRate old = clickRateRepService.findByDoctorIdAndCreateTimeAndType(doctorId, createDate, type);
                if (old != null) {
                    if (StringUtils.isEmpty(old.getDeptName())) {
                        String deptCode = one.getUserDept();
                        old.setDeptCode(deptCode);
                        SmDepts firstByDeptCode = smDeptsRepService.findFirstByDeptCode(deptCode);
                        String deptName = firstByDeptCode.getDeptName();
                        logger.info("部门名称：{}", deptName);
                        old.setDeptName(deptName);
                    }
                    old.setCount(old.getCount() + value);
                    logger.info("保存点击事件：{}", JSONObject.toJSONString(clickRate));
                    clickRateRepService.save(old);
                } else {
                    logger.info("保存点击事件：{}", JSONObject.toJSONString(clickRate));
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
