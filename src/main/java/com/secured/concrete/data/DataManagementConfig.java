package com.secured.concrete.data;

import com.secured.api.IJwtTokenFactory;
import com.secured.api.data.IUserDetailService;
import com.secured.utils.JwtTokenFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.secured.api.data")
@ComponentScan({"com.secured.api.data","com.secured.concrete.data"})
@EntityScan("com.secured.api.data")
@Configuration
public class DataManagementConfig {

    @Qualifier(IUserDetailService.SYSTEM_QUALIFIER)
    @Bean
    IUserDetailService userDetailService() {
        return new UserDetailServiceImpl();
    }
    @Bean
    IJwtTokenFactory tokenManager()
    {
        return  new JwtTokenFactory();
    }


}
