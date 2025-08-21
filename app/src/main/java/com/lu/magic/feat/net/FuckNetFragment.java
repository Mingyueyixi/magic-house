package com.lu.magic.feat.net;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.lu.magic.module.databinding.FragmentFuckNetBinding;
import com.lu.magic.ui.BindingFragment;
import com.lu.magic.util.ToastUtils;
import com.lu.magic.util.dialog.DialogUtil;
import com.lu.magic.util.permission.PermissionUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author Lu
 * @date 2025/7/29
 * @description
 */
public class FuckNetFragment extends BindingFragment<FragmentFuckNetBinding> {
    private FragmentFuckNetBinding mBinding;
    private FuckNetViewModel mViewModel;


    @NotNull
    @Override
    public FragmentFuckNetBinding onViewBinding(@NotNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        return FragmentFuckNetBinding.inflate(layoutInflater, viewGroup, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FuckNetViewModel.class);
        mBinding = getBinding();

        mBinding.btnGetNetInfo.setOnClickListener(v -> loadNetInfo());
        mBinding.btnGetWifiScannerInfo.setOnClickListener(v -> loadWifiList());
        initViewModelObserver();
    }

    private void loadWifiList() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // 如果定位服务没有开启，提示用户开启
        if (!isGpsEnabled && !isNetworkEnabled) {
            DialogUtil.buildAlertDialog(getContext())
                    .setTitle("提示")
                    .setMessage("需要开启定位服务才能获取WiFi列表，请前往设置开启")
                    .setNegativeButton("取消", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setPositiveButton("去设置", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        dialog.dismiss();
                    }).show();
            return;
        }
        PermissionUtil.Companion.permission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE
        ).onResult(new PermissionUtil.CallBack() {
            @Override
            public void onRequestPermissionsResult(int i, @NonNull Map<String, Integer> map, boolean b) {
                mViewModel.loadNearbyWifiList();
                if (!b) {
                    // 跳转去打开位置权限
                    ToastUtils.show("需要位置权限才能获取WiFi列表");
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(android.net.Uri.parse("package:" + getContext().getPackageName()));
                    startActivity(intent);
                }
            }
        }).call(getContext());
    }

    private void loadNetInfo() {
        //获取定位权限
        PermissionUtil.Companion.permission(
                Manifest.permission.ACCESS_FINE_LOCATION
        ).onResult(new PermissionUtil.CallBack() {
            @Override
            public void onRequestPermissionsResult(int i, @NonNull Map<String, Integer> map, boolean b) {
                if (b) {
                    mViewModel.getNetInfo();
                } else {
                    // 跳转去打开位置权限
                    ToastUtils.show("需要位置权限才能获取WiFi列表");
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(android.net.Uri.parse("package:" + getContext().getPackageName()));
                    startActivity(intent);
                }
            }
        }).call(getContext());
    }

    private void initViewModelObserver() {
        mViewModel.getNetInfoLive().observe(getViewLifecycleOwner(), netInfo -> {
            if (netInfo == null) {
                return;
            }
            mBinding.tvNetInfo.setText(netInfo.toString());
        });
    }

    @Override
    public boolean onBackPressed() {
        if (mViewModel.isConfigChanged()) {
            return super.onBackPressed();
        }
        DialogUtil.buildAlertDialog(getContext())
                .setTitle("提示")
                .setMessage("是否保存修改？")
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                    getActivity().finish();
                })
                .setPositiveButton("保存", (dialog, which) -> {
                    mViewModel.saveConfig();
                    dialog.dismiss();
                    getActivity().finish();
                })
                .setOnDismissListener(dialog -> {

                }).show();
        return true;
    }
}