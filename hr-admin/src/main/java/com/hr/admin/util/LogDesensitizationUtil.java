package com.hr.admin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogDesensitizationUtil {

    private static final Logger log = LoggerFactory.getLogger(LogDesensitizationUtil.class);

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(password|pwd|passwd)[=:]\\s*([\\w@#$%^&*!]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TOKEN_PATTERN = Pattern.compile("(token|jwt|access_token|refresh_token)[=:]\\s*([\\w\\-\\.]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("([1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx])");
    private static final Pattern PHONE_PATTERN = Pattern.compile("(1[3-9]\\d{9})");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");

    public static String desensitize(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        try {
            String result = message;
            result = PASSWORD_PATTERN.matcher(result).replaceAll("$1=***");
            result = TOKEN_PATTERN.matcher(result).replaceAll("$1=***");
            result = ID_CARD_PATTERN.matcher(result).replaceAll(m -> maskIdCard(m.group(1)));
            result = PHONE_PATTERN.matcher(result).replaceAll(m -> maskPhone(m.group(1)));
            result = EMAIL_PATTERN.matcher(result).replaceAll(m -> maskEmail(m.group(1)));
            return result;
        } catch (Exception e) {
            return message;
        }
    }

    private static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 8) {
            return "**************";
        }
        return idCard.substring(0, 4) + "**********" + idCard.substring(idCard.length() - 4);
    }

    private static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return "***********";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***@***";
        }
        String[] parts = email.split("@");
        if (parts[0].length() <= 2) {
            return "**@" + parts[1];
        }
        return parts[0].substring(0, 2) + "***@" + parts[1];
    }
}
