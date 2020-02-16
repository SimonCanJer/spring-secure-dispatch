package com.secured.concrete.data.cache.hazelcast;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.secured.api.data.ICachedUserDataAccess;
import com.secured.api.data.ImpersonatedDataShare;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * implements user detail cache over hazelcast
 * @see ICachedUserDataAccess
 */
public class HazelcastProvider  implements ICachedUserDataAccess {
    @Value("${secured.cache.name}")
    String appName;
    private HazelcastInstance hazelcast;
    Config  hazelcastConfig;
    IMap<String,UserDetails> mapByToken;
    IMap<String, Serializable> sessionData;
    ImpersonatedDataShare userDataService= new ImpersonatedDataShare() {
        @Override
        public <T extends Serializable> void cacheData(String token, T data, long millisecons) {
            sessionData.put(token,data,millisecons,TimeUnit.MILLISECONDS);
        }

        @Override
        public <T extends Serializable> T getData(String token) {
            return (T) sessionData.get(token);
        }

        @Override
        public void forget(String token) {
            sessionData.remove(token);

        }
    };


    @PostConstruct
    void init()
    {
        hazelcastConfig= new ClasspathXmlConfig("hazelcast.xml");
        hazelcastConfig.setInstanceName(appName);
        MapConfig config = new MapConfig();
        config.setTimeToLiveSeconds(3600);
        config.setName("token2user");
        hazelcastConfig.addMapConfig(config);
        config = new MapConfig();
        config.setTimeToLiveSeconds(3600);
        config.setName("session."+appName);
        hazelcast= Hazelcast.getOrCreateHazelcastInstance(hazelcastConfig);
        mapByToken = hazelcast.getMap("token2user");
        sessionData = hazelcast.getMap("session."+appName);
    }



    @Override
    public UserDetails getUserDetailsByToken(String userToken) {
        return mapByToken.get(userToken);
    }

    @Override
    public void updateUseDetails(UserDetails ud, String token,long milliseconds) {
        mapByToken.put(token,ud,milliseconds, TimeUnit.MILLISECONDS);

    }

    @Override
    public void unlinkUserDetailsFromToken(String token) {
        mapByToken.remove(token);

    }

    @Override
    public ImpersonatedDataShare getImpersonationDataSharing() {
        return userDataService;
    }
}
