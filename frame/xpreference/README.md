# 说明

这是一个跨进程的`android`偏好库

## CPPreference

> CPPreference 使用ContentProvider实现

使用：
作为数据仓库的app（相当于服务端）

（1）添加依赖

（2）配置`PreferenceServerProvider`

自行定义`authorities`，如 `com.lu.magic`

```xml

<provider android:name="com.lu.magic.frame.xp.provider.PreferenceServerProvider"
    android:authorities="com.lu.magic.server" android:exported="true" />

```

作为作为查询方的app：

（1）添加依赖
（2）Android 11 以上的应用中，因为包可见性原因，可能无法通过`authority`找到`ContentProvider`，需要配置 `<queries>`
（3）初始化`CPreference`

```java 
       new CPPreference(context, "config", PreferenceIdValue.SP, "xxx.xxx")
```

## SPreference

> SPreference使用Socket实现

使用方式：

（1）作为服务端，对外提供数据

```kotlin

SPreference.initServer(10087)

```

这会启动一个socket服务。

（2）作为本地数据，像普通SharedPreference一样使用

```kotlin

val sp = SPreference.getLocalImpl(context, "config");

```

（3）其他app使用，查询远端SharedPreferences数据

```kotlin

val sp = SPreference.getRemoteImpl(context, "config", PreferenceIdValue.SP, 10087);

```
