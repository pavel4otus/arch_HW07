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
import ru.pavel2107.arch.hzcatalog.domain.Category;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static ru.pavel2107.arch.hzcatalog.Utils.serviceUrl;

@RestController
public class CategoriesController {
    static final Logger logger = LogManager.getLogger(CategoriesController.class);

    private RestTemplate restTemplate;
    private HazelcastInstance hazelcastInstance;
    private DiscoveryClient discoveryClient;

    // @Value("${app.categories.url}")
    private String url;

    @Value("${app.categories.ttl}")
    private Integer ttl;

    @Autowired
    public CategoriesController(RestTemplate restTemplate, @Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.hazelcastInstance = hazelcastInstance;
        this.discoveryClient = discoveryClient;
        this.url = serviceUrl( discoveryClient, Utils.CATALOG_APP_NAME) + "/microservices/v2/catalog/categories";
    }

    @GetMapping(value = "/microservices/v3/catalog/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Category> getCategories() {
        IMap<Long, Category> map = hazelcastInstance.getMap("categories");
        List<Category> list = new ArrayList<>();
        String cashed = "";
        if (map.entrySet().size() == 0) {
            cashed = "N";
            HttpEntity<String> entity = Utils.initEntity("", "");
            ResponseEntity<List<Category>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Category>>() {
            });
            list = responseEntity.getBody();
            for (Category category : list) {
                map.put(category.getId(), category, ttl, TimeUnit.SECONDS);
            }
        } else {
            cashed = "Y";
            Collection<Category> collection = map.values();
            list = new ArrayList<>(collection);
        }
        logger.info( "CATEGORIES. CASHED: {} RETURNED: {}", cashed, list.size());

        return list;
    }


}