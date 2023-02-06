package com.hyphenate.chatdemo.common.utils;

import java.util.regex.Pattern;

public class PhoneNumberUtils {

    /**
     * Mobile phone number verification
     * @param phone
     * @return
     */
    public static boolean isPhoneNumber(String phone) {
        return Pattern.matches("^1\\d{10}$", phone);
    }

    public static boolean isNumber(String number) {
        return Pattern.matches("^([1-9]\\d*)|(0)$", number);
    }
}
