package com.jhmk.cloudservice.warnService.service;

import com.jhmk.cloudentity.earlywaring.entity.SmUsers;
import com.jhmk.cloudentity.earlywaring.entity.repository.service.SmRoleRepService;
import com.jhmk.cloudentity.earlywaring.entity.repository.service.SmUsersRepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmRoleService {
    @Autowired
    SmRoleRepService roleRepository;
    @Autowired
    SmUsersRepService userRepository;

    /**
     * 更新用户角色
     *
     * @param id
     * @param role
     */
    public void updateRoleUser(String id, String role) {
        SmUsers user = userRepository.findOne(id);
        user.setRoleId(role);
        userRepository.save(user);
    }




}
