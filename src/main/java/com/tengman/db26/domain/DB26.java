package com.tengman.db26.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DB26规则中的信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DB26 {
    /**
     * 0-8是企业信用代码
     */
    String code;
    /**
     * 9-12是项目代码
     */
    String item;
    /**
     * 13-16 生产年月
     */
    String date;
    /**
     * 17-24 生产批次
     */
    String batch;
}
