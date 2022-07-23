package com.lu.magic.magic;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class UriTest {
    @Test
    public void testParse() {
        Uri uri = Uri.parse("content://www.baidu.com/mmkv/getString?table=123&key=abc");
        System.out.println(uri.getPathSegments());

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("nima", "nima");
        map.put("nima2", "nima2");
        map.put("nima1", "nima1");
        map.put("nima3", "nima3");

        Log.e(">>>", map + "");
        Log.e(">>>", Arrays.toString(map.values().toArray()) + "");

        Bundle bundle = new Bundle();
        bundle.putString("nima", "nima");
        bundle.putString("nima2", "nima2");
        bundle.putString("nima1", "nima1");
        bundle.putString("nima3", "nima3");

        Log.e(">>>\n\n----------\n", bundle + "");
        Log.e(">>>", Arrays.toString(bundle.keySet().toArray()) + "");

    }

}
