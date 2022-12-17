package com.lu.magic.frame.xp.provider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lu.magic.frame.xp.bean.ContractRequest;
import com.lu.magic.frame.xp.PreferenceServerImpl;
import com.lu.magic.frame.xp.bean.ContractResponse;

/**
 * content://<authority>/<data_type>/<id>
 * Example：
 * content://com.lu.magic/mmkv/
 * content://com.lu.magic/sp/
 *
 * @author Lu
 */
public class PreferenceServerProvider extends BaseCallProvider {
    private PreferenceServerImpl preferenceServerImpl;

    private PreferenceServerImpl initPreferenceImplIfNeed(Context context) {
        if (preferenceServerImpl == null) {
            preferenceServerImpl = new PreferenceServerImpl(context);
        } else if (preferenceServerImpl.getContext() != context) {
            preferenceServerImpl = new PreferenceServerImpl(context);
        }
        return preferenceServerImpl;
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        ContractResponse<?> response = null;
        try {
            initPreferenceImplIfNeed(getContext());
            ContractRequest request = ContractUtil.toContractRequest(extras);
            response = preferenceServerImpl.call(request);
        } catch (Exception e) {
            e.printStackTrace();
            response = new ContractResponse<>(null, e);
        }
        if (response == null) {
            response = new ContractResponse<>();
        }
        return ContractUtil.toResponseBundle(response);
    }

}