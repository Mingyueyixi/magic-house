package com.lu.magic.util;

import androidx.core.util.Predicate;

import java.util.ArrayList;
import java.util.List;


public class Select<E> {

    private List<E> dataList;
    private Predicate<E> whereSupplier;

    public static <E> Select bean() {
        return new Select<E>();
    }

    public Select from(List<E> dataList) {
        this.dataList = dataList;
        return this;
    }

    public Select where(Predicate<E> supplier) {
        this.whereSupplier = supplier;
        return this;
    }

    public List<E> commit() {
        ArrayList<E> arrayList = new ArrayList<>();
        for (E ele : this.dataList) {
            if (whereSupplier.test(ele)) {
                arrayList.add(ele);
            }
        }
        return arrayList;
    }
}
