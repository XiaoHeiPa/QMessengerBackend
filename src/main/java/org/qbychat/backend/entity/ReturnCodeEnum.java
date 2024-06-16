package org.qbychat.backend.entity;

import lombok.Getter;

@Getter
public enum ReturnCodeEnum {
    RC200(200, "OK"),
    RC400(400, "请求失败，参数错误。"),
    RC404(404, "请求失败，没有找到所需的内容。"),
    RC405(405, "请求失败，请求方法错误。"),
    RC500(500, "请求失败，服务器繁忙或发生内部错误。");
    private final int code;
    private final String message;
    ReturnCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
