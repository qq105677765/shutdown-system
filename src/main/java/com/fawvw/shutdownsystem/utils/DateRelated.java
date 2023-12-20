package com.fawvw.shutdownsystem.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateRelated {
    /**
     * 验证字符串是否符合目标时间格式
     * @param input     字符串
     * @param format    时间格式
     * @return
     */
    public static boolean isValidFormat(String input, String format) {
        if (input == null || input.length() == 0){
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);

        try {
            sdf.parse(input);
            // 如果解析成功，说明字符串符合指定格式
            return true;
        } catch (ParseException e) {
            // 解析失败，说明字符串不符合指定格式
            return false;
        }
    }

    /**
     * 如果早于8:30则返回前一天日期
     * @param date
     * @return
     */
    public static LocalDate workDateConvert(LocalDateTime date){
        LocalTime time = date.toLocalTime();
        LocalTime compareTime = LocalTime.of(8, 30);
        if(time.isBefore(compareTime)){
            return date.minusDays(1).toLocalDate();
        }else{
            return date.toLocalDate();
        }
    }
}
