package ru.pavel2107.arch.hzcatalog.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.client.config.ClientUserCodeDeploymentConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.UserCodeDeploymentConfig;
import com.hazelcast.core.HazelcastInstance;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class HazelcastConfig {

    @Value("${app.hazelcast.server}")
    private String hzServer;

    @Value("${app.hazelcast.username}")
    private String hzUserName;

    @Value("${app.hazelcast.password}")
    private String hzPassword;

    @Bean
    public ClientConfig clientConfig() {
        ClientConfig config = new ClientConfig();
        ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
        for (String address : hzServer.split(",")) {
            clientNetworkConfig.addAddress(address);
        }

        GroupConfig groupConfig = config.getGroupConfig();
        groupConfig.setName(hzUserName);
        groupConfig.setPassword(hzPassword);
        config.setNetworkConfig(clientNetworkConfig);
        config.setClassLoader(Thread.currentThread().getContextClassLoader());

        return config;
    }

    @Bean
    public HazelcastInstance hazelcastInstance(ClientConfig config) {
        HazelcastInstance hazelcastInstanceClient = HazelcastClient.newHazelcastClient(config);
        return hazelcastInstanceClient;
    }

}
