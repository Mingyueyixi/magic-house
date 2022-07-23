package com.lu.magic.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Lu
 * Date: 2022/03/02
 * Description:
 */
public class TextUtil {

    public static boolean isEmpty(CharSequence chr) {
        return TextUtils.isEmpty(chr);
    }

    /**
     * @param regex 正则
     * @param input 文本
     * @return 是否找到
     */
    public static boolean find(String regex, CharSequence input) {
        if (input == null) {
            return false;
        }
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.find();
    }

    /**
     * @param regex 正则
     * @param input 文本
     * @param flags – Match flags, a bit mask that may include CASE_INSENSITIVE, MULTILINE, DOTALL, UNICODE_CASE, UNIX_LINES, LITERAL, and COMMENTS
     * @return 是否找到
     */
    public static boolean find(String regex, CharSequence input, int flags) {
        if (input == null) {
            return false;
        }
        Pattern p = Pattern.compile(regex, flags);
        Matcher m = p.matcher(input);
        return m.find();
    }

    /**
     * @param regex 正则
     * @param input 文本
     * @return 是否完全匹配
     */
    public static boolean matches(String regex, CharSequence input) {
        if (input == null) {
            return false;
        }
        return Pattern.matches(regex, input);
    }

    /**
     * @param regex 正则
     * @param input 文本
     * @param flags – Match flags, a bit mask that may include CASE_INSENSITIVE, MULTILINE, DOTALL, UNICODE_CASE, UNIX_LINES, LITERAL, and COMMENTS
     * @return 是否完全匹配
     */
    public static boolean matches(String regex, CharSequence input, int flags) {
        if (input == null) {
            return false;
        }
        Pattern p = Pattern.compile(regex, flags);
        return p.matcher(input).matches();
    }

    /**
     * @param text  文本1
     * @param input 文本2
     * @return text是否包含input
     */
    public static boolean contains(String text, String input) {
        if (input == null) {
            return false;
        }
        return input.contains(text);
    }

    public static CharSequence ofNotNull(CharSequence charSequence) {
        if (charSequence == null) {
            return "";
        }
        return charSequence;
    }
}
