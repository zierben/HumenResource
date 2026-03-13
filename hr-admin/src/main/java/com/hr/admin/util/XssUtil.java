package com.hr.admin.util;

import org.springframework.stereotype.Component;

@Component
public class XssUtil {
    
    private static final String[] DANGEROUS_TAGS = {
        "<script", "</script>", "javascript:", "onerror=", "onload=",
        "onclick=", "onmouseover=", "onfocus=", "onblur=",
        "<iframe", "</iframe>", "<object", "</object>",
        "<embed", "<form", "</form>", "eval(", "expression("
    };
    
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        
        String result = input;
        
        for (String tag : DANGEROUS_TAGS) {
            result = result.replaceAll("(?i)" + tag, "");
        }
        
        result = result.replace("&", "&amp;")
                       .replace("<", "&lt;")
                       .replace(">", "&gt;")
                       .replace("\"", "&quot;")
                       .replace("'", "&#x27;");
        
        return result;
    }
    
    public static String stripHtml(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("<[^>]*>", "");
    }
    
    public static boolean containsXss(String input) {
        if (input == null) {
            return false;
        }
        
        String lowerInput = input.toLowerCase();
        for (String tag : DANGEROUS_TAGS) {
            if (lowerInput.contains(tag.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
