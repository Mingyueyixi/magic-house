package com.lu.magic.fuckdialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lu.magic.bean.Config;
import com.lu.magic.bean.FuckDialogData;
import com.lu.magic.fuckdialog.databinding.FragmentFuckDialogBinding;
import com.lu.magic.main.AppListModel;
import com.lu.magic.store.ItemModel;
import com.lu.magic.store.TitleModel;
import com.lu.magic.ui.BindingFragment;
import com.lu.magic.util.SingleClassStoreUtil;
import com.lu.magic.config.ConfigUtil;

/**
 * @author lu
 */
public class FuckDialogFragment extends BindingFragment<FragmentFuckDialogBinding> {
    private Config<FuckDialogData> mConfig;
    private AppListModel appListModel;
    private ItemModel routeItem;

    @Nullable
    @Override
    public FragmentFuckDialogBinding onViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @NonNull FragmentFuckDialogBinding binding = FragmentFuckDialogBinding.inflate(inflater, container, false);
        appListModel = SingleClassStoreUtil.get(AppListModel.class);
        routeItem = SingleClassStoreUtil.get(TitleModel.class);

        String packageName = appListModel.getPackageName();
        mConfig = ConfigUtil.getFuckDialogConfig(packageName);

        if (mConfig == null) {
            mConfig = new Config<>(false, new FuckDialogData());
            mConfig.getData().setMode("normal");
        }
        FuckDialogData data = mConfig.getData();
        if (data.getRegexMode() == null) {
            data.setRegexMode(new FuckDialogData.RegexModeDTO());
        }
        binding.etSearchKeyWord.setText(data.getKeyword());
        switch (data.getMode()) {
            case "normal":
                binding.rbNormalMode.setChecked(true);
                break;
            case "regex":
                binding.rbRegexMode.setChecked(true);
            default:
                break;
        }

        binding.cbDotLineOption.setChecked(data.getRegexMode().isDotLine());
        binding.sbOpenTip.setChecked(data.isPromptTip());
        binding.sbStrongMode.setChecked(data.isStrongHide());

        binding.sbStrongMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            data.setStrongHide(isChecked);
        });
        binding.sbOpenTip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            data.setPromptTip(isChecked);
        });
        return binding;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBinding().btnConfirm.setOnClickListener(v -> {
            String kw = getBinding().etSearchKeyWord.getText() + "";
            FuckDialogData data = mConfig.getData();
            data.setKeyword(kw);
            if (getBinding().rbNormalMode.isChecked()) {
                data.setMode("normal");
            } else {
                data.setMode("regex");
            }
            if (getBinding().cbDotLineOption.isChecked()) {
                data.getRegexMode().setDotLine(true);
            } else {
                data.getRegexMode().setDotLine(false);
            }
            ConfigUtil.setFuckDialogConfig(appListModel.getPackageName(), mConfig);
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
