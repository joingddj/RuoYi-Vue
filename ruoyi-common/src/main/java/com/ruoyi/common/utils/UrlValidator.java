package com.ruoyi.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证url格式
 */
public class UrlValidator {
    public static boolean validate(String url) {
        String regex = "^http(s)?\\:\\/\\/t\\.me\\/[a-zA-Z+\\-0-9]+$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        return matcher.matches();
    }
}
