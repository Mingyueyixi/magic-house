package com.lu.code.magic.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
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
        return builder.path(path).appendQueryParameter("table", tableName).build();
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
        response.exception = (Throwable) bundle.getSerializable("throw");
        Object dataValue = bundle.get("data");
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
        String mode = bundle.getString("mode");
        String group = bundle.getString("group");
        Bundle[] bundleActions = (Bundle[]) bundle.get("actions");
        ArrayList<ContractRequest.Action<?>> actions = new ArrayList<>();
        for (Bundle ele : bundleActions) {
            String function = ele.getString("function");
            String key = ele.getString("key");
            Object value = ele.get("value");
            actions.add(new ContractRequest.Action<>(function, key, value));
        }
        return new ContractRequest(mode, group, group, actions);
    }

    public static Bundle toBundleRequest(ContractRequest request) {
        Bundle bundle = new Bundle();
        bundle.putString("mode", request.mode);
        bundle.putString("group", request.group);
        bundle.putParcelableArray("actions", toBundleArrayAction(request.actions));
        return bundle;
    }

    public static Bundle toBundleResponse(ContractResponse<?> response) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("throw", response.exception);
        if (response.data instanceof Serializable) {
            bundle.putSerializable("data", (Serializable) response.data);
        } else if (response.data instanceof Parcelable) {
            bundle.putParcelable("data", (Parcelable) response.data);
        } else {
            try {
                throw new InvalidClassException(response.data.getClass() + " is not support !!!");
            } catch (InvalidClassException e) {
                e.printStackTrace();
                bundle.putSerializable("throw", response.exception);
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
            bundleAction.putString("function", action.function);
            bundleAction.putString("key", action.key);
            if (action.value instanceof Serializable) {
                bundleAction.putSerializable("value", (Serializable) action.value);
            } else if (action.value instanceof Parcelable) {
                bundleAction.putParcelable("value", (Parcelable) action.value);
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
