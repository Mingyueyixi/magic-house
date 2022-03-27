package com.lu.code.magic.magic;

import android.net.Uri;

import org.junit.Test;

public class UriTest {
    @Test
    public void testParse() {
        Uri uri = Uri.parse("content://www.baidu.com/mmkv/getString?table=123&key=abc");
        System.out.println(uri.getPathSegments());
    }
}
