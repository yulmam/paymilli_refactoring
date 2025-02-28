package com.paymilli.paymilli.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"code", "message", "result"})
public class BaseResponse<T> {
    private final int code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public BaseResponse(T result) {
        this.message = BaseResponseStatus.SUCCESS.getMessage();
        this.code = BaseResponseStatus.SUCCESS.getCode();
        this.result = result;
    }


    public BaseResponse(BaseResponseStatus status, T result) {
        this.message = status.getMessage();
        this.code = status.getCode();
        this.result = result;
    }

    public BaseResponse(BaseResponseStatus status) {
        this.message = status.getMessage();
        this.code = status.getCode();
    }

    public static BaseResponse<Void> ok() {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

    public static BaseResponse<Void> successCreating() {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS_CREATING);
    }
}

