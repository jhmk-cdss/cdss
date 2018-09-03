package com.jhmk.cloudentity.page.repository;

import com.jhmk.cloudentity.page.bean.ClickRate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface ClickRateRepository extends PagingAndSortingRepository<ClickRate, Integer>, JpaSpecificationExecutor<ClickRate> {

    ClickRate findByDoctorIdAndCreateTimeAndType(String doctorId, String createTime, String type);

}
