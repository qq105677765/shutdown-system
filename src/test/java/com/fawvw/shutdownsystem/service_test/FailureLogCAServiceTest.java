package com.fawvw.shutdownsystem.service_test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fawvw.shutdownsystem.service.FailureLogCAService;

@SpringBootTest
public class FailureLogCAServiceTest {
    private FailureLogCAService failureLogCAService;

    @Autowired
    public FailureLogCAServiceTest(FailureLogCAService failureLogCAService) {
        this.failureLogCAService = failureLogCAService;
    }

    @Test
    void readFromExcelTest(){
        try {
            failureLogCAService.loadUpFromExcel("/Users/wizard/Downloads/alarmlogbook12-04-13-21.xlsx");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
