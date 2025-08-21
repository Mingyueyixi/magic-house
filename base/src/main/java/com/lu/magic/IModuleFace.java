package com.lu.magic;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.lu.magic.arts.BaseMagic;
import com.lu.magic.main.SelectAppActivity;
import com.lu.magic.store.ItemModel;

public interface IModuleFace {
    /**
     * 创建模块
     *
     * @return
     */
    BaseMagic loadMagic();

    /**
     * 模块入口页面
     *
     * @param context
     * @param itemModel
     */
    default void onEntry(Context context, ItemModel itemModel) {
        SelectAppActivity.start(context, itemModel);
    }

    /**
     * 模块详情页面
     *
     * @return
     */
    default IFragmentFactory getDetailFragmentFactory() {
        return null;
    }

    class ModuleEmpty implements IModuleFace {
        @Override
        public BaseMagic loadMagic() {
            return null;
        }
    }

    interface IFragmentFactory {
        Fragment create();
    }
}
