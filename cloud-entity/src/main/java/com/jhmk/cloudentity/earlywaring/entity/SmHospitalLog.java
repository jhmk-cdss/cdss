package com.jhmk.cloudentity.earlywaring.entity;

import com.jhmk.cloudentity.earlywaring.entity.rule.LogMapping;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author ziyu.zhou
 * @date 2018/7/12 18:04
 */

@Entity
@Table(name = "sm_hospital_log", schema = "jhmk_waring")
public class SmHospitalLog {
    private int id;
    private String patientId;
    private String visitId;
    private String doctorName;
    private String doctorId;
    private String deptCode;
    private String diagnosisName;
    private String sicknessGrade;
    private String alarmLevel;
    private String classification;
    private String identification;
    private String warnSource;
    private Date createTime;
    private Date nowTime;
    private String hintContent;
    private String ruleSource;
    private String signContent;
    private String ruleId;
    private String ruleCondition;
    @Transient
    private String ruledate;

    private List<LogMapping>logMappingList;

    public String getRuledate() {
        return ruledate;
    }

    public void setRuledate(String ruledate) {
        this.ruledate = ruledate;
    }

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "patient_id", nullable = false, length = 32)
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Basic
    @Column(name = "visit_id", nullable = true, length = 11)
    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    @Basic
    @Column(name = "doctor_name", nullable = true, length = 255)
    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    @Basic
    @Column(name = "doctor_id", nullable = true, length = 32)
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    @Basic
    @Column(name = "dept_code", nullable = true, length = 32)
    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    @Basic
    @Column(name = "diagnosis_name", nullable = true, length = 255)
    public String getDiagnosisName() {
        return diagnosisName;
    }

    public void setDiagnosisName(String diagnosisName) {
        this.diagnosisName = diagnosisName;
    }

    @Basic
    @Column(name = "sickness_grade", nullable = true, length = 2)
    public String getSicknessGrade() {
        return sicknessGrade;
    }

    public void setSicknessGrade(String sicknessGrade) {
        this.sicknessGrade = sicknessGrade;
    }

    @Basic
    @Column(name = "alarm_level", nullable = true, length = 8)
    public String getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    @Basic
    @Column(name = "classification", nullable = true, length = 8)
    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    @Basic
    @Column(name = "identification", nullable = true, length = 8)
    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    @Basic
    @Column(name = "warn_source", nullable = true, length = 20)
    public String getWarnSource() {
        return warnSource;
    }

    public void setWarnSource(String warnSource) {
        this.warnSource = warnSource;
    }

    @Column(name = "create_time", nullable = true)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "now_time", nullable = true)
    public Date getNowTime() {
        return nowTime;
    }

    public void setNowTime(Date nowTime) {
        this.nowTime = nowTime;
    }

    @Basic
    @Column(name = "hint_content", nullable = true, length = 255)
    public String getHintContent() {
        return hintContent;
    }

    public void setHintContent(String hintContent) {
        this.hintContent = hintContent;
    }

    @Basic
    @Column(name = "rule_source", nullable = true, length = 255)
    public String getRuleSource() {
        return ruleSource;
    }

    public void setRuleSource(String ruleSource) {
        this.ruleSource = ruleSource;
    }

    @Basic
    @Column(name = "sign_content", nullable = true, length = 255)
    public String getSignContent() {
        return signContent;
    }

    public void setSignContent(String signContent) {
        this.signContent = signContent;
    }

    @Basic
    @Column(name = "rule_id", nullable = true, length = 60)
    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    @Basic
    @Column(name = "rule_condition", nullable = true, length = 255)
    public String getRuleCondition() {
        return ruleCondition;
    }

    public void setRuleCondition(String ruleCondition) {
        this.ruleCondition = ruleCondition;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "smHospitalLog")
    public List<LogMapping> getLogMappingList() {
        return logMappingList;
    }

    public void setLogMappingList(List<LogMapping> logMappingList) {
        this.logMappingList = logMappingList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SmHospitalLog that = (SmHospitalLog) o;
        return id == that.id &&
                Objects.equals(patientId, that.patientId) &&
                Objects.equals(visitId, that.visitId) &&
                Objects.equals(doctorName, that.doctorName) &&
                Objects.equals(doctorId, that.doctorId) &&
                Objects.equals(deptCode, that.deptCode) &&
                Objects.equals(diagnosisName, that.diagnosisName) &&
                Objects.equals(sicknessGrade, that.sicknessGrade) &&
                Objects.equals(alarmLevel, that.alarmLevel) &&
                Objects.equals(classification, that.classification) &&
                Objects.equals(identification, that.identification) &&
                Objects.equals(warnSource, that.warnSource) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(hintContent, that.hintContent) &&
                Objects.equals(ruleSource, that.ruleSource) &&
                Objects.equals(signContent, that.signContent) &&
                Objects.equals(ruleId, that.ruleId) &&
                Objects.equals(ruleCondition, that.ruleCondition);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, patientId, visitId, doctorName, doctorId, deptCode, diagnosisName, sicknessGrade, alarmLevel, classification, identification, warnSource, createTime, hintContent, ruleSource, signContent, ruleId, ruleCondition);
    }
}
