package com.hyphenate.easeim.common.net;



/**
 * 结果基础类
 * @param <T> 请求结果的实体类
 */
public class Result<T> {
    public int code;
    public T result;

    public Result(){
    }

    public Result(int code, T result) {
        this.code = code;
        this.result = result;
    }

    public Result(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isSuccess(){
        return code == ErrorCode.EM_NO_ERROR;
    }

}
