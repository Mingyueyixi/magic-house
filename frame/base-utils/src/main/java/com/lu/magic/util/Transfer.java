package com.lu.magic.util;

public class Transfer<S, T> {
    private S source;
    private T target;

    public Transfer() {
    }

    public Transfer(S source, T target) {
        this.source = source;
        this.target = target;
    }

    public S getSource() {
        return source;
    }

    public void setSource(S source) {
        this.source = source;
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }


    @Override
    public String toString() {
        return "Transfer{" +
                "source=" + source +
                ", target=" + target +
                '}';
    }
}
