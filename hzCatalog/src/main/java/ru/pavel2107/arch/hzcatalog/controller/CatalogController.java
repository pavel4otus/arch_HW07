package ru.pavel2107.arch.hzcatalog.controller;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.pavel2107.arch.hzcatalog.Utils;
import ru.pavel2107.arch.hzcatalog.domain.Brand;
import ru.pavel2107.arch.hzcatalog.domain.User;
import ru.pavel2107.arch.hzcatalog.dto.GoodDto;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static ru.pavel2107.arch.hzcatalog.Utils.serviceUrl;

@RestController
public class CatalogController {
    static final Logger logger = LogManager.getLogger(CatalogController.class);

    private RestTemplate restTemplate;
    private HazelcastInstance hazelcastInstance;
    private DiscoveryClient discoveryClient;

    // @Value("${app.catalog.url}")
    private String url;

    @Value("${app.catalog.ttl}")
    private Integer ttl;

    @Autowired
    public CatalogController(RestTemplate restTemplate, @Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.hazelcastInstance = hazelcastInstance;
        this.discoveryClient = discoveryClient;
        this.url = serviceUrl( discoveryClient, Utils.CATALOG_APP_NAME) + "/microservices/v2/catalog";
    }

    @GetMapping(value = "/microservices/v3/catalog", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GoodDto> listGoods(Principal principal, @RequestHeader("Authorization") String token) {
        logger.info( "GOODS. GET");
        List<GoodDto> list = listGoods(principal, token, url, "", ttl, null);
        return list;
    }

    @GetMapping(value = "/microservices/v3/catalog/bycategory", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GoodDto> listGoodsByCategory(Principal principal, @RequestHeader("Authorization") String token, @RequestParam Long categoryId) {
        logger.info( "GOODS. USER: {}, CATEGORY: {}", principal.getName(), categoryId);
        Predicate predicate = Predicates.equal("category", categoryId);
        List<GoodDto> list = listGoods(principal, token, url, "" + categoryId, ttl, predicate);
        return list;
    }

    @GetMapping(value = "/microservices/v3/catalog/bybrand", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GoodDto> listGoodsByBrand(Principal principal, @RequestHeader("Authorization") String token, @RequestParam Long brandId) {
        logger.info( "GOODS. USER: {}, BRAND: {}", principal.getName(), brandId);
        Predicate predicate = Predicates.equal("brand", brandId);
        List<GoodDto> list = listGoods(principal, token, url, "", ttl, predicate);
        return list;
    }

    @GetMapping(value = "/microservices/v3/catalog/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GoodDto get(Principal principal, @RequestHeader("Authorization") String token, @PathVariable(value = "id") Long id) {
        logger.info( "GOODS. USER: {}, GET: {}", principal.getName(), id);
        Predicate predicate = Predicates.equal("id", id);
        List<GoodDto> list = listGoods(principal, token, url, "", ttl, predicate);
        GoodDto good = null;
        if (list != null && list.size() == 1) good = list.get(0);
        return good;
    }


    public List<GoodDto> listGoods(Principal principal, @RequestHeader("Authorization") String token, String url, String request, Integer ttl, Predicate p) {
        IMap<Long, GoodDto> map = hazelcastInstance.getMap("goods");
        List<GoodDto> list;
        String cashed = "Y";
        if (map.entrySet().size() == 0) {
            cashed = "N";
            HttpEntity<String> entity = Utils.initEntity(request, token);
            ResponseEntity<List<GoodDto>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<GoodDto>>() {
            });
            list = responseEntity.getBody();
            for (GoodDto good : list) {
                map.put(good.getId(), good, ttl, TimeUnit.SECONDS);
            }
            list.clear();
        }
        Collection<GoodDto> collection = p == null ? map.values() : map.values(p);
        list = new ArrayList<>(collection);
        logger.info( "GOODS. CASHED: {} RETURNED: {}", cashed, list.size());

        return list;
    }


}
