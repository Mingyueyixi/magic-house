package com.lu.magic;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.lu.magic.arts.BaseMagic;
import com.lu.magic.main.SelectAppActivity;
import com.lu.magic.store.ItemModel;

public interface IModuleFace {

    BaseMagic loadMagic();

    default void onEntry(Context context, ItemModel itemModel) {
        SelectAppActivity.start(context, itemModel);
    }

    default IFragmentFactory getDetailFragmentFactory() {
        return null;
    }

    class ModuleEmpty implements IModuleFace {
        public static final String MODULE_KEY = "";
        @Override
        public BaseMagic loadMagic() {
            return null;
        }
    }

    interface IFragmentFactory {
        Fragment create();
    }
}
