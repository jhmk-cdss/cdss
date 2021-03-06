package com.jhmk.cloudentity.earlywaring.entity.repository.service;

import com.jhmk.cloudentity.base.BaseRepService;
import com.jhmk.cloudentity.earlywaring.entity.repository.BasicInfoRepository;
import com.jhmk.cloudentity.earlywaring.entity.rule.BasicInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BasicInfoRepService extends BaseRepService<BasicInfo, Integer> {
    @Autowired
    BasicInfoRepository repository;


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BasicInfo save(BasicInfo basicInfo) {
        return repository.save(basicInfo);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Iterable<BasicInfo> save(List<BasicInfo> list) {
        return repository.save(list);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Integer id) {
        repository.delete(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(BasicInfo BasicInfo) {
        repository.delete(BasicInfo);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(List<BasicInfo> list) {
        repository.delete(list);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public BasicInfo findOne(Integer id) {
        return repository.findOne(id);
    }


    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Iterable<BasicInfo> findAll() {
        return repository.findAll();
    }


    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Page<BasicInfo> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }


    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public BasicInfo findByPatient_idAndVisit_id(String patient_id, String visit_id) {
        return repository.findByPatient_idAndVisit_id(patient_id, visit_id);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<BasicInfo> findLessThanVisit_id(String patient_id, String visit_id) {
        return repository.findLessThanVisit_id(patient_id,visit_id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByPatient_idAndVisit_id(String pasit_id,String v_id) {
        repository.deleteByPatient_idAndVisit_id(pasit_id,v_id);
    }
}
