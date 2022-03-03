package com.lu.code.foolish.egg;

import android.text.TextUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Lu
 * Date: 2022/03/01
 * Description:
 */
public class OtherTest {
    @Test
    public void testRegex() {
        List<String> result = parseLastNodeIdForScratch("123,12， 34 ,9087,");
        System.out.println(result);
    }

    @Test
    public void testRegex2() {
//        boolean flag = Pattern.matches(".*bug.*", "警告bug哈哈哈");
        Pattern p = Pattern.compile(".*bug.*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        String input = "警告\nBug.\nssss";
        System.out.println(p.matcher(input).matches());
    }

    private List<String> parseLastNodeIdForScratch(String text) {
        List<String> lastNodeIdList = new ArrayList<>();
        String textTrim = text.trim();
        boolean formatError = false;

        if (textTrim.contains(",") || textTrim.contains("，")) {
            String[] splitText = textTrim.split("[,，]");
            for (String num : splitText) {
                num = num.trim();
                if (!num.matches("[0-9]+")) {
                    formatError = true;
                    continue;
                }
                lastNodeIdList.add(num);
            }
        } else {
            if (textTrim.matches("[0-9]")) {
                lastNodeIdList.add(textTrim);
            } else {
                formatError = true;
            }
        }
        if (formatError) {

        }
        return lastNodeIdList;
    }

}
