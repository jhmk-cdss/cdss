package com.jhmk.cloudentity.earlywaring.entity.repository;

import com.jhmk.cloudentity.earlywaring.entity.rule.Binganshouye;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BinganshouyeRepository extends PagingAndSortingRepository<Binganshouye, Integer>, JpaSpecificationExecutor<Binganshouye> {

    @Query("select b  from  Binganshouye b where b.patient_id = ?1 and b.visit_id = ?2")
    Binganshouye findByPatient_idAndVisit_id(String patient_id, String visit_id);


    @Query("select b from Binganshouye b where b.patient_id = ?1 and b.visit_id <=?2 ")
    List<Binganshouye> findLessThanVisit_id(String patient_id, String visit_id);

    @Modifying
    @Query("delete from  Binganshouye b where b.patient_id = ?1 and b.visit_id <=?2 ")
    void deleteByPatient_idAndVisit_id(String patient_id, String visit_id);
}
