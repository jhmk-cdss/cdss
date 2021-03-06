package com.jhmk.cloudservice.warnService.service;


import com.jhmk.cloudentity.earlywaring.entity.SmHospitalLog;
import com.jhmk.cloudentity.earlywaring.entity.SmUsers;
import com.jhmk.cloudentity.earlywaring.entity.repository.SmRoleRepository;
import com.jhmk.cloudentity.earlywaring.entity.repository.service.SmDeptsRepService;
import com.jhmk.cloudentity.earlywaring.entity.repository.service.SmHospitalLogRepService;
import com.jhmk.cloudentity.earlywaring.entity.repository.service.SmUsersRepService;
import com.jhmk.cloudutil.model.WebPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class SmUserService {
    @Autowired
    SmUsersRepService smUsersRepService;
    @Autowired
    SmRoleRepository roleRepository;
    @Autowired
    SmHospitalLogRepService smHospitalLogRepService;

    @Autowired
    SmDeptsRepService smDeptsRepService;
    @Autowired
    SmRoleService smRoleService;


    /**
     * 添加用户
     */
    @Transactional
    public SmUsers save(SmUsers user, String role) {
        SmUsers one = null;
        if (StringUtils.isNotBlank(role)) {
            //1.保存用户
            user.setRoleId(role);
            one = smUsersRepService.save(user);
            //2.保存用户角色的中间表

        }
        return one;
    }

    /**
     * 筛选
     *
     * @param user
     * @param page
     * @return
     */
    public Map<String, Object> serachData(SmUsers user, int page) {
        Map<String, Object> params = new HashMap<>();
        WebPage webPage = new WebPage();
        Pageable pageable = new PageRequest(page, webPage.getPageSize(), new Sort(Sort.Direction.DESC, "userId"));
        //1.动态条件查询集合
        Page<SmUsers> userPage = smUsersRepService.findAll(getWhereClause(user.getUserName()), pageable);
        List<SmUsers> userList = userPage.getContent();
        params.put("listData", userList);

        //2.机构列表
        int currentPage = page + 1;
        //当前页
        webPage.setPageNo(currentPage);
        //总页数
        webPage.setTotalPageNum(userPage.getTotalPages());
        //总记录数
        webPage.setTotalCount(userPage.getTotalElements());
        if (currentPage < userPage.getTotalPages()) {
            webPage.setHasNext(true);
        }
        if (currentPage > 1) {
            webPage.setHasPre(true);
        }
        params.put(WebPage.WEB_PAGE, webPage);
        return params;
    }

    /**
     * 动态条件拼装
     *
     * @param userNameCn
     * @return
     */
    private static Specification<SmUsers> getWhereClause(String userNameCn) {
        return new Specification<SmUsers>() {
            @Override
            public Predicate toPredicate(Root<SmUsers> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StringUtils.isNotBlank(userNameCn)) {
                    list.add(cb.like(root.get("userName").as(String.class), "%" + userNameCn + "%"));
                }


                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        };
    }

    /**
     * 保存编辑
     *
     * @param user
     * @param role
     * @return
     */
    @Transactional
    public SmUsers editor(SmUsers user, String role) {
        //1.更新用户信息
        SmUsers userTmp = smUsersRepService.findOne(user.getUserId());
        user.setUserPwd(userTmp.getUserPwd());
        SmUsers updateUser = smUsersRepService.save(user);
        //2.更新用户角色表中间表
        smRoleService.updateRoleUser(user.getUserId(), role);
        return updateUser;
    }


    public void updateDept() {
        Iterable<SmHospitalLog> all = smHospitalLogRepService.findAll();
        Iterable<SmUsers> depts = smUsersRepService.findAll();
        Iterator<SmUsers> deptsIterator = depts.iterator();
        Map<String, String> deptMap = new HashMap<>();
        while (deptsIterator.hasNext()) {
            SmUsers next = deptsIterator.next();
            deptMap.put(next.getUserName(), next.getUserId());
        }

        Iterator<SmHospitalLog> iterator = all.iterator();
        while (iterator.hasNext()) {
            SmHospitalLog next = iterator.next();
            if (deptMap.get(next.getDoctorName()) != null) {
                next.setDoctorId(deptMap.get(next.getDoctorName()));
                smHospitalLogRepService.save(next);
            }

        }
    }

}
