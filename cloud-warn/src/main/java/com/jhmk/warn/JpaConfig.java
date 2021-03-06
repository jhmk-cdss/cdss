package com.jhmk.warn;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author zzy on 18/08/19.
 */
@EnableJpaRepositories(basePackages = {"com.jhmk.cloudentity"})

@Configuration
public class JpaConfig {

}
