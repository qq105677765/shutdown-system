package com.fawvw.shutdownsystem.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.fawvw.shutdownsystem.model.FailureLogCA;
import com.fawvw.shutdownsystem.repository.FailureLogCARepository;
import com.fawvw.shutdownsystem.utils.DateRelated;

import io.micrometer.common.lang.Nullable;

@Service
public class FailureLogCAService {
    private ResourceLoader resourceLoader;
    private FailureLogCARepository failureLogCARepository;

    @Autowired
    public FailureLogCAService(ResourceLoader resourceLoader, FailureLogCARepository failureLogCARepository) {
        this.resourceLoader = resourceLoader;
        this.failureLogCARepository = failureLogCARepository;
    }

    /**
     * 将excel数据存入数据表
     * 
     * @param excelFilePath 传入文件路径
     * @throws Exception 返回给前端的异常信息
     */
    public void loadUpFromExcel(String excelFilePath) throws Exception {
        List<FailureLogCA> entity_list = new ArrayList<>();
        // 时间格式
        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        // 必要的字段
        List<String> necessary_fields = Arrays.asList("开始", "结束", "设备区域", "警报信息", "通知形式", "优先级");
        // 索引-字段映射
        Map<Integer, String> necessary_field_index = new HashMap<>();

        try (InputStream inputStream = resourceLoader
                .getResource("file:" + excelFilePath)
                .getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)) {
            // 获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);

            // 遍历行
            for (Row row : sheet) {
                if (0 == row.getRowNum()) {
                    guaranteeInfoComplete(row, necessary_fields, necessary_field_index);
                    continue;
                }
                FailureLogCA obj = new FailureLogCA();
                if (!obtainTableValue(obj, row, necessary_field_index, pattern, formatter))
                    continue;
                additionInfo(obj);
                // 存入实体类列表
                entity_list.add(obj);
            }

            // 批量存入数据库
            failureLogCARepository.saveAllIfNotExists(entity_list);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 判断首行字段是否缺失。如果缺失，抛出异常
     * 
     * @param row                   行
     * @param necessary_fields      必要的字段list
     * @param necessary_field_index 字段-索引 映射
     * @throws Exception
     */
    private void guaranteeInfoComplete(Row row, List<String> necessary_fields,
            Map<Integer, String> necessary_field_index)
            throws Exception {

        List<String> lost_field = new ArrayList<>();
        // 收集首行内容
        List<String> file_field = new ArrayList<>();
        for (Cell cell : row) {
            file_field.add(cell.getStringCellValue().trim());
        }
        // 建立字段-索引映射，如果有不存在的项记入lost_field
        for (String f : necessary_fields) {
            int indexOf = file_field.indexOf(f);
            if (-1 == indexOf) {
                lost_field.add(f);
                continue;
            }
            necessary_field_index.put(indexOf, f);
        }
        if (lost_field.size() > 0) {
            throw new Exception("缺少以下字段：" + String.join(",", lost_field));
        }

    }

    /**
     * 获取表格内容传入目标对象，包含各种校验
     * 
     * @param obj                   要传入的对象
     * @param row                   行
     * @param necessary_field_index 必要的字段所在的索引
     * @param pattern               时间格式，用于时间格式校验
     * @param formatter             时间格式类，由时间格式生成（放置重复实例化占用内存）
     * @return 是否成功传入对象,成功为true
     */

    private @Nullable boolean obtainTableValue(FailureLogCA obj, Row row, Map<Integer, String> necessary_field_index,
            String pattern,
            DateTimeFormatter formatter) {
        // 没有可迭代的内容返回false
        Iterator<Cell> iterator = row.iterator();
        if (!iterator.hasNext())
            return false;
        // 开始遍历Cell
        for (Cell cell : row) {
            // 不是需要的字段，跳过
            int cellIndex = cell.getColumnIndex();
            if (!necessary_field_index.containsKey(cellIndex)) {
                continue;
            }
            // 从表中获取数值
            String field_name_cn = necessary_field_index.get(cellIndex);
            String cellValue = "";
            switch (field_name_cn) {
                case "开始":
                    cellValue = cell.getStringCellValue();
                    // 内容格式校验
                    if (!DateRelated.isValidFormat(cellValue, pattern))
                        return false;
                    obj.setStartTime(LocalDateTime.parse(cellValue, formatter));
                    break;
                case "结束":
                    // 内容格式校验
                    cellValue = cell.getStringCellValue();
                    if (!DateRelated.isValidFormat(cellValue, pattern))
                        return false;
                    obj.setEndTime(LocalDateTime.parse(cell.getStringCellValue(), formatter));
                    break;
                case "设备区域":
                    obj.setEquipmentArea(cell.getStringCellValue().trim());
                    break;
                case "警报信息":
                    obj.setAlarmInfo(cell.getStringCellValue());
                    break;
                case "通知形式":
                    obj.setNotificationForm(cell.getStringCellValue());
                    break;
                case "优先级":
                    obj.setPriority(Integer.parseInt(cell.getStringCellValue()));
                    break;
            }
        }
        return true;
    }

    /**
     * 添加（计算出来的）额外信息
     * 
     * @param obj 要添加信息的对象
     */
    private void additionInfo(FailureLogCA obj) {
        // 配置年月日
        obj.setRecordDate(DateRelated.workDateConvert(obj.getStartTime()));
        String[] equipmentStrList = obj.getEquipmentArea().split("_");
        // 添加工段
        obj.setSection(equipmentStrList[2]);
        // region 添加工位
        obj.setPosition(equipmentStrList[3]);
        // endregion
        // 添加持续时间
        obj.setDuration(Duration.between(obj.getStartTime(), obj.getEndTime()).toSeconds() / 60.0);
    }
}
