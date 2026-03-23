package com.grupo6daw.lcdd_daw.model;

public class Error {
    private String errorMsg;
    private int errorCode;

    public Error(String errorMsg, int errorCode) {
        super();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
