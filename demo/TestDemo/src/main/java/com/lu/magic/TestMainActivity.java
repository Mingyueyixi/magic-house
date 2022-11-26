package com.lu.magic;

import android.os.Bundle;
import android.os.VibratorManager;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.VibrateUtils;
import com.lu.magic.testdemo.databinding.LayoutMainBinding;
import com.lu.magic.ui.BaseActivity;

public class TestMainActivity extends BaseActivity {
    private LayoutMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LayoutMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnClickVibrate.setOnClickListener((v) -> {
            VibrateUtils.vibrate(75L);
        });
    }
}
