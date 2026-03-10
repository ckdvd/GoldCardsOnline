package com.tengman.db26.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通道检测结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelResult {
    /**
     * 通道号
     */
    int channelId = 1;
    /**
     * 项目名称
     */
    String codeName;

    String limitValue;
    /**
     * 浓度值/检测值
     */
    String result;
    /**
     * 国标
     */
    String criterion;
    /**
     * 结果
     */
    String detectionResult;
    /**
     * 限值
     */
    @JsonIgnore
    float limit = 1.1f;
    /**
     * 0：显色法，1：TC比值法
     */
    @JsonIgnore
    int method;
    /**
     * 灰度图
     */
    @JsonIgnore
    List<Float> grayArray;

    public void setMethod(int method) {
        this.method = method;
        if (method == 1) {
            limit = 1f;
            limitValue = "<=" + limit;
        } else {
            limit = 1.1f;
            limitValue = "<=" + limit;
        }
    }
}
