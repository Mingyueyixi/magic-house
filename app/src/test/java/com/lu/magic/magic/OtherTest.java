package com.lu.magic.magic;

import com.lu.magic.util.ReflectUtil;
import com.lu.magic.util.Transfer;

import org.junit.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.EventListenerProxy;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Author: Lu
 * Date: 2022/03/01
 * Description:
 */
public class OtherTest {
    @Test
    public void testRegex() {
        String whole = "www.baidu.com";
        String sub = "baidu";
    }

    @Test
    public void testRegex2() {
        StringBuilder sb = new StringBuilder();
        CharSequence proxy = (CharSequence) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), StringBuilder.class.getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if ("charAt".equals(method.getName())) {
                    return '鸣';
                }
                return method.invoke(sb, args);
            }
        });
        sb.append("尼玛");
        sb.append("哇哇哇");
        System.out.println(proxy.subSequence(1, 3));

        
    }

}
