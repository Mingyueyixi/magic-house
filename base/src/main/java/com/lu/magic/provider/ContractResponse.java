package com.lu.magic.provider;

public class ContractResponse<T> {
    protected T data;
    protected Throwable exception;

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
