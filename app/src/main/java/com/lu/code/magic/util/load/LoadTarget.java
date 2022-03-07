package com.lu.code.magic.util.load;


public interface LoadTarget<E> {
    void onStart();

    void onComplete(E target);
}
