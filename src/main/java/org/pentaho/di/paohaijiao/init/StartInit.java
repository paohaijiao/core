package org.pentaho.di.paohaijiao.init;

/**
 * @program: core
 * @description: 描述
 * @author: 泡海椒
 * @create: 2020-04-20 12:34
 **/
public class StartInit {
    /**
     * 初始化环境
     * @throws Exception
     */
    public void init() throws Exception {
        //初始化环境***
        KettleInit.environmentInit();
        org.pentaho.di.core.KettleEnvironment.init();
    }
}
