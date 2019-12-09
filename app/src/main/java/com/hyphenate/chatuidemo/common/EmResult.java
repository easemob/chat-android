package com.hyphenate.chatuidemo.common;


import com.hyphenate.chat.EMBase;

/**
 * 结果基础类
 * @param <T> 请求结果的实体类
 */
public class EmResult<T> extends EMBase<T> {
    public int code;
    public T result;

    public EmResult(){
    }

    public EmResult(int code, T result) {
        this.code = code;
        this.result = result;
    }

    public EmResult(int code){
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
        return code == EmErrorCode.EM_NO_ERROR;
    }

}
