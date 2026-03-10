package com.tengman.db26.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * db26项目
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DB26Item {
    /**
     * 项目名称
     */
    String name;
    /**
     * 项目代码
     */
    String code;
    /**
     * 国标号
     */
    String criterion;
}
