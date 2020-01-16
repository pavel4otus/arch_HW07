package ru.pavel2107.arch.hzcatalog.controller;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

@RestController
public class CategoriesController  {

    private RestTemplate restTemplate;
    private HazelcastInstance hazelcastInstance;

    @Value("${app.categories.url}")
    private String url;

    @Value("${app.categories.ttl}")
    private Integer ttl;

    @Autowired
    public CategoriesController(RestTemplate restTemplate, @Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance) {
        this.restTemplate = restTemplate;
        this.hazelcastInstance = hazelcastInstance;
    }

    @GetMapping(value = "/microservices/v3/catalog/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List <Category> getCategories() {
        IMap<Long, Category> map = hazelcastInstance.getMap("categories");
        List<Category> list = new ArrayList<>();
        if (map.entrySet().size() == 0) {
            System.out.println( "Кеш пуст. делаем запрос к основному серверу");
            HttpEntity<String> entity = Utils.initEntity("", "");
            ResponseEntity<List<Category>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Category>>() {});
            list = responseEntity.getBody();
            System.out.println( "Получен ответ от основного сервера. count=" + list.size());
            for (Category category : list) {
                map.put(category.getId(), category, ttl, TimeUnit.SECONDS);
            }
        } else {
            System.out.println( "Получаем значения из кеша");
            Collection<Category> collection = map.values();
            list = new ArrayList<>( collection);
            System.out.println( "Получен ответ из кеша. count=" + list.size());
        }

        return list;
    }


}