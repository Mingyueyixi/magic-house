package com.lu.magic.feat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.system.Os;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdbConnectionReceiver extends BroadcastReceiver {
    public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";
    public static final String ACTION_WIFI_STATE = "android.net.wifi.WIFI_STATE_CHANGED";
    public static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String USB_CONNECTED = "connected";
    private static AdbConnectionReceiver sInstance;
    private boolean mIsRegister;
    private ExecutorService executorService;
    private Handler mainHandler;
    // 删除轮询相关的变量
    // private Runnable pollingRunnable;
    // private static final int POLLING_INTERVAL = 10000; // 10秒轮询一次
    private boolean lastAdbWifiState = false;
    private Context appContext; // 保存应用上下文

    public AdbConnectionReceiver() {
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        // 删除轮询任务初始化
        // pollingRunnable = new Runnable() {
        //     @Override
        //     public void run() {
        //         // 轮询检查WiFi ADB状态
        //         checkAdbOverWifiIfNeeded();
        //         // 继续轮询
        //         mainHandler.postDelayed(this, POLLING_INTERVAL);
        //     }
        // };
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        appContext = context.getApplicationContext(); // 保存上下文

        // 监听USB状态变化
        if (ACTION_USB_STATE.equals(action)) {
            boolean connected = intent.getBooleanExtra(USB_CONNECTED, false);
            boolean adb = intent.getBooleanExtra("adb", false);

            // 检查是否是ADB连接，只有真正ADB连接时才提示
            if (connected && adb) {
                Toast.makeText(context, "检测到ADB连接", Toast.LENGTH_SHORT).show();
            }
            // 其他USB连接状态不提示
        }
        // 监听WiFi状态变化和网络连接变化
        else if (ACTION_WIFI_STATE.equals(action) || ACTION_CONNECTIVITY_CHANGE.equals(action)) {
            // 当网络状态变化时检查WiFi ADB连接状态
            checkAdbOverWifiIfNeeded();
        }
    }

    /**
     * 根据需要检查WiFi ADB连接状态
     */
    private void checkAdbOverWifiIfNeeded() {
        // 这里可以添加检查条件，例如只在特定界面或特定状态下才检查
        checkAdbOverWifi();
    }

    /**
     * 检查WiFi ADB连接
     */
    private void checkAdbOverWifi() {
        executorService.execute(() -> {
            // 方法1: 检查5555端口是否开放（ADB默认端口）
            if (isPortOpen(getLocalIpAddress(), 5555, 2000)) {
                notifyAdbWifiState(true);
                return;
            }

            // 方法2: 检查系统属性
            if (isAdbOverWifiEnabled()) {
                notifyAdbWifiState(true);
                return;
            }

            // 没有检测到WiFi ADB连接
            notifyAdbWifiState(false);
        });
    }

    /**
     * 通知WiFi ADB连接状态变化
     * @param isConnected 是否连接
     */
    private void notifyAdbWifiState(boolean isConnected) {
        if (isConnected != lastAdbWifiState) {
            lastAdbWifiState = isConnected;
            if (isConnected) {
                // 只有在检测到WiFi ADB连接时才提示
                showToastOnUiThread("检测到WiFi ADB连接");
            }
        }
    }

    /**
     * 检查指定IP和端口是否开放
     */
    private boolean isPortOpen(String ip, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), timeout);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取本地IP地址
     */
    private String getLocalIpAddress() {
        try {
            // 读取系统网络信息文件
            BufferedReader reader = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("00:00:00:00:00:00") && line.contains("wlan0")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length > 3) {
                        return parts[0]; // 返回IP地址
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }

    /**
     * 检查是否启用了WiFi ADB调试
     */
    private boolean isAdbOverWifiEnabled() {
        try {
            // 检查系统属性
            String adbPort = getSystemProperty("service.adb.tcp.port");
            return adbPort != null && !adbPort.isEmpty() && !adbPort.equals("0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取系统属性
     */
    private String getSystemProperty(String key) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return Os.getenv(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 在UI线程显示Toast
     */
    private void showToastOnUiThread(String message) {
        mainHandler.post(() -> {
            if (appContext != null) {
                Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static AdbConnectionReceiver getInstance() {
        if (sInstance == null) {
            sInstance = new AdbConnectionReceiver();
        }
        return sInstance;
    }

    @SuppressWarnings("UnspecifiedRegisterReceiverFlag")
    public void register(Context context) {
        unregister(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_STATE);
        // 添加WiFi状态变化和网络连接变化的监听
        filter.addAction(ACTION_WIFI_STATE);
        filter.addAction(ACTION_CONNECTIVITY_CHANGE);

        AdbConnectionReceiver receiver = getInstance();
        context.registerReceiver(receiver, filter);
        appContext = context.getApplicationContext();
        // 删除轮询机制的启动
        // if (!mIsRegister) {
        //     // 启动轮询机制
        //     mainHandler.postDelayed(pollingRunnable, POLLING_INTERVAL);
        // }
        mIsRegister = true;
    }

    public void unregister(Context context) {
        try {
            if (mIsRegister) {
                context.unregisterReceiver(this);
                mIsRegister = false;
            }
            // 移除轮询任务
            // mainHandler.removeCallbacks(pollingRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
