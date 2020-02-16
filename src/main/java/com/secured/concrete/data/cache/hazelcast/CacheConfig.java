package com.secured.concrete.data.cache.hazelcast;

import com.secured.api.data.ICachedUserDataAccess;
import com.secured.api.data.ICachedUserDetailAccess;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {
    HazelcastProvider provider;
    @Bean
    @Qualifier(ICachedUserDetailAccess.SYSTEM_QUALIFIER)
    ICachedUserDataAccess userDetailCache()
    {
        return provider =new HazelcastProvider();

    }



}
