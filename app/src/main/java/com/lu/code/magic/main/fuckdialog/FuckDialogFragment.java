package com.lu.code.magic.main.fuckdialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lu.code.magic.bean.FuckDialogConfig;
import com.lu.code.magic.databinding.FragmentFuckDialogBinding;
import com.lu.code.magic.main.AppListModel;
import com.lu.code.magic.store.ItemModel;
import com.lu.code.magic.store.TitleModel;
import com.lu.code.magic.ui.BindingFragment;

import com.lu.code.magic.util.SingleStoreUtil;
import com.lu.code.magic.util.config.ConfigUtil;

/**
 * @author lu
 */
public class FuckDialogFragment extends BindingFragment<FragmentFuckDialogBinding> {

    private FuckDialogConfig config;
    private AppListModel appListModel;
    private ItemModel routeItem;

    @Nullable
    @Override
    public FragmentFuckDialogBinding onViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @NonNull FragmentFuckDialogBinding binding = FragmentFuckDialogBinding.inflate(inflater, container, false);
        appListModel = SingleStoreUtil.get(AppListModel.class);
        routeItem = SingleStoreUtil.get(TitleModel.class);

        String packageName = appListModel.getPackageName();
        config = ConfigUtil.getFuckDialogConfig(packageName);

        if (config == null) {
            config = new FuckDialogConfig();
            config.setMode("normal");
        }
        binding.etSearchKeyWord.setText(config.getKeyword());
        switch (config.getMode()) {
            case "normal":
                binding.rbNormalMode.setChecked(true);
                break;
            case "regex":
                binding.rbRegexMode.setChecked(true);
            default:
                break;
        }

        binding.cbDotLineOption.setChecked(config.getRegexMode().isDotLine());
        binding.sbOpenTip.setChecked(config.isPromptTip());
        binding.sbStrongMode.setChecked(config.isStrongHide());

        binding.sbStrongMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            config.setStrongHide(isChecked);
        });
        binding.sbOpenTip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            config.setPromptTip(isChecked);
        });
        return binding;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBinding().btnConfirm.setOnClickListener(v -> {
            String kw = getBinding().etSearchKeyWord.getText() + "";
            config.setKeyword(kw);
            if (getBinding().rbNormalMode.isChecked()) {
                config.setMode("normal");
            } else {
                config.setMode("regex");
            }
            if (getBinding().cbDotLineOption.isChecked()) {
                config.getRegexMode().setDotLine(true);
            } else {
                config.getRegexMode().setDotLine(false);
            }
            ConfigUtil.setFuckDialogConfig(appListModel.getPackageName(), config);
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
