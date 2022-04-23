package com.lu.code.magic.main.screen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewStateModel extends ViewModel {
    private MutableLiveData<ViewState> viewStateLiveData = new MutableLiveData<>();

    public void setViewState(ViewState viewState) {
        if (viewState == null) {
            return;
        }
        viewStateLiveData.setValue(viewState);
    }

    public ViewState getViewState() {
        ViewState v = viewStateLiveData.getValue();
        if (v == null) {
            v = new ViewState();
        }
        return v;
    }

    public void setEditActItem(OrientationDTO.ActItem actItem) {
        ViewState viewState = getViewState();
        viewState.editActItem = actItem;
        setViewState(viewState);
    }

    public void setEditActItemPosition(int position) {
        ViewState viewState = getViewState();
        viewState.editActItemPosition = position;
        setViewState(viewState);
    }

    public LiveData<ViewState> getViewStateLiveData() {
        return viewStateLiveData;
    }

    public class ViewState {
        OrientationDTO.ActItem editActItem;
        int editActItemPosition;
    }
}
