package com.lu.code.magic.util.load;

import android.graphics.drawable.Drawable;
import android.view.View;

public interface IImageLoader extends ILoadWorker<Drawable> {
    void into(View view);
}
