package org.pentaho.di.paohaijiao.util;

import java.util.Iterator;
import java.util.Map;

/**
 * @program: core
 * @description: 描述
 * @author: 泡海椒
 * @create: 2020-04-20 12:17
 **/
public class Util {
    /**
     * 获取环境配置
     * @return
     */
    public static String getAmosConfigure(){
        Map map = System.getenv();
        Iterator it = map.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry entry = (Map.Entry)it.next();
            if("AMOS_HOME".equalsIgnoreCase((String)entry.getKey())){
                return (String)entry.getValue();
            }
        }
        return null;
    }
}
