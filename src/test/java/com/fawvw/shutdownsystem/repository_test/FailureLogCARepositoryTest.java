package com.fawvw.shutdownsystem.repository_test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fawvw.shutdownsystem.repository.FailureLogCARepository;

@SpringBootTest
public class FailureLogCARepositoryTest {
    private FailureLogCARepository failureLogCARepository;

    @Autowired
    public FailureLogCARepositoryTest(FailureLogCARepository failureLogCARepository){
        this.failureLogCARepository = failureLogCARepository;
    }


    @Test
    void queryLast1hourmorethan10minTest(){
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime starTime = endTime.minusHours(1);
        List<Map<String,Object>> result = failureLogCARepository.queryLast1hourmorethan10min(starTime, endTime);
        System.out.println(result.size());
    }
}
