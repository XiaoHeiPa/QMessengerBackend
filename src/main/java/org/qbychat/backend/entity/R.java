package org.qbychat.backend.entity;

import lombok.Data;

@Data
public class R<T> {
    private int code;
    private String message;
    private T data;
    private Long timestamp;
    public R(){
        this.timestamp = System.currentTimeMillis();
    }
    public static <T> R<T> success(T data){
        R<T> r = new R<>();
        r.setCode(ReturnCodeEnum.RC200.getCode());
        r.setMessage(ReturnCodeEnum.RC200.getMessage());
        r.setData(data);
        return r;
    }
    public static <T> R<T> error(int code, String msg){
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(msg);
        r.setData(null);
        return r;
    }
}
