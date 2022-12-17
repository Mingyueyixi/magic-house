package com.lu.magic.frame.xp.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import com.lu.magic.frame.xp.bean.ContractRequest;
import com.lu.magic.frame.xp.bean.ContractResponse;

import java.io.InvalidClassException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ContractUtil {


    public static Uri buildUri(String baseUri, String tableName, String path) {
        Uri.Builder builder = Uri.parse(baseUri).buildUpon();
        return builder.path(path).appendQueryParameter(DtoKey.TABLE, tableName).build();
    }

    public static <T> ContractResponse<T> request(ContentResolver resolver, Uri uri, ContractRequest req, Class<T> dataCls) {
        Bundle bundleRequest = toRequestBundle(req);
        Bundle bundleResponse = null;
        try {
            bundleResponse = resolver.call(uri, req.mode, req.table, bundleRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ContractResponse<T> response = ContractUtil.toContractResponse(bundleResponse, dataCls);
        try {
            checkResultThrow(response);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return response;
    }

    private static <T> ContractResponse<T> toContractResponse(Bundle bundle, Class<T> dataClass) {
        ContractResponse<T> response = new ContractResponse<>();
        if (bundle == null) {
            return response;
        }
        response.exception = (Throwable) bundle.getSerializable(DtoKey.THROW);
        Object dataValue = bundle.get(DtoKey.DATA);
        if (dataValue != null) {
            response.data = dataClass.cast(dataValue);
        }
        return response;
    }

    private static void checkResultThrow(ContractResponse<?> result) throws Throwable {
        if (result == null || result.exception == null) {
            return;
        }
        throw result.exception;
    }

    public static ContractRequest toContractRequest(Bundle bundle) {
        String preferenceId = bundle.getString(DtoKey.PREFERENCE_ID);
        String mode = bundle.getString(DtoKey.MODE);
        String table = bundle.getString(DtoKey.TABLE);
        String group = bundle.getString(DtoKey.GROUP);
        Parcelable[] parcelableArray = bundle.getParcelableArray(DtoKey.ACTIONS);

        ArrayList<ContractRequest.Action<?>> actions = new ArrayList<>();
        for (Parcelable parcelable : parcelableArray) {
            Bundle ele = (Bundle) parcelable.getClass().cast(parcelable);
            String function = ele.getString(DtoKey.FUNCTION);
            String key = ele.getString(DtoKey.KEY);
            Object value = ele.get(DtoKey.VALUE);
            actions.add(new ContractRequest.Action<>(function, key, value));
        }
        return new ContractRequest(preferenceId, mode, table, group, actions);
    }

    public static Bundle toRequestBundle(ContractRequest request) {
        Bundle bundle = new Bundle();
        bundle.putString(DtoKey.PREFERENCE_ID, request.preferenceId);
        bundle.putString(DtoKey.MODE, request.mode);
        bundle.putString(DtoKey.TABLE, request.table);
        bundle.putString(DtoKey.GROUP, request.group);
        bundle.putParcelableArray(DtoKey.ACTIONS, toBundleArrayAction(request.actions));
        return bundle;
    }

    public static Bundle toResponseBundle(ContractResponse<?> response) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DtoKey.THROW, response.exception);
        if (response.data instanceof Serializable) {
            bundle.putSerializable(DtoKey.DATA, (Serializable) response.data);
        } else if (response.data instanceof Parcelable) {
            bundle.putParcelable(DtoKey.DATA, (Parcelable) response.data);
        } else {
            try {
                throw new InvalidClassException(response.data.getClass() + " is not support !!!");
            } catch (Exception e) {
                e.printStackTrace();
                bundle.putSerializable(DtoKey.THROW, response.exception);
            }
        }
        return bundle;
    }

    public static Bundle[] toBundleArrayAction(Collection<ContractRequest.Action<?>> actions) {
        Iterator<ContractRequest.Action<?>> it = actions.iterator();
        Bundle[] bundleActionArray = new Bundle[actions.size()];
        int index = 0;
        while (it.hasNext()) {
            ContractRequest.Action<?> action = it.next();
            Bundle bundleAction = new Bundle();
            bundleAction.putString(DtoKey.FUNCTION, action.function);
            bundleAction.putString(DtoKey.KEY, action.key);
            if (action.value instanceof Serializable) {
                bundleAction.putSerializable(DtoKey.VALUE, (Serializable) action.value);
            } else if (action.value instanceof Parcelable) {
                bundleAction.putParcelable(DtoKey.VALUE, (Parcelable) action.value);
            } else {
                try {
                    throw new InvalidClassException(action.value.getClass() + " is not support! ");
                } catch (InvalidClassException e) {
                    e.printStackTrace();
                }
            }
            bundleActionArray[index] = bundleAction;
            index++;

        }
        return bundleActionArray;
    }
}
