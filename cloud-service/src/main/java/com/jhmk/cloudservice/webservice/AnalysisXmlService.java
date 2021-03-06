package com.jhmk.cloudservice.webservice;

import com.jhmk.cloudentity.earlywaring.entity.rule.Jianyanbaogao;
import com.jhmk.cloudentity.earlywaring.entity.rule.Yizhu;
import com.jhmk.cloudentity.earlywaring.webservice.JianyanbaogaoForAuxiliary;
import com.jhmk.cloudentity.earlywaring.webservice.OriginalJianyanbaogao;
import com.jhmk.cloudutil.util.DateFormatUtil;
import com.jhmk.cloudutil.util.RegularUtils;
import com.jhmk.cloudutil.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ziyu.zhou
 * @date 2018/7/19 18:07
 */
@Service
public class AnalysisXmlService {

    /**
     * 解析病案首页
     *
     * @param str
     * @return
     */
    public Map<String, String> analysisXml2Binganshouye(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        Map<String, String> binganshouye = new HashMap<>();
        try {
            //将&转换为空格，处理解析特殊符号 报错
            Document dom = DocumentHelper.parseText(str.replaceAll("&", ""));
            Element root = dom.getRootElement();
            Element item = root.element("item");
            if (Objects.nonNull(item)) {

                List<Element> items = item.elements();
                Element age_valueElement = item.element("AGE_VALUE");
                if (Objects.nonNull(age_valueElement)) {
                    String text = age_valueElement.getText();
                    String s = RegularUtils.delCC(text);
                    //age
                    binganshouye.put("pat_info_age_value", s);
                }

                Element sex_name = item.element("SEX_NAME");
                if (Objects.nonNull(sex_name)) {
                    String text = sex_name.getText();
                    //性别
                    binganshouye.put("pat_info_sex_name", text);
                }
                Element MARITAL_STATUS_NAME = item.element("MARITAL_STATUS_NAME");
                if (Objects.nonNull(MARITAL_STATUS_NAME)) {
                    String marital_status_nameText = MARITAL_STATUS_NAME.getText();
                    //婚配
                    binganshouye.put("pat_info_marital_status_name", marital_status_nameText);
                }
                Element OCCUPATION_NAME = item.element("OCCUPATION_NAME");
                if (Objects.nonNull(OCCUPATION_NAME)) {
                    String text = OCCUPATION_NAME.getText();
                    //职业
                    binganshouye.put("pat_info_occupation_name", text);
                }
                Element dept_admission_to_name = item.element("DEPT_ADMISSION_TO_NAME");
                if (Objects.nonNull(dept_admission_to_name)) {
                    String text = dept_admission_to_name.getText();
                    //r入院部门
                    binganshouye.put("pat_visit_dept_admission_to_name", text);
                }
                Element dept_admission_to_code = item.element("DEPT_ADMISSION_TO_CODE");
                if (Objects.nonNull(dept_admission_to_code)) {
                    String text = dept_admission_to_code.getText();
                    //入院部门编码
                    binganshouye.put("pat_visit_dept_admission_to_code", text);
                }
                Element dept_discharge_from_name = item.element("DEPT_DISCHARGE_FROM_NAME");
                if (Objects.nonNull(dept_discharge_from_name)) {
                    String text = dept_discharge_from_name.getText();
                    //r入院部门
                    binganshouye.put("pat_visit_dept_discharge_from_name", text);
                }
                Element dept_discharge_from_code = item.element("DEPT_DISCHARGE_FROM_CODE");
                if (Objects.nonNull(dept_discharge_from_code)) {
                    String text = dept_discharge_from_code.getText();
                    //入院部门编码
                    binganshouye.put("pat_visit_dept_discharge_from_code", text);
                }

            }


        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return binganshouye;
    }


    /**
     * 诊断(适用病例诊断、首页诊断)
     *
     * @param str
     * @return
     */
    public List<Map<String, String>> analysisXml2Zhenduan(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        List<Map<String, String>> blzdList = new LinkedList<>();
        try {
            Document dom = DocumentHelper.parseText(str.replaceAll("&", ""));
            Element root = dom.getRootElement();
            List<Element> items = root.elements();
            if (Objects.nonNull(items)) {

//                List<Element> items = item.elements();
                for (int i = 0; i < items.size(); i++) {
                    Map<String, String> blzdMap = new HashMap<>();
                    Element element = items.get(i);
                    //诊断名称
                    Element diagnosis_name = element.element("DIAGNOSIS_NAME");
                    if (Objects.nonNull(diagnosis_name)) {
                        blzdMap.put("diagnosis_name", diagnosis_name.getText());
                    }
                    Element diagnosis_desc = element.element("DIAGNOSIS_DESC");
                    if (Objects.nonNull(diagnosis_desc)) {
                        blzdMap.put("diagnosis_desc", diagnosis_desc.getText());
                    }
                    Element diagnosis_time = element.element("DIAGNOSIS_TIME");
                    if (Objects.nonNull(diagnosis_time)) {
                        blzdMap.put("diagnosis_time", diagnosis_time.getText());
                    }

                    //初步诊断
                    Element diagnosis_property_name = element.element("DIAGNOSIS_PROPERTY_NAME");
                    if (Objects.nonNull(diagnosis_property_name)) {
                        blzdMap.put("diagnosis_type_name", diagnosis_property_name.getText());
                    }

                    //诊断编号
                    Element diagnosis_num = element.element("DIAGNOSIS_NUM");
                    if (Objects.nonNull(diagnosis_num)) {
                        blzdMap.put("diagnosis_num", diagnosis_num.getText());
                    }

                    Element diagnosis_sub_num = element.element("DIAGNOSIS_SUB_NUM");
                    if (Objects.nonNull(diagnosis_sub_num)) {
                        blzdMap.put("diagnosis_sub_num", diagnosis_sub_num.getText());
                    }
                    if (blzdMap.size() != 0) {
                        blzdList.add(blzdMap);
                    }
                }
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return blzdList;
    }

    /**
     * 检查报告
     *
     * @param str
     * @return
     */
    public List<Map<String, String>> analysisXml2Jianchabaogao(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        List<Map<String, String>> jcbgList = new LinkedList<>();
        try {
            Document dom = DocumentHelper.parseText(str.replaceAll("&", ""));
            Element root = dom.getRootElement();
            List<Element> items = root.elements();
            if (Objects.nonNull(items)) {

//                List<Element> items = item.elementsm();
                for (int i = 0; i < items.size(); i++) {
                    Map<String, String> map = new HashMap<>();
                    Element element = items.get(i);
                    //诊断名称
                    Element exam_item_name = element.element("EXAM_ITEM_NAME");
                    if (Objects.nonNull(exam_item_name)) {
                        map.put("exam_item_name", exam_item_name.getText());
                    }
                    //检查诊断
                    Element exam_diag = element.element("EXAM_DIAG");
                    if (Objects.nonNull(exam_diag)) {
                        map.put("exam_diag", exam_diag.getText());
                    }
                    //检查特征
                    Element exam_feature = element.element("EXAM_FEATURE");
                    if (Objects.nonNull(exam_feature)) {
                        map.put("exam_feature", exam_feature.getText());
                    }

                    //检查时间
                    Element report_time = element.element("REPORT_TIME");
                    if (Objects.nonNull(report_time)) {
                        map.put("exam_time", report_time.getText());
                    }

                    if (map.size() != 0) {
                        jcbgList.add(map);
                    }
                }
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return jcbgList;
    }

    /**
     * 检验报告主表
     *
     * @param str                    检验报告主表信息
     * @param analysisXmlServiceList 检验报告所有明细信息
     * @return
     */
    public List<OriginalJianyanbaogao> analysisXml2Jianyanbaogao(String str, List<JianyanbaogaoForAuxiliary> analysisXmlServiceList) {
        List<OriginalJianyanbaogao> jybgList = new LinkedList<>();
        try {
            Document dom = DocumentHelper.parseText(str.replaceAll("&", ""));
            Element root = dom.getRootElement();
            List<Element> items = root.elements();
            if (Objects.nonNull(items)) {

//                List<Element> items = item.elementsm();
                for (int i = 0; i < items.size(); i++) {
                    OriginalJianyanbaogao jianyanbaogao = new OriginalJianyanbaogao();
                    Element element = items.get(i);
                    //检验名
                    Element lab_item_name = element.element("LAB_ITEM_NAME");
                    if (Objects.nonNull(lab_item_name)) {
                        jianyanbaogao.setLab_item_name(lab_item_name.getText());
                    }
                    //样本
                    Element speciman_type_name = element.element("SPECIMAN_TYPE_NAME");
                    if (Objects.nonNull(speciman_type_name)) {
                        jianyanbaogao.setSpecimen(speciman_type_name.getText());
                    }

                    //检查时间
                    Element report_time = element.element("REPORT_TIME");
                    if (Objects.nonNull(report_time)) {
                        jianyanbaogao.setReport_time(report_time.getText());
                    }
                    //检查时间
                    Element report_no = element.element("REPORT_NO");
                    if (Objects.nonNull(report_no)) {
                        jianyanbaogao.setReport_no(report_no.getText());
                        /**
                         *将检验报告明细放入检验报告中
                         */
                        List<JianyanbaogaoForAuxiliary> tempList = new LinkedList<>();
                        for (JianyanbaogaoForAuxiliary bean : analysisXmlServiceList) {
                            if (report_no.getText().equals(bean.getReport_no())) {
                                tempList.add(bean);
                            }
                        }
                        jianyanbaogao.setLabTestItems(tempList);
                    }

                    jybgList.add(jianyanbaogao);
                }
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        //删除重复项 一局spec ,保留最大时间
        Map<String, Optional<OriginalJianyanbaogao>> collect = jybgList.stream().collect(Collectors.groupingBy(OriginalJianyanbaogao::getSpecimen, Collectors.maxBy((o1, o2) -> DateFormatUtil.parseDate(o1.getReport_time(), DateFormatUtil.DATETIME_PATTERN_SS).compareTo(DateFormatUtil.parseDate(o2.getReport_time(), DateFormatUtil.DATETIME_PATTERN_SS)))));
        List<OriginalJianyanbaogao> resultList = new ArrayList<>();
        for (Map.Entry<String, Optional<OriginalJianyanbaogao>> entry : collect.entrySet()) {
            OriginalJianyanbaogao originalJianyanbaogao = entry.getValue().get();
            resultList.add(originalJianyanbaogao);
        }
        return resultList;
    }

    public List<Jianyanbaogao> analysisOriginalJianyanbaogao2Jianyanbaogao(List<OriginalJianyanbaogao> originalJianyanbaogaoList) {
        List<Jianyanbaogao> jybgList = new LinkedList<>();
        for (OriginalJianyanbaogao originalJianyanbaogao : originalJianyanbaogaoList) {
            String lab_item_name = originalJianyanbaogao.getLab_item_name();
            String report_no = originalJianyanbaogao.getReport_no();
            String report_time = originalJianyanbaogao.getReport_time();
            String specimen = originalJianyanbaogao.getSpecimen();
            List<JianyanbaogaoForAuxiliary> labTestItems = originalJianyanbaogao.getLabTestItems();
            for (JianyanbaogaoForAuxiliary jianyanbaogaoForAuxiliary : labTestItems) {
                Jianyanbaogao jianyanbaogao = new Jianyanbaogao();
                jianyanbaogao.setLab_item_name(lab_item_name);
                jianyanbaogao.setReport_no(report_no);
                jianyanbaogao.setReport_time(report_time);
                jianyanbaogao.setSpecimen(specimen);
                String lab_sub_item_name = jianyanbaogaoForAuxiliary.getName();
                jianyanbaogao.setLab_sub_item_name(lab_sub_item_name);
                String lab_qual_result = jianyanbaogaoForAuxiliary.getLab_qual_result();
                jianyanbaogao.setLab_qual_result(lab_qual_result);
                String reference_range = jianyanbaogaoForAuxiliary.getReference_range();
                jianyanbaogao.setReference_range(reference_range);
                String unit = jianyanbaogaoForAuxiliary.getUnit();
                jianyanbaogao.setLab_result_value_unit(unit);
                String lab_result = jianyanbaogaoForAuxiliary.getLab_result();
                if (!StringUtil.isNumber(lab_result)) {
                    continue;
                }
                jianyanbaogao.setLab_result_value(jianyanbaogaoForAuxiliary.getLab_result());
                jybgList.add(jianyanbaogao);
            }
        }

        //检验报告获取最近时间的
        Map<String, Optional<Jianyanbaogao>> collect = jybgList.stream().collect(Collectors.groupingBy(Jianyanbaogao::getIwantData, Collectors.maxBy((o1, o2) -> DateFormatUtil.parseDate(o1.getReport_time(), DateFormatUtil.DATETIME_PATTERN_SS).compareTo(DateFormatUtil.parseDate(o2.getReport_time(), DateFormatUtil.DATETIME_PATTERN_SS)))));
        List<Jianyanbaogao> resultList = new ArrayList<>();
        for (Map.Entry<String, Optional<Jianyanbaogao>> entry : collect.entrySet()) {
            Jianyanbaogao student = entry.getValue().get();
            resultList.add(student);
        }
        return resultList;
    }


    /**
     * 检验报告明细
     *
     * @param str
     * @return
     */
    public List<JianyanbaogaoForAuxiliary> analysisXml2JianyanbaogaoMX(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        List<JianyanbaogaoForAuxiliary> jybgMXList = new LinkedList<>();
        try {
            Document dom = DocumentHelper.parseText(str.replaceAll("&", ""));
            Element root = dom.getRootElement();
            List<Element> items = root.elements();
            if (Objects.nonNull(items)) {

                for (int i = 0; i < items.size(); i++) {
                    JianyanbaogaoForAuxiliary mx = new JianyanbaogaoForAuxiliary();
                    Element element = items.get(i);
                    //检验细项
                    Element lab_sub_item_name = element.element("LAB_SUB_ITEM_NAME");
                    if (Objects.nonNull(lab_sub_item_name)) {
                        mx.setName(lab_sub_item_name.getText());
                    }
                    //定性结果
                    Element lab_qual_result = element.element("LAB_QUAL_RESULT");
                    if (Objects.nonNull(lab_qual_result)) {
                        mx.setLab_qual_result(lab_qual_result.getText());
                    }
                    //检验定量结果值
                    Element lab_result_value = element.element("LAB_RESULT_VALUE");
                    if (Objects.nonNull(lab_result_value)) {
                        mx.setLab_result(lab_result_value.getText());
                    }
                    //检验定量结果单位
                    Element lab_result_unit = element.element("LAB_RESULT_UNIT");
                    if (Objects.nonNull(lab_result_unit)) {
                        mx.setUnit(lab_result_unit.getText());
                    }
                    //检验定量结果单位
                    Element report_no = element.element("REPORT_NO");
                    if (Objects.nonNull(report_no)) {
                        mx.setReport_no(report_no.getText());
                    }

                    jybgMXList.add(mx);
                }
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return jybgMXList;
    }

    /**
     * 获取入院时间 出院时间等
     *
     * @param str
     * @return
     */
    public Map<String, String> getHospitalDate(String str) {
        Map<String, String> rcz = new HashMap<>();
        try {
            Document dom = DocumentHelper.parseText(str.replaceAll("&", ""));
            Element root = dom.getRootElement();
            List<Element> items = root.elements();
            if (Objects.nonNull(items)) {

                for (int i = 0; i < items.size(); i++) {
                    JianyanbaogaoForAuxiliary mx = new JianyanbaogaoForAuxiliary();
                    Element element = items.get(i);
                    //入院时间
                    Element admission_time = element.element("ADMISSION_TIME");
                    if (Objects.nonNull(admission_time)) {
                        rcz.put("admission_time", admission_time.getText());
                    }
                    //出院时间
                    Element discharge_time = element.element("DISCHARGE_TIME");
                    if (Objects.nonNull(discharge_time)) {
                        rcz.put("discharge_time", discharge_time.getText());
                    }

                }
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return rcz;
    }


//    public List<Map<String, String>> analysisXml2Ruyuanjilu(String str) {
//        List<Map<String, String>> ruyuanjiluList = new LinkedList<>();
//        try {
//            Document dom = DocumentHelper.parseText(str.replaceAll("&", ""));
//            Element root = dom.getRootElement();
//            List<Element> items = root.elements();
//            if (Objects.nonNull(items)) {
//
//                for (int i = 0; i < items.size(); i++) {
//                    JianyanbaogaoForAuxiliary mx = new JianyanbaogaoForAuxiliary();
//                    Element element = items.get(i);
//                    Element zs_value = element.element("ZS_VALUE");
//                    if (Objects.nonNull(zs_value)) {
//                        Map<String, String> zs = new HashMap<>();
//                        zs.put("key", "主诉");
//                        zs.put("value", zs_value.getText());
//                        ruyuanjiluList.add(zs);
//                    }
//                    Element xbs_value = element.element("XBS_VALUE");
//
//                    if (Objects.nonNull(xbs_value)) {
//                        Map<String, String> xbs = new HashMap<>();
//                        xbs.put("key", "现病史");
//                        xbs.put("value", xbs_value.getText());
//                        ruyuanjiluList.add(xbs);
//                    }
//                    Element jws_value = element.element("JWS_VALUE");
//                    if (Objects.nonNull(jws_value)) {
//                        Map<String, String> jws = new HashMap<>();
//                        jws.put("key", "既往史");
//                        jws.put("value", jws_value.getText());
//                        ruyuanjiluList.add(jws);
//                    }
//                    Element grs_value = element.element("GRS_VALUE");
//                    if (Objects.nonNull(grs_value)) {
//                        Map<String, String> grs = new HashMap<>();
//                        grs.put("key", "个人史");
//                        grs.put("value", jws_value.getText());
//                        ruyuanjiluList.add(grs);
//                    }
//                    Element jhys_value = element.element("JHYS_VALUE");
//                    if (Objects.nonNull(jhys_value)) {
//                        Map<String, String> grs = new HashMap<>();
//                        grs.put("key", "婚育史");
//                        grs.put("value", jhys_value.getText());
//                        ruyuanjiluList.add(grs);
//                    }
//
//                    Element fzjc_value = element.element("FZJC_VALUE");
//                    if (Objects.nonNull(fzjc_value)) {
//                        Map<String, String> fzjc = new HashMap<>();
//                        fzjc.put("key", "辅助检查");
//                        fzjc.put("value", fzjc_value.getText());
//                        ruyuanjiluList.add(fzjc);
//                    }
//
//                }
//            }
//
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//        return ruyuanjiluList;
//    }

    /**
     * JHHDRWS021B 病历章节（反结构化）获取一诉五史 辅助检查 专科检查
     *
     * @param str
     * @return
     */
    public List<Map<String, String>> analysisXml2Ruyuanjilu(String str) {
        List<Map<String, String>> ruyuanjiluList = new LinkedList<>();
        try {
            Document dom = DocumentHelper.parseText(str.replaceAll("&", ""));
            Element root = dom.getRootElement();
            List<Element> items = root.elements();
            if (Objects.nonNull(items)) {

                for (int i = 0; i < items.size(); i++) {
                    JianyanbaogaoForAuxiliary mx = new JianyanbaogaoForAuxiliary();
                    Element element = items.get(i);
                    Element dg_name = element.element("DG_NAME");
                    Element dg_plain_content_blob = element.element("DG_PLAIN_CONTENT_BLOB");
                    if (Objects.nonNull(dg_name) && Objects.nonNull(dg_plain_content_blob)) {
                        Map<String, String> zs = new HashMap<>();
                        zs.put("key", dg_name.getText());
                        zs.put("value", dg_plain_content_blob.getText());
                        ruyuanjiluList.add(zs);
                    }

                }
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return ruyuanjiluList;
    }

    /**
     * 体征  JHHDRWS017
     *
     * @param str
     * @return
     */
    public List<Map<String, String>> analysisXml2physicalSign(String str) {
        List<Map<String, String>> ruyuanjiluList = new LinkedList<>();
        Map<String, String> tzMap = new HashMap<>();
        try {
            Document dom = DocumentHelper.parseText(str.replaceAll("&", ""));
            Element root = dom.getRootElement();
            List<Element> items = root.elements();
            if (Objects.nonNull(items)) {

                for (int i = 0; i < items.size(); i++) {
                    JianyanbaogaoForAuxiliary mx = new JianyanbaogaoForAuxiliary();
                    Element element = items.get(i);
                    //名称 体温 脉搏等
                    String vitl_type_name = element.element("VITAL_TYPE_NAME").getText();
                    //值
                    String vital_sign_value = element.element("VITAL_SIGN_VALUE").getText();
                    //检查时间
                    String record_time = element.element("RECORD_TIME").getText();

//                    if(tzMap.containsKey(e)){

//                    }

                    Element dg_name = element.element("DG_NAME");
                    Element dg_plain_content_blob = element.element("DG_PLAIN_CONTENT_BLOB");
                    if (Objects.nonNull(dg_name) && Objects.nonNull(dg_plain_content_blob)) {
                        Map<String, String> zs = new HashMap<>();
                        zs.put("key", dg_name.getText());
                        zs.put("value", dg_plain_content_blob.getText());
                        ruyuanjiluList.add(zs);
                    }

                }
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return ruyuanjiluList;
    }

    /**
     * 医嘱  JHHDRWS012A
     *
     * @param str
     * @return
     */
    public List<Yizhu> analysisXml2Yizhu(String str) {
        List<Yizhu> yizhuList = new LinkedList<>();
        try {
            Document dom = DocumentHelper.parseText(str.replaceAll("&", ""));
            Element root = dom.getRootElement();
            List<Element> items = root.elements();
            if (Objects.nonNull(items)) {
                Yizhu yizhu = null;

                for (int i = 0; i < items.size(); i++) {
                    yizhu = new Yizhu();

                    Element element = items.get(i);
                    Element order_end_time = element.element("ORDER_END_TIME");
                    if (Objects.nonNull(order_end_time)) {
                        String text = order_end_time.getText();
                        Date date = DateFormatUtil.parseDate(text, DateFormatUtil.DATETIME_PATTERN_SS);
                        if (date.before(new Date())) {
                            continue;
                        }

                    }
                    //名称 体温 脉搏等
                    String orderItemName = element.element("ORDER_ITEM_NAME").getText();
                    //规格
                    String specification = "";
                    if (element.element("SPECIFICATION") != null) {
                        specification = element.element("SPECIFICATION").getText();
                    }
//                    频率
                    String frequencyName = "";
                    if (element.element("FREQUENCY_NAME") != null) {
                        frequencyName = element.element("FREQUENCY_NAME").getText();
                    }
                    //编码
                    String frequencyCode = null;
                    if (Objects.nonNull(element.element("FREQUENCY_CODE"))) {
                        frequencyCode = element.element("FREQUENCY_CODE").getText();
                    }
                    //单位 mg
//                    String dosageUnit = element.element("DOSAGE_UNIT").getText();
//                    String orderClassName = element.element("ORDER_CLASS_NAME").getText();
                    //用药方式
//                    String pharmacyWayName = element.element("PHARMACY_WAY_NAME").getText();
                    String orderBeginTime = null;
                    if (Objects.nonNull(element.element("ORDER_BEGIN_TIME"))) {
                        orderBeginTime = element.element("ORDER_BEGIN_TIME").getText();
                    }
                    Element dosage_value = element.element("DOSAGE_VALUE");
                    if (dosage_value != null) {
                        String dosageValue = element.element("DOSAGE_VALUE").getText();
                        yizhu.setDrug_amount_value(dosageValue);

                    }
//                    String orderPropertiesName = element.element("ORDER_PROPERTIES_NAME").getText();


                    if (Objects.nonNull(orderItemName)) {
                        yizhu.setOrder_item_name(orderItemName);
                        yizhu.setSpecification(specification);
                        yizhu.setFrequency_name(frequencyName);
                        yizhu.setFrequency_code(frequencyCode);
                        yizhu.setOrder_begin_time(orderBeginTime);
                        yizhuList.add(yizhu);
                    }

                }
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return yizhuList;
    }

    public static void main(String[] args) {
//        String s =
//                "<ArrayList>\n" +
//                "  <item> \n" +
//                "   <LAB_SUB_ITEM_ENAME>Gentamicin High &30;Level Resistance</LAB_SUB_ITEM_ENAME>\n" +
//                "  </item>\n" +
//                "</ArrayList>\n";
//        String s1="<xml><![CDATA[" + s + "]]></xml>";
        String s1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<ArrayList>\n" +
                "  <item> \n" +
                "   <LAB_SUB_ITEM_ENAME>Gentamicin High &#30;Level Resistance</LAB_SUB_ITEM_ENAME>\n" +
                "  </item>\n" +
                "</ArrayList>\n";


        try {
            String ss = s1.replaceAll("&", "%%%");
            Document dom = DocumentHelper.parseText(ss);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }
}
