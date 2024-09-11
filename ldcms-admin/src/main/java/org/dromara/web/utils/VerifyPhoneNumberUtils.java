package org.dromara.web.utils;

public class VerifyPhoneNumberUtils {


    public static boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^1[3-9]\\d{9}$";
        return phoneNumber.matches(regex);
    }


}
