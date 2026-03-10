package com.tengman.db26.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {

    int status;
    String msg;
    Object content;

    public static BaseResponse fail(Object data) {
        return new BaseResponse(0, "失败", data);
    }

    public static BaseResponse success(Object data) {
        return new BaseResponse(200, "成功", data);
    }

}
