package com.example.productadmin;

import org.checkerframework.checker.units.qual.A;
import org.dromara.web.utils.VerifyPhoneNumberUtils;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTest {

    @Test
    public void testPhoneNumber() {
        String input = "Thu Aug 22 00:00:00 GMT+08:00 2024";

        // 使用字符串操作提取年月日
        String year = input.substring(input.lastIndexOf(' ') + 1);
        String monthDay = input.substring(input.indexOf(' ') + 1, input.lastIndexOf(' '));
        String[] parts = monthDay.split(" ");
        String month = parts[0]; // 假设月份总是两个字符或三个字符（英文缩写）
        String day = parts[1];

        // 创建一个LocalDate（注意：这里我们假设月份总是三个字符的英文缩写）
        // Java 8的Month枚举没有直接的方法来从字符串获取Month对象，所以我们可能需要一个映射或使用DateTimeFormatter
        // 但为了简洁，我们直接构造LocalDate（这在实际应用中可能不是最佳实践）
        // 注意：这里的月份处理是简化的，可能不适用于所有语言环境
        int monthNum = switch (month.toLowerCase(Locale.ROOT)) {
            case "jan" -> 1;
            case "feb" -> 2;
            case "mar" -> 3;
            case "apr" -> 4;
            case "may" -> 5;
            case "jun" -> 6;
            case "jul" -> 7;
            case "aug" -> 8;
            case "sep" -> 9;
            case "oct" -> 10;
            case "nov" -> 11;
            case "dec" -> 12;
            default -> throw new IllegalArgumentException("Invalid month: " + month);
        };

        LocalDate localDate = LocalDate.of(Integer.parseInt(year), monthNum, Integer.parseInt(day));

        // 使用DateTimeFormatter格式化LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = localDate.format(formatter);

        System.out.println(formattedDate); // 输出: 2024-08-22

        LocalDate localDate2 = LocalDate.of(2024, 8, 22); // 示例日期

        // 转换为ZonedDateTime，假设我们想要的是UTC时间，或者任何特定的时区
        // 这里我们使用UTC作为示例，但你可以根据需要更改ZoneId
        ZonedDateTime zonedDateTime = localDate2.atStartOfDay(ZoneId.of("UTC"));

        // 将ZonedDateTime转换为java.util.Date
        Date date = Date.from(zonedDateTime.toInstant());

        // 输出转换后的Date对象
        System.out.println(date);


    }

}
