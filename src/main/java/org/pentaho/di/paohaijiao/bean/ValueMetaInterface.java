package org.pentaho.di.paohaijiao.bean;

import lombok.Data;

/**
 * @program: core
 * @description: 描述
 * @author: 泡海椒
 * @create: 2020-04-20 12:21
 **/
@Data
public class ValueMetaInterface {
    /**
     * 字段名
     */
    private String name;
    /**
     * 字段注释
     */
    private String comments;
    /**
     * 字段类型
     */
    private String type;
}
