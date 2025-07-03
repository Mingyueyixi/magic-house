package com.lu.magic.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Lu
 * @date 2025/7/4 0:57
 * @description
 */
public class RecyclerViewUtil {

    public static EveryAdapterDataObserver registerEveryChangeAdapterDataObserver(LifecycleOwner lifecycleObserver, RecyclerView.Adapter<?> adapter, OnEveryChangeListener listener) {
        EveryAdapterDataObserver result = new EveryAdapterDataObserver(){
            @Override
            public void onEveryChange() {
                if (listener != null) {
                    listener.onEveryChange();
                }
            }
        };

        lifecycleObserver.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    adapter.unregisterAdapterDataObserver(result);
                }
            }
        });
        adapter.registerAdapterDataObserver(result);
        return result;
    }


    public static interface OnEveryChangeListener {
        void onEveryChange();
    }


    public static class EveryAdapterDataObserver extends RecyclerView.AdapterDataObserver implements OnEveryChangeListener {
        public EveryAdapterDataObserver() {

        }

        @Override
        public void onChanged() {
            super.onChanged();
            onEveryChange();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            onEveryChange();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            onEveryChange();

        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            onEveryChange();

        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            onEveryChange();

        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            onEveryChange();

        }

        @Override
        public void onStateRestorationPolicyChanged() {
            super.onStateRestorationPolicyChanged();
            onEveryChange();
        }

        @Override
        public void onEveryChange() {

        }
    }

}
