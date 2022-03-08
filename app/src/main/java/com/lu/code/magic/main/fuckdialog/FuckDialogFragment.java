package com.lu.code.magic.main.fuckdialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.lu.code.magic.magic.databinding.FragmentFuckDialogBinding;
import com.lu.code.magic.main.MagicConfigViewModel;
import com.lu.code.magic.ui.BindingFragment;
import com.lu.code.magic.util.log.LogUtil;

/**
 * @author lu
 */
public class FuckDialogFragment extends BindingFragment<FragmentFuckDialogBinding> {

    private MagicConfigViewModel magicViewModel;

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
    }
}
