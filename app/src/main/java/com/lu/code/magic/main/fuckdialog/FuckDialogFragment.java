package com.lu.code.magic.main.fuckdialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.lu.code.magic.bean.FuckDialogConfig;
import com.lu.code.magic.magic.databinding.FragmentFuckDialogBinding;
import com.lu.code.magic.main.AppListModel;
import com.lu.code.magic.main.MagicConfigViewModel;
import com.lu.code.magic.ui.BindingFragment;
import com.lu.code.magic.util.ConfigUtil;
import com.lu.code.magic.util.log.LogUtil;

/**
 * @author lu
 */
public class FuckDialogFragment extends BindingFragment<FragmentFuckDialogBinding> {

    private MagicConfigViewModel magicViewModel;
    private FuckDialogConfig config;

    @Nullable
    @Override
    public FragmentFuckDialogBinding onViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @NonNull FragmentFuckDialogBinding binding = FragmentFuckDialogBinding.inflate(inflater, container, false);
        return binding;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        magicViewModel = new ViewModelProvider(getActivity()).get(MagicConfigViewModel.class);
        LogUtil.d(magicViewModel.getAppListModel());
        AppListModel model = magicViewModel.getAppListModel();
        String packageName = model.getPackageName();

        config = ConfigUtil.getFuckDialogConfig(packageName);

        if (config == null) {
            config = new FuckDialogConfig(false, "", new FuckDialogConfig.NormalModeDTO(), new FuckDialogConfig.RegexModeDTO());
            config.setMode("normal");
        }
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
            ConfigUtil.setFuckDialogConfig(model.getPackageName(), config);
        });

    }
}
