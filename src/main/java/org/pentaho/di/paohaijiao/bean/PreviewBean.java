package org.pentaho.di.paohaijiao.bean;

import lombok.Data;

import java.util.List;

/**
 * @program: core
 * @description: 描述
 * @author: 泡海椒
 * @create: 2020-04-20 12:27
 **/
@Data
public class PreviewBean {
    private List<Object[]> rowBuffer;
    private List<ValueMetaInterface> rowBufferMeta;
}
