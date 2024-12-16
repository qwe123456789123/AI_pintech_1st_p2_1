package org.koreait.redistest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.koreait.dl.entities.RedisItem;
import org.koreait.dl.repositories.RedisItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"default","test"})
public class Ex01 {
    @Autowired
    private RedisItemRepository repository;

    @BeforeEach
    void init() {
        RedisItem redisItem = new RedisItem();
        redisItem.setKey("testkey");
        redisItem.setPrice(1000);
        redisItem.setProductNm("테스트 명");


        repository.save(redisItem);
    }

    @Test
    void test1() {
        RedisItem item = repository.findById("testkey").orElse(null);
        System.out.println(item);
    }
}