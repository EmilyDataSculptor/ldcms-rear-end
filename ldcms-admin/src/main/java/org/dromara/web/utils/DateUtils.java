package org.dromara.web.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String formatDateToYYYYMMDD(String dateTimeStr) {
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateTimeStr.substring(0, 10), parser); // 假设时间部分总是"00:00:00"
        // 或者更健壮的方式：LocalDate date = YearMonthDay.parse(dateTimeStr.substring(0, 10), parser);
        // 但注意：YearMonthDay是假设的类，实际上应使用LocalDate和适当的解析器
        return date.format(formatter);
        // 注意：上面的代码实际上是不必要的分割和解析，因为我们可以直接解析整个字符串
        // 更简洁的方式是直接解析整个字符串并忽略时间部分，如第一个示例所示
    }

    // 但更简洁且正确的方法应该是：
    public static String formatDateToYYYYMMDDSimpler(String dateTimeStr) {
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate date = LocalDate.parse(dateTimeStr, parser); // LocalDate会自动忽略时间部分
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

}
