package ru.pavel2107.arch.hzcatalog.controller;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Configuration;
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
import ru.pavel2107.arch.hzcatalog.domain.Category;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static ru.pavel2107.arch.hzcatalog.Utils.serviceUrl;

@RestController
@Configuration
public class BrandController {
    static final Logger logger = LogManager.getLogger(BrandController.class);

    private RestTemplate restTemplate;
    private HazelcastInstance hazelcastInstance;
    private DiscoveryClient discoveryClient;

    // @Value("${app.brands.url}")
    private String url;

    @Value("${app.brands.ttl}")
    private Integer ttl;


    @Autowired
    public BrandController(RestTemplate restTemplate, @Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.hazelcastInstance = hazelcastInstance;
        this.discoveryClient = discoveryClient;
        this.url = serviceUrl( discoveryClient, Utils.CATALOG_APP_NAME) + "/microservices/v2/catalog/brands";
    }

    @GetMapping(value = "/microservices/v3/catalog/brands", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Brand> getBrands() {
        IMap<Long, Brand> map = hazelcastInstance.getMap("brands");
        List<Brand> list = new ArrayList<>();
        String cashed = "";
        if (map.entrySet().size() == 0) {
            HttpEntity<String> entity = Utils.initEntity("", "");
            ResponseEntity<List<Brand>> responseEntity = restTemplate.exchange( url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Brand>>() {});
            list = responseEntity.getBody();
            cashed = "N";
            for (Brand brand : list) {
                map.put(brand.getId(), brand, ttl, TimeUnit.SECONDS);
            }
        } else {
            Collection<Brand> collection = map.values();
            list = new ArrayList<>(collection);
            cashed = "Y";
        }
        logger.info( "BRANDS. CASHED: {} RETURNED: {}", cashed, list.size());

        return list;
    }

}
