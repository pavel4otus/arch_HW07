package ru.pavel2107.arch.hzcatalog.controller;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

@RestController
@Configuration
public class BrandController  {

    private RestTemplate restTemplate;
    private HazelcastInstance hazelcastInstance;

    @Value( "${app.brands.url}")
    private String url;

    @Value( "${app.brands.ttl}")
    private Integer ttl;

    @Autowired
    public BrandController( RestTemplate restTemplate, @Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance) {
       this.restTemplate = restTemplate;
       this.hazelcastInstance = hazelcastInstance;
    }

    @GetMapping(value = "/microservices/v3/catalog/brands", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Brand> getBrands() {
        IMap<Long, Brand> map = hazelcastInstance.getMap("brands");
        List<Brand> list = new ArrayList<>();
        if( map.entrySet().size() == 0){
            System.out.println( "Кеш пуст. делаем запрос к основному серверу");
            HttpEntity<String> entity = Utils.initEntity( "", "");

            ResponseEntity<List<Brand>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Brand>>() {});
            list = responseEntity.getBody();
            System.out.println( "Получен ответ от основного сервера. count=" + list.size());
            for ( Brand brand: list) {
                map.put( brand.getId(), brand, ttl, TimeUnit.SECONDS);
            }
        } else {
            System.out.println( "Получаем значения из кеша");
            Collection<Brand> collection = map.values();
            list = new ArrayList<>( collection);
            System.out.println( "Получен ответ из кеша. count=" + list.size());
        }

        return list;
    }

}
