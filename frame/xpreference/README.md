# 说明

这是一个跨进程的`android`偏好库

实现方式：`ContentProvider`

使用方式：

作为数据仓库的app：

（1）添加依赖

（2）配置`DataShareProvider`

自行定义`authorities`，默认接收的是 `com.lu.magic`

```xml

<provider 
    android:name="com.lu.magic.frame.xp.provider.DataShareProvider"
    android:authorities="com.lu.magic" 
    android:exported="true" />

```

作为作为查询方的app：

（1）添加依赖

（2）初始化`XPreference`

在`setProviderConfig`时，配置提供`authority`

```java 

XPreference.ofConfigApply()
            .setProviderConfig(new ProviderConfig("xxx.authority"))
            .setLogger(new DefaultLogger());
```

//TODO: 目前由于setProviderConfig保存为静态，故不支持查询多个不同的authority