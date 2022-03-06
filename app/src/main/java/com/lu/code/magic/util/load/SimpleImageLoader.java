package com.lu.code.magic.util.load;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.collection.LruCache;

public class SimpleImageLoader {
    private MemoryRepositoryImp mCachePoolImp;

    public SimpleImageLoader() {
        this(new LruCache<>(4096));
    }

    public SimpleImageLoader(LruCache<String, Object> pools) {
        mCachePoolImp = new MemoryRepositoryImp(pools);
    }

    public ILoadWorker<Drawable> with() {
        return new ImageLoaderImp(mCachePoolImp);
    }

    public static class ImageLoaderImp extends MemoryLoaderWorker<Drawable> {
        public ImageLoaderImp(MemoryRepositoryImp loaderPool) {
            super(loaderPool);
        }


        public void into(View view) {
            realLoad(new LoadTarget<Drawable>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onComplete(Drawable drawable) {
                    view.setBackground(drawable);
                }
            });
        }

        public void into(ImageView view) {
            realLoad(new LoadTarget<Drawable>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onComplete(Drawable drawable) {
                    view.setImageDrawable(drawable);
                }
            });
        }
    }

}
