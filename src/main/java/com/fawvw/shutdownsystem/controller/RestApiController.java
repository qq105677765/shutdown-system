package com.fawvw.shutdownsystem.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fawvw.shutdownsystem.repository.FailureLogCARepository;
import com.fawvw.shutdownsystem.service.FailureLogCAService;

@RestController
@RequestMapping("/api")
public class RestApiController {

    private FailureLogCAService failureLogCAService;
    private FailureLogCARepository failureLogCARepository;

    @Autowired
    public RestApiController(FailureLogCAService failureLogCAService,
            FailureLogCARepository failureLogCARepository) {
        this.failureLogCAService = failureLogCAService;
        this.failureLogCARepository = failureLogCARepository;
    }

    @GetMapping("/test")
    ResponseEntity<String> test() {
        try {
            failureLogCAService.loadUpFromExcel("/Users/wizard/Downloads/总装故障日志-20.xlsx");
            return ResponseEntity.ok("");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(e.getMessage());
        }
    }

    /**
     * 上传数据
     * 
     * @param path 文件地址（绝对路径）
     * @return 上传成功返回200；否则501，并携带错误信息
     */
    @GetMapping("/loadupdata")
    ResponseEntity<String> fileUpload(@RequestParam(name = "path") String path) {
        try {
            failureLogCAService.loadUpFromExcel(path);
            return ResponseEntity.ok("");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(e.getMessage());
        }
    }

    /**
     * 获取今日设备区域累计停台排名
     * 
     * @return
     */
    @GetMapping("/stopTop")
    ResponseEntity<List<Map<String, Object>>> stopTop() {
        return ResponseEntity.ok(failureLogCARepository.queryTopDuration(
                LocalDate.now(),3));
    }

    /**
     * 线体停台趋势
     * 
     * @param ws 工段名
     * @return
     */
    @GetMapping("/accumofline")
    ResponseEntity<List<Map<String, Object>>> stopAccumTendOfLine(@RequestParam("workshop_section") String ws) {
        return ResponseEntity.ok(failureLogCARepository.queryAccumTend(
                LocalDate.now().minusDays(10),
                LocalDate.now(),
                ws));
    }

    /**
     * 车间停台趋势
     * 
     * @return
     */
    @GetMapping("/accumofworkshop")
    ResponseEntity<List<Map<String, Object>>> stopAccumTendOfWorkshop() {
        return ResponseEntity
                .ok(failureLogCARepository.queryAccumTend(
                        LocalDate.now().minusDays(10),
                        LocalDate.now(),
                        null));
    }

    /**
     * 一小时前超过10min停台
     * @return
     */
    @GetMapping("/lasthourstop")
    ResponseEntity<List<Map<String, Object>>> lastHourStop() {
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime startTime = endDateTime.minusHours(1);
        List<Map<String,Object>> result = failureLogCARepository.queryLast1hourmorethan10min(startTime,endDateTime);
        return ResponseEntity.ok(result);
    }
}
