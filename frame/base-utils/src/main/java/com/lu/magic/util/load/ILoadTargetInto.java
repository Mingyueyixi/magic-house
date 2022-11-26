package com.lu.magic.util.load;


public interface ILoadTargetInto<E> {
    void onStart();

    void onComplete(E target);
}
