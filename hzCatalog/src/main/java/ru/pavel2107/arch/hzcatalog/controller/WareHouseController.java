package ru.pavel2107.arch.hzcatalog.controller;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.pavel2107.arch.hzcatalog.Utils;
import ru.pavel2107.arch.hzcatalog.domain.Brand;
import ru.pavel2107.arch.hzcatalog.domain.WareHouse;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static ru.pavel2107.arch.hzcatalog.Utils.serviceUrl;

@RestController
public class WareHouseController {
    static final Logger logger = LogManager.getLogger(WareHouseController.class);

    private RestTemplate restTemplate;
    private HazelcastInstance hazelcastInstance;
    private DiscoveryClient discoveryClient;

    // @Value("${app.warehouses.url}")
    private String url;

    @Value("${app.warehouses.ttl}")
    private Integer ttl;

    @Autowired
    public WareHouseController(RestTemplate restTemplate, @Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.hazelcastInstance = hazelcastInstance;
        this.discoveryClient = discoveryClient;
        this.url = serviceUrl( discoveryClient, Utils.CATALOG_APP_NAME) + "/microservices/v2/catalog/warehouses";
    }


    @GetMapping(value = "/microservices/v3/catalog/warehouses", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WareHouse> getWareHouses(Principal principal, @RequestHeader("Authorization") String token) {
        IMap<Long, WareHouse> map = hazelcastInstance.getMap("warehouses");
        List<WareHouse> list = new ArrayList<>();
        String cashed = "Y";
        if (map.entrySet().size() == 0) {
            cashed = "N";
            HttpEntity<String> entity = Utils.initEntity("", token);
            ResponseEntity<List<WareHouse>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<WareHouse>>() {
            });
            list = responseEntity.getBody();
            for (WareHouse wareHouse : list) {
                map.put(wareHouse.getId(), wareHouse, ttl, TimeUnit.SECONDS);
            }
        }
        for (Map.Entry<Long, WareHouse> pair : map.entrySet()) {
            list.add(pair.getValue());
        }
        logger.info( "WAREHOUSE. CASHED: {} RETURNED: {}", cashed, list.size());

        return list;
    }
}
