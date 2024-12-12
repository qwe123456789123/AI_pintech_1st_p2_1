package org.koreait.pokemon.tests;

import org.junit.jupiter.api.Test;
import org.koreait.pokemon.api.services.ApiUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApiTest2 {
    @Autowired
    private ApiUpdateService service;

    @Test
    void updateTest1() {
        service.update(1); // 숫자 1게 증가 할때마다 100게식 가져옴
        service.update(2);
        service.update(3);
        service.update(4);
    }
}
