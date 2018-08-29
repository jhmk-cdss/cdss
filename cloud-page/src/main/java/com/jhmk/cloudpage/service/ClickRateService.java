package com.jhmk.cloudpage.service;

import com.jhmk.cloudentity.page.bean.ClickRate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ziyu.zhou
 * @date 2018/8/29 16:47
 */
public class ClickRateService {

    //分隔符
    private static final String SPLITSYMPOL = "&&&";
    public static Map<String, Integer> clickRateMap;

    public static Map<String, Integer> initMaps() {
        clickRateMap = new ConcurrentHashMap<>();
        return clickRateMap;
    }

    /**
     * 将数据保存到map中
     *
     * @param clickRate
     */
    public static void addDate2Map(ClickRate clickRate) {
        if (clickRateMap==null){
            initMaps();
        }
        // 封装用户统计的request，并且用hash算法分布到不同的队列当中
        String doctorId = clickRate.getDoctorId();
        String type = clickRate.getType();
        String createTime = clickRate.getCreateTime();
        String key = doctorId + SPLITSYMPOL + type + SPLITSYMPOL + createTime;
        if (clickRateMap.containsKey(key)) {
            Integer integer = clickRateMap.get(key);
            clickRateMap.put(key, integer + 1);
        } else {
            clickRateMap.put(key, 1);
        }
    }


}