package com.fawvw.shutdownsystem.controller_test;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;


@SpringBootTest
@AutoConfigureMockMvc
public class RestApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testStopAccumOfLine() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/accumofworkshop")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testStopTop() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
        .get("/api/stopTop")
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 文件上传
     * @throws Exception
     */
    @Test
    public void fileUploadTest() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders
        .get("/api/loadupdata")
        .contentType(MediaType.APPLICATION_JSON)
        .param("path", "/Users/wizard/Downloads/alarmlogbook12-04-13-21.xlsx"))
        .andDo(MockMvcResultHandlers.print());
    }
}