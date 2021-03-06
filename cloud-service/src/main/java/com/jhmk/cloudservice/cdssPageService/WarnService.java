package com.jhmk.cloudservice.cdssPageService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jhmk.cloudentity.common.JiaheRuleBean;
import com.jhmk.cloudentity.earlywaring.entity.repository.service.RuyuanjiluRepService;
import com.jhmk.cloudentity.earlywaring.entity.repository.service.YizhuRepService;
import com.jhmk.cloudentity.earlywaring.entity.rule.*;
import com.jhmk.cloudservice.warnService.service.*;
import com.jhmk.cloudutil.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author ziyu.zhou
 * @date 2019/1/11 13:40
 * 提醒页面服务层
 */

@Service
public class WarnService {
    private static final Logger logger = LoggerFactory.getLogger(WarnService.class);

    @Autowired
    JianyanbaogaoService jianyanbaogaoService;
    @Autowired
    JianchabaogaoService jianchabaogaoService;
    @Autowired
    YizhuRepService yizhuRepService;
    @Autowired
    YizhuService yizhuService;
    @Autowired
    RuyuanjiluRepService ruyuanjiluRepService;
    @Autowired
    RuyuanjiluService ruyuanjiluService;
    @Autowired
    RuleService ruleService;

    public Rule getRule(String data, String hospital) {
        JSONObject object = JSONObject.parseObject(data);
        String pageSource = object.getString("pageSource");//页面来源
        if (StringUtils.isEmpty(pageSource) || "test".equals(pageSource)) {
            Map<String, String> paramMap = (Map) JSON.parse(data);
            String s = ruleService.anaRule(paramMap);
            String replace = StringUtil.stringTransform(s);
            JSONObject parse = JSONObject.parseObject(replace);
            Rule rule = Rule.fill(parse);
            return rule;
        } else if ("6".equals(pageSource)) {//医嘱 为6 其他做下诊断处理
            List<Yizhu> yizhus = yizhuService.getYizhu(data, hospital);
            if (Objects.nonNull(object)) {
                String patient_id = object.getString("patient_id");
                String visit_id = object.getString("visit_id");
                String doctor_id = object.getString("doctor_id");
                Rule rule = ruleService.getDiagnoseFromDatabase(patient_id, visit_id);
                rule.setYizhu(yizhus);
                rule.setDoctor_id(doctor_id);
                //获取 拼接检验检查报告
                List<Jianyanbaogao> jianyanbaogao = jianyanbaogaoService.getJianyanbaogao(rule, hospital);
                rule.setJianyanbaogao(jianyanbaogao);
                List<Jianchabaogao> jianchabaogao = jianchabaogaoService.getJianchabaogao(rule, hospital);
                rule.setJianchabaogao(jianchabaogao);
                return rule;
            }
        } else {
            Map<String, String> parse = (Map) JSONObject.parse(data);
            String s = ruleService.anaRule(parse);
            JSONObject object1 = JSONObject.parseObject(s);
            //解析一诉五史
            Rule rule = Rule.fill(object1);
            //获取 拼接检验检查报告
            List<Jianyanbaogao> jianyanbaogao = jianyanbaogaoService.getJianyanbaogao(rule, hospital);
            rule.setJianyanbaogao(jianyanbaogao);
            List<Jianchabaogao> jianchabaogao = jianchabaogaoService.getJianchabaogao(rule, hospital);
            rule.setJianchabaogao(jianchabaogao);
            List<Yizhu> yizhuList = yizhuService.getYizhu(parse, hospital);
            rule.setYizhu(yizhuList);
            return rule;
        }
        return null;
    }

    public Rule getRuleFromETL(String data, String hospital) {
        JSONObject object = JSONObject.parseObject(data);
        String pageSource = object.getString("pageSource");//页面来源
        if (StringUtils.isEmpty(pageSource) || "test".equals(pageSource)) {
            Map<String, String> paramMap = (Map) JSON.parse(data);
            String s = ruleService.anaRule(paramMap);
            String replace = StringUtil.stringTransform(s);
            JSONObject parse = JSONObject.parseObject(replace);
            Rule rule = Rule.fill(parse);
            return rule;
        } else if ("6".equals(pageSource)) {//医嘱 为6 其他做下诊断处理
            List<Yizhu> yizhus = yizhuService.getYizhu(data, hospital);
            if (Objects.nonNull(object)) {
                String patient_id = object.getString("patient_id");
                String visit_id = object.getString("visit_id");
                String doctor_id = object.getString("doctor_id");
                Rule rule = ruleService.getDiagnoseFromDatabase(patient_id, visit_id);
                rule.setYizhu(yizhus);
                rule.setDoctor_id(doctor_id);
                //获取 拼接检验检查报告
                List<Jianyanbaogao> jianyanbaogao = jianyanbaogaoService.getJianyanbaogao(rule, hospital);
                rule.setJianyanbaogao(jianyanbaogao);
                List<Jianchabaogao> jianchabaogao = jianchabaogaoService.getJianchabaogao(rule, hospital);
                rule.setJianchabaogao(jianchabaogao);
                return rule;
            }
        } else {
            Map<String, String> parse = (Map) JSONObject.parse(data);
            String s = ruleService.anaRule(parse);
            JSONObject object1 = JSONObject.parseObject(s);
            //解析一诉五史
            Rule rule = Rule.fill(object1);
            //获取 拼接检验检查报告
            List<Jianyanbaogao> jianyanbaogao = jianyanbaogaoService.getJianyanbaogao(rule, hospital);
            rule.setJianyanbaogao(jianyanbaogao);
            List<Jianchabaogao> jianchabaogao = jianchabaogaoService.getJianchabaogao(rule, hospital);
            rule.setJianchabaogao(jianchabaogao);
            List<Yizhu> yizhuList = yizhuService.getYizhu(parse, hospital);
            rule.setYizhu(yizhuList);
            return rule;
        }
        return null;
    }

    /**
     * @param data 原始数据
     * @param form emr厂商  分为嘉和和其他
     * @return
     */
    public JiaheRuleBean analyzeEmrData2Jiaherulebean(String data, String form, String ip, int port) {
        JiaheRuleBean jiaheRuleBean = new JiaheRuleBean();
        JSONObject parse = JSONObject.parseObject(data);
        Binganshouye binganshouye = parse.getObject("binganshouye", Binganshouye.class);
        jiaheRuleBean.setBinganshouye(binganshouye);
        String wenshuxinxi = parse.getString("wenshuxinxi");
        JSONObject wenshuxinxiJsonObj = parse.getJSONObject("wenshuxinxi");
        String key = wenshuxinxiJsonObj.getString("mr_class_code");
        Ruyuanjilu ruyuanjilu = null;
        if (StringUtils.isNotBlank(key) && "EMR09.00.01".equals(key)) {
            //调用亚飞分词接口
           String participleStringResult = ruyuanjiluService.getParticipleStringResult(wenshuxinxi, null);
            ruyuanjilu = ruyuanjiluService.analyzeParticipleResult2Ruyuanjilu(participleStringResult, null);
            jiaheRuleBean.setRuyuanjilu(ruyuanjilu);
            //更新入院记录
//            ruyuanjiluService.saveAndFlush(rule);
        } else {
            ruyuanjilu = ruyuanjiluService.getRuyuanjilu(jiaheRuleBean, ip, port);
            jiaheRuleBean.setRuyuanjilu(ruyuanjilu);
        }
        return jiaheRuleBean;
    }


}
