package com.jhmk.cloudservice.warnService.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jhmk.cloudentity.base.BaseRepService;
import com.jhmk.cloudentity.earlywaring.entity.LogBean;
import com.jhmk.cloudentity.earlywaring.entity.SmDepts;
import com.jhmk.cloudentity.earlywaring.entity.SmHospitalLog;
import com.jhmk.cloudentity.earlywaring.entity.SmUsers;
import com.jhmk.cloudentity.earlywaring.entity.repository.service.SmDeptsRepService;
import com.jhmk.cloudentity.earlywaring.entity.repository.service.SmHospitalLogRepService;
import com.jhmk.cloudentity.earlywaring.entity.repository.service.SmUsersRepService;
import com.jhmk.cloudentity.earlywaring.entity.rule.Binganshouye;
import com.jhmk.cloudentity.earlywaring.entity.rule.Binglizhenduan;
import com.jhmk.cloudentity.earlywaring.entity.rule.Rule;
import com.jhmk.cloudentity.earlywaring.entity.rule.Shouyezhenduan;
import com.jhmk.cloudutil.config.UrlPropertiesConfig;
import com.jhmk.cloudutil.model.WebPage;
import com.jhmk.cloudutil.util.CompareUtil;
import com.jhmk.cloudutil.util.DateFormatUtil;
import com.jhmk.cloudutil.util.ObjectUtils;
import com.jhmk.cloudutil.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class HosptailLogService extends BaseRepService<SmHospitalLog, Integer> {

    static final Logger logger = LoggerFactory.getLogger(SmHospitalLog.class);


    @Autowired
    SmHospitalLogRepService smHospitalLogRepService;
    @Autowired
    RuleService ruleService;
    @Autowired
    SmDeptsRepService smDeptRepService;

    @Autowired
    SmUsersRepService smUsersRepService;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    UrlPropertiesConfig urlPropertiesConfig;

    private static String blue = "蓝色预警";
    private static String red = "红色预警";
    private static String orange = "橙色预警";
    private static String yellow = "黄色预警";

    private static void addToMap(HashMap map, Class[] array) {


        for (int i = 0; i < array.length; i++) {
            String name = array[i].getName();
            map.put(name.substring(name.lastIndexOf(".") + 1, name.length()), array[i]);
        }
    }


    public Map<String, Integer> getMap(String deptId, int year) {
        Date startTime = DateFormatUtil.getYearFirst(year);
        Date endTime = DateFormatUtil.getYearLast(year);
        Map<String, Integer> map = new HashMap<>();
        //根据部门查询所有log
        List<SmHospitalLog> hosptailLogList = smHospitalLogRepService.getAllByDeptAndYear(deptId, startTime, endTime);
        if (hosptailLogList.size() > 0) {
            for (SmHospitalLog log : hosptailLogList) {
                //将log 的key：value  主疾病 ：次数
                if (map.containsKey(log.getDiagnosisName())) {
                    map.put(log.getDiagnosisName(), map.get(log.getDiagnosisName()) + 1);
                } else {
                    map.put(log.getDiagnosisName(), 1);
                }
            }
        }
        return map;

    }

    /**
     * 触发疾病统计
     *
     * @param deptId
     * @param year
     * @return
     */
    public Map<String, Integer> getCountByDeptAndSickness(String deptId, int year) {
        Date startTime = DateFormatUtil.getYearFirst(year);
        Date endTime = DateFormatUtil.getYearLast(year);
        //用于分页  相当于limit（0,10）
        List<Object[]> list = smHospitalLogRepService.getCountByDiagnosisNameAndDeptCode(deptId, startTime, endTime);
        Map<String, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Object[] objects = list.get(i);
            map.put(objects[0].toString(), Integer.valueOf(objects[1].toString()));
        }
        Map<String, Integer> map1 = CompareUtil.compareMapValue(map);

        return map1;

    }


    //科室分布功能
    public List<LogBean> countByDept(int year) {
        Map<String, LogBean> map = new HashMap<>();
        List<SmHospitalLog> logList = smHospitalLogRepService.getDeptCountByYear(DateFormatUtil.getYearFirst(year), DateFormatUtil.getYearLast(year));
        for (SmHospitalLog SmHospitalLog : logList) {
            if (map.containsKey(SmHospitalLog.getDeptCode())) {
                LogBean bean = map.get(SmHospitalLog.getDeptCode());
                if (blue.equals(SmHospitalLog.getAlarmLevel())) {
                    bean.setBlue(bean.getBlue() + 1);
                } else if (yellow.equals(SmHospitalLog.getAlarmLevel())) {
                    bean.setYellow(bean.getYellow() + 1);
                } else if (orange.equals(SmHospitalLog.getAlarmLevel())) {
                    bean.setOrange(bean.getOrange() + 1);
                } else if (red.equals(SmHospitalLog.getAlarmLevel())) {
                    bean.setRed(bean.getRed() + 1);
                } else {
                    logger.info("此规则无预警等级" + SmHospitalLog.toString());
                }
                bean.setCount(bean.getCount() + 1);
                map.put(SmHospitalLog.getDeptCode(), bean);
            } else {
                LogBean bean = new LogBean();
//                bean.setDoctorId(SmHospitalLog.getDoctorId());
//                bean.setDoctorName(SmHospitalLog.getDoctorName());
                bean.setDeptId(SmHospitalLog.getDeptCode());
                if (blue.equals(SmHospitalLog.getAlarmLevel())) {
                    bean.setBlue(1);
                } else if (yellow.equals(SmHospitalLog.getAlarmLevel())) {
                    bean.setYellow(1);
                } else if (orange.equals(SmHospitalLog.getAlarmLevel())) {
                    bean.setOrange(1);
                } else if (red.equals(SmHospitalLog.getAlarmLevel())) {
                    bean.setRed(1);
                } else {
                    logger.info("此规则无预警等级" + SmHospitalLog.toString());
                }
                bean.setCount(bean.getCount() + 1);
                map.put(SmHospitalLog.getDeptCode(), bean);
            }
        }
        List<LogBean> resultList = new ArrayList<>();
        for (Map.Entry<String, LogBean> entry : map.entrySet()) {
            resultList.add(entry.getValue());
        }
        Collections.sort(resultList, CompareUtil.createComparator(-1, "count"));

        return resultList;
    }

    public List<String> mapDeptNames() {
        List<String> countByDistinctDeptCode = smHospitalLogRepService.getCountByDistinctDeptCode();
        return countByDistinctDeptCode;
    }


    //人员分布功能
    public List<LogBean> countByDoctor(String deptId, int year) {
        Map<String, LogBean> map = new HashMap<>();
        List<SmHospitalLog> logList = smHospitalLogRepService.getAllByDeptAndYear(deptId, DateFormatUtil.getYearFirst(year), DateFormatUtil.getYearLast(year));
        for (SmHospitalLog SmHospitalLog : logList) {
            if (map.containsKey(SmHospitalLog.getDoctorId())) {
                LogBean bean = map.get(SmHospitalLog.getDoctorId());
                if (blue.equals(SmHospitalLog.getAlarmLevel())) {
                    bean.setBlue(bean.getBlue() + 1);
                } else if (yellow.equals(SmHospitalLog.getAlarmLevel())) {
                    bean.setYellow(bean.getYellow() + 1);
                } else if (orange.equals(SmHospitalLog.getAlarmLevel())) {
                    bean.setOrange(bean.getOrange() + 1);
                } else if (red.equals(SmHospitalLog.getAlarmLevel())) {
                    bean.setRed(bean.getRed() + 1);
                } else {
                    logger.info("此规则无预警等级" + SmHospitalLog.toString());
                }
                bean.setCount(bean.getCount() + 1);
                map.put(SmHospitalLog.getDoctorId(), bean);
            } else {
                LogBean bean = new LogBean();
                bean.setDoctorId(SmHospitalLog.getDoctorId());
                bean.setDoctorName(SmHospitalLog.getDoctorName());
                bean.setDeptId(SmHospitalLog.getDeptCode());
                if (blue.equals(SmHospitalLog.getAlarmLevel())) {
                    bean.setBlue(1);
                } else if (yellow.equals(SmHospitalLog.getAlarmLevel())) {
                    bean.setYellow(1);
                } else if (orange.equals(SmHospitalLog.getAlarmLevel())) {
                    bean.setOrange(1);
                } else if (red.equals(SmHospitalLog.getAlarmLevel())) {
                    bean.setRed(1);
                } else {
                    logger.info("此规则无预警等级" + SmHospitalLog.toString());
                }
                bean.setCount(bean.getCount() + 1);
                map.put(SmHospitalLog.getDoctorId(), bean);
            }
        }
        List<LogBean> resultList = new ArrayList<>();
        for (Map.Entry<String, LogBean> entry : map.entrySet()) {
            resultList.add(entry.getValue());
        }
        Collections.sort(resultList, CompareUtil.createComparator(-1, "count"));

        return resultList;
    }


    /**
     * 临床预警 数据统计 全院数据科室分析之科室门诊数和住院数
     *
     * @param startTime
     * @param endTime
     * @param deptName  父dept Name
     * @return
     */
    public String countForDate(String startTime, String endTime, String deptName) {
        Map<String, Object> params = new HashMap<>();
        if (deptName != null) {
            params.put("deptCode", deptName);
        }
        //发送
        Specification sf = getWhereClause(startTime, endTime, params);

        List<SmHospitalLog> all = smHospitalLogRepService.findAll(sf);
        return "";

    }

    //住院预警次数
    public List<LogBean> countForField(String startTime, String endTime, String deptName, String field) {
        Map<String, Object> params = new HashMap<>();
        if (deptName != null) {
            params.put("deptCode", deptName);
        }
        params.put("warnSource", field);

        //发送
        Specification sf = getWhereClause(startTime, endTime, params);

        List<SmHospitalLog> all = smHospitalLogRepService.findAll(sf);
        List<String> monthBetween = DateFormatUtil.getMonthBetween(startTime, endTime);

        Map<String, Integer> map = new HashMap<>();
        for (String s : monthBetween) {
            map.put(s, 0);
        }
        for (SmHospitalLog SmHospitalLog : all) {
            Date createTime = SmHospitalLog.getCreateTime();
            String format = DateFormatUtil.format(createTime, "yyyy-MM");
            if (map.containsKey(format)) {
                map.put(format, map.get(format) + 1);
            }
        }
        List<LogBean> beanList = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            LogBean logBean = new LogBean();
            logBean.setTime(entry.getKey());
            logBean.setCount(entry.getValue());
            beanList.add(logBean);
        }
        Collections.sort(beanList, new Comparator<LogBean>() {
            @Override
            public int compare(LogBean o1, LogBean o2) {
                String time1 = o1.getTime();
                String time2 = o2.getTime();
                Date date1 = DateFormatUtil.parseDateBySdf(time1, DateFormatUtil.DATE_PATTERN_MM);
                Date date2 = DateFormatUtil.parseDateBySdf(time2, DateFormatUtil.DATE_PATTERN_MM);
                if (date1.getTime() > date2.getTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return beanList;

    }

    //门诊预警次数
    public String countForClinic(String startTime, String endTime, String deptName) {
        Map<String, Object> params = new HashMap<>();
        if (deptName != null) {
            params.put("deptCode", deptName);
        }
        params.put("warnSource", "门诊");

        //发送
        Specification sf = getWhereClause(startTime, endTime, params);

        List<SmHospitalLog> all = smHospitalLogRepService.findAll(sf);
        return "";

    }

    public List<SmHospitalLog> getDataByCondition(Date start, Date end, Map param, Integer pageNo, Integer pageSize) {
        int page = 0;
        if (pageNo != null) {
            // Pageable页面从0开始计
            page = pageNo;
        }
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");

        WebPage webPage = new WebPage();
        if (pageSize != null) {
            webPage.setPageSize(pageSize);
        }

        Pageable pageable = new PageRequest(page, webPage.getPageSize(), sort);
        Specification sf = getWhereClause(start, end, param);
//        List<SmHospitalLog> all = smHospitalLogRepService.findAll(sf, pageable);
//        List<SmHospitalLog> all = smHospitalLogRepService.findAllByOrderByCreateTimeDesc(sf);
        Page<SmHospitalLog> pageCount = smHospitalLogRepService.findAll(sf, pageable);
        List<SmHospitalLog> all = pageCount.getContent();
        for (SmHospitalLog l : all) {
            Date createTime = l.getCreateTime();
            l.setRuledate(DateFormatUtil.format(createTime, DateFormatUtil.DATETIME_PATTERN_SS));
        }

        return all;
    }

    public List<SmHospitalLog> getDataByConditionBySort(Date start, Date end, Map param, Integer pageNo, Integer pageSize) {
        int page = 0;
        if (pageNo != null) {
            // Pageable页面从0开始计
            page = pageNo;
        }
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");

        Specification sf = getWhereClause(start, end, param);
        List<SmHospitalLog> all = smHospitalLogRepService.findAll(sf, sort);
        for (SmHospitalLog l : all) {
            Date createTime = l.getCreateTime();
            l.setRuledate(DateFormatUtil.format(createTime, DateFormatUtil.DATETIME_PATTERN_SS));
        }
        return all;
    }

    public Specification<T> getWhereClause(String startTime, String endTime, Map<String, Object> params) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();

                if (startTime != null) {
                    String[] split = startTime.split("-");
                    String firstDayOfMonth = DateFormatUtil.getFirstDayOfMonth(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
                    Date strarDate = DateFormatUtil.parseDate(firstDayOfMonth, DateFormatUtil.DATE_PATTERN_S);
                    list.add(cb.greaterThanOrEqualTo(root.get("createTime").as(Date.class), strarDate));

                }
                if (endTime != null) {
                    String[] split = endTime.split("-");
                    String lastDayOfMonth = DateFormatUtil.getLastDayOfMonth(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
                    Date endDate = DateFormatUtil.parseDate(lastDayOfMonth, DateFormatUtil.DATE_PATTERN_S);
                    list.add(cb.lessThanOrEqualTo(root.get("createTime").as(Date.class), endDate));

                }
//                //拼接传入参数
                if (params != null) {
                    for (String key : params.keySet()) {
                        if (WebPage.PAGE_NUM.equals(key)) {
                            continue;
                        } else {
                            Object value = params.get(key);
                            if (!org.springframework.util.StringUtils.isEmpty(value.toString())) {
                                list.add(cb.equal(root.get(key), value));
                            }
                        }
                    }
                }

                Predicate[] p = new Predicate[list.size()];
                list.toArray(p);
                return cb.and(p);

            }
        };
    }

    public Specification<T> getWhereClause(Date startTime, Date endTime, Map<String, Object> params) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();

                if (startTime != null) {
                    list.add(cb.greaterThanOrEqualTo(root.get("createTime").as(Date.class), startTime));

                }
                if (endTime != null) {
                    list.add(cb.lessThanOrEqualTo(root.get("createTime").as(Date.class), endTime));
                }
//                //拼接传入参数
                if (params != null) {
                    for (String key : params.keySet()) {
                        if (WebPage.PAGE_NUM.equals(key)) {
                            continue;
                        } else {
                            Object value = params.get(key);
                            if (!org.springframework.util.StringUtils.isEmpty(value.toString())) {
                                list.add(cb.equal(root.get(key), value));
                            }
                        }
                    }
                }

                Predicate[] p = new Predicate[list.size()];
                list.toArray(p);
                return cb.and(p);

            }
        };
    }


    public SmHospitalLog addLog(String map) {
        SmHospitalLog smHospitalLog = new SmHospitalLog();
        Map<String, Object> jsonObject = (Map) JSON.parseObject(map);

        Object doctor_id = jsonObject.get("doctor_id");
        smHospitalLog.setDoctorId(ObjectUtils.flagObj(doctor_id));
        Object doctorName = jsonObject.get("doctor_name");
        smHospitalLog.setDoctorName(ObjectUtils.flagObj(doctorName));
        if (StringUtils.isNotBlank(ObjectUtils.flagObj(doctorName))) {

            List<SmUsers> byUserName = smUsersRepService.findByUserName(ObjectUtils.flagObj(doctorName));
            if (byUserName != null && byUserName.size() > 0) {
                smHospitalLog.setDoctorId(byUserName.get(0).getUserId());
            }
        }
        Object deptName = jsonObject.get("dept_code");
        smHospitalLog.setDeptCode(ObjectUtils.flagObj(deptName));
        //病人id
        Object patient_id = jsonObject.get("patient_id");
        smHospitalLog.setPatientId(ObjectUtils.flagObj(patient_id));
        Object visit_id = jsonObject.get("visit_id");
        smHospitalLog.setVisitId(ObjectUtils.flagObj(visit_id));
        Object warnSource = jsonObject.get("warnSource");
        smHospitalLog.setWarnSource(ObjectUtils.flagObj(warnSource));
        Object shouyezhenduan = jsonObject.get("shouyezhenduan");
        Object binglizhenduan = jsonObject.get("binglizhenduan");

        //主诊断
        String affirmSickness = "";
        //判断首页诊断如果不为空，确认主疾病为首页诊断
        if (binglizhenduan != null) {
            JSONArray objects = JSONArray.parseArray(binglizhenduan.toString());
            Iterator<Object> iterator = objects.iterator();
            while (iterator.hasNext()) {
                {
                    Map<String, String> next = (Map) iterator.next();
                    if ("1".equals(next.get("diagnosis_num"))) {
                        Optional.ofNullable(next.get("diagnosis_name")).ifPresent(s -> {
                            smHospitalLog.setDiagnosisName(next.get("diagnosis_name").trim());
                        });
                        Optional.ofNullable(next.get("diagnosis_time")).ifPresent(s -> {
                            smHospitalLog.setCreateTime(DateFormatUtil.parseDate(next.get("diagnosis_time"), DateFormatUtil.DATETIME_PATTERN_SS));
                        });
                    }

                }
            }
        } else {
            JSONArray objects = JSONArray.parseArray(shouyezhenduan.toString());
            Iterator<Object> iterator = objects.iterator();
            while (iterator.hasNext()) {
                {
                    Map<String, String> next = (Map) iterator.next();
                    if ("1".equals(next.get("diagnosis_num"))) {
                        affirmSickness = next.get("diagnosis_name").trim();
                        Optional.ofNullable(next.get("diagnosis_name")).ifPresent(s -> {
                            smHospitalLog.setDiagnosisName(next.get("diagnosis_name").trim());
                        });
                        Optional.ofNullable(next.get("diagnosis_time")).ifPresent(s -> {
                            smHospitalLog.setCreateTime(DateFormatUtil.parseDate(next.get("diagnosis_time"), DateFormatUtil.DATETIME_PATTERN_SS));
                        });
                    }

                }
            }

        }
        return smHospitalLog;

    }

    public SmHospitalLog addLog(Rule rule) {
        SmHospitalLog smHospitalLog = new SmHospitalLog();
        smHospitalLog.setDoctorId(rule.getDoctor_id());
        smHospitalLog.setDoctorName(rule.getDoctor_name());
        Binganshouye binganshouye = rule.getBinganshouye();
        if (binganshouye != null) {
            String deptName = "";
            String pat_visit_dept_admission_to_name = binganshouye.getPat_visit_dept_admission_to_name();
            if (StringUtil.isInteger(pat_visit_dept_admission_to_name)) {
                SmDepts firstByDeptCode = smDeptRepService.findFirstByDeptCode(pat_visit_dept_admission_to_name);
                if (firstByDeptCode != null) {
                    deptName = firstByDeptCode.getDeptName();
                    smHospitalLog.setDeptCode(deptName);
                } else {
                    smHospitalLog.setDeptCode(pat_visit_dept_admission_to_name);
                }
            } else {
                smHospitalLog.setDeptCode(pat_visit_dept_admission_to_name);

            }

        } else {
            smHospitalLog.setDeptCode("测试部门");
        }
        //病人id
        smHospitalLog.setPatientId(rule.getPatient_id());
        smHospitalLog.setVisitId(rule.getVisit_id());
        smHospitalLog.setWarnSource(rule.getWarnSource());
        List<Binglizhenduan> binglizhenduan = rule.getBinglizhenduan();
        List<Shouyezhenduan> shouyezhenduan = rule.getShouyezhenduan();
        //主诊断
        //判断首页诊断如果不为空，确认主疾病为首页诊断
        if (Objects.nonNull(binglizhenduan)) {
            Iterator<Binglizhenduan> iterator = binglizhenduan.iterator();
            while (iterator.hasNext()) {
                {
                    Binglizhenduan next = iterator.next();
                    if ("1".equals(next.getDiagnosis_num())&&("初步诊断".equals(next.getDiagnosis_type_name())||"入院初诊".equals(next.getDiagnosis_type_name()))) {
                        Optional.ofNullable(next.getDiagnosis_name()).ifPresent(s -> {
                            smHospitalLog.setDiagnosisName(next.getDiagnosis_name().trim());
                        });
                        Optional.ofNullable(next.getDiagnosis_time()).ifPresent(s -> {
                            smHospitalLog.setCreateTime(DateFormatUtil.parseDate(next.getDiagnosis_time(), DateFormatUtil.DATETIME_PATTERN_SS));
                        });
                    }

                }
            }
        } else {
            if (Objects.nonNull(binglizhenduan)) {
                Iterator<Shouyezhenduan> iterator = shouyezhenduan.iterator();
                while (iterator.hasNext()) {
                    {
                        Shouyezhenduan next = iterator.next();
                        if ("1".equals(next.getDiagnosis_num())&&"出院诊断".equals(next.getDiagnosis_type_name())) {
                            Optional.ofNullable(next.getDiagnosis_name()).ifPresent(s -> {
                                smHospitalLog.setDiagnosisName(next.getDiagnosis_name().trim());
                            });
                            Optional.ofNullable(next.getDiagnosis_time()).ifPresent(s -> {
                                smHospitalLog.setCreateTime(DateFormatUtil.parseDate(next.getDiagnosis_time(), DateFormatUtil.DATETIME_PATTERN_SS));
                            });
                        }

                    }
                }
            }
        }
        return smHospitalLog;

    }


}

