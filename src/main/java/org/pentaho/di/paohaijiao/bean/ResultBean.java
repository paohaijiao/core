package org.pentaho.di.paohaijiao.bean;

import lombok.Data;

/**
 * @program: core
 * @description: 描述
 * @author: 泡海椒
 * @create: 2020-04-20 12:26
 **/
@Data
public class ResultBean {
    private Integer code;
    private String message;
    private Object data;
}
