package com.lu.code.magic.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

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

    public static <T> ContractResponse<T> request(ContentResolver cp, Uri uri, ContractRequest req, Class<T> dataCls) {
        Bundle bundleRequest = toBundleRequest(req);
        Bundle bundleResponse = cp.call(uri, req.mode, req.table, bundleRequest);
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

    public static ContractRequest toRequest(Bundle bundle) {
        String mode = bundle.getString(DtoKey.MODE);
        String group = bundle.getString(DtoKey.GROUP);
        Bundle[] bundleActions = (Bundle[]) bundle.get(DtoKey.ACTIONS);
        ArrayList<ContractRequest.Action<?>> actions = new ArrayList<>();
        for (Bundle ele : bundleActions) {
            String function = ele.getString(DtoKey.FUNCTION);
            String key = ele.getString(DtoKey.KEY);
            Object value = ele.get(DtoKey.VALUE);
            actions.add(new ContractRequest.Action<>(function, key, value));
        }
        return new ContractRequest(mode, group, group, actions);
    }

    public static Bundle toBundleRequest(ContractRequest request) {
        Bundle bundle = new Bundle();
        bundle.putString(DtoKey.MODE, request.mode);
        bundle.putString(DtoKey.GROUP, request.group);
        bundle.putParcelableArray(DtoKey.ACTIONS, toBundleArrayAction(request.actions));
        return bundle;
    }

    public static Bundle toBundleResponse(ContractResponse<?> response) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DtoKey.THROW, response.exception);
        if (response.data instanceof Serializable) {
            bundle.putSerializable(DtoKey.DATA, (Serializable) response.data);
        } else if (response.data instanceof Parcelable) {
            bundle.putParcelable(DtoKey.DATA, (Parcelable) response.data);
        } else {
            try {
                throw new InvalidClassException(response.data.getClass() + " is not support !!!");
            } catch (InvalidClassException e) {
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
