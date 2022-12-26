package com.lu.magic.frame.xp.bean;

public class ContractResponse<T> {
    public T data;
    public Throwable exception;
    public String responseId;

    public ContractResponse() {
    }

    public ContractResponse(T data, Throwable exception) {
        this.data = data;
        this.exception = exception;
    }

    public void setData(T data) {
        this.data = data;
    }
}
