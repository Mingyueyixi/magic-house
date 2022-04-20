package com.lu.code.magic.main.amap;

import android.text.TextUtils;

import com.amap.api.services.core.PoiItem;
import com.lu.code.magic.ui.recycler.MultiAdapter;

public class SearchPoiAdapter extends MultiAdapter<PoiItem> {
    private String beginAddress;
    private int selectedPosition = -1;

    public void setBeginAddress(String address) {
        this.beginAddress = address;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public String getBeginAddress() {
        return beginAddress;
    }

    public String poiItemToString(PoiItem poiItem) {

        if ("regeo".equals(poiItem.getPoiId())) {
            return poiItem.getSnippet();
        }

        if (beginAddress != null) {
            return beginAddress + poiItem.getSnippet();
        }

        StringBuilder builder = new StringBuilder(poiItem.getProvinceName());

        if (!TextUtils.equals(poiItem.getProvinceName(), poiItem.getCityName())) {
            builder.append(poiItem.getCityName());
        }

        builder.append(poiItem.getAdName());

        if (!TextUtils.equals(poiItem.getAdName(), poiItem.getSnippet())) {
            builder.append(poiItem.getSnippet());
        }

        return builder.toString();
    }
}
