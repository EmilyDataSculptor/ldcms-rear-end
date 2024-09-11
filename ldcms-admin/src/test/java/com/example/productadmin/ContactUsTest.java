package com.example.productadmin;

import org.dromara.web.utils.VerifyPhoneNumberUtils;
import org.junit.jupiter.api.Test;

public class ContactUsTest {

    @Test
    public void testPhoneNumber() {
        String s = "13400967938";
        System.out.println(VerifyPhoneNumberUtils.isValidPhoneNumber(s));

    }

    @Test
    public void testMinioRoute() {
        String s = "http://192.168.200.130:9000/minio/test/2024/8/9/";
        String arr = "";
        String[] split = s.split("/");
        for (int i = 0; i < split.length; i++) {
            if (i+3==split.length){
            return;
            }
            arr = arr + split[i+3];
        }
        System.out.println(arr);
        System.out.println(arr);
        System.out.println(arr);
        System.out.println(arr);

    }

    @Test
    public void testSplitUrl() {
        String s = "http://192.168.200.130:9000/test/2024/8/31/ff56af61-017f-4bb6-9d63-03b03740273b_indexLogo2.png";
        System.out.println(s.split("/test/")[1]);
    }



}
