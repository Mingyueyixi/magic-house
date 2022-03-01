package com.lu.code.foolish.egg.main.plugin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lu.code.foolish.egg.databinding.FragmentFuckDialogBinding;
import com.lu.code.foolish.egg.ui.BindingFragment;

/**
 * @author lu
 */
public class FuckDialogFragment extends BindingFragment<FragmentFuckDialogBinding> {

    @Nullable
    @Override
    public FragmentFuckDialogBinding onViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @NonNull FragmentFuckDialogBinding binding = FragmentFuckDialogBinding.inflate(inflater, container, false);
        return binding;
    }

}
