package org.pentaho.di.paohaijiao.bean;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.paohaijiao.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @program: core
 * @description: 描述
 * @author: 泡海椒
 * @create: 2020-04-20 12:15
 **/
public class Constant extends Const {
    /** Public */
    public static final String UKETTLE = "resource/amos.properties";
    public static final String[] typeCodes = new String[]{"-", "Number", "String", "Date", "Boolean", "Integer", "BigNumber", "Serializable", "Binary", "Timestamp", "Internet Address"};
    public static final String SUCCESS = "执行成功";
    public static final String FAIL = "执行失败";
    public static final String CONNECTFAIL = "连接失败";



    public static String AMOS_PLUGIN;
    public static String AMOS_SCRIPT;
    public static LogLevel AMOS_LOGLEVEL;

    public static String AMOS_HOME;

    public static Properties props;

    static {
        props = readProperties();
        AMOS_HOME= Util.getAmosConfigure();
        System.out.println("start Configuration#################################"+AMOS_HOME);
        if(null==AMOS_HOME||"".equalsIgnoreCase(AMOS_HOME)){
            System.err.println("没有正确配置AMOS环境");
        }
        AMOS_PLUGIN =AMOS_HOME+ File.separator+ props.getProperty("amos.plugin");
        AMOS_SCRIPT = uKettle()
                + props.getProperty("amos.script");
        AMOS_LOGLEVEL = logger(props
                .getProperty("amos.loglevel"));

    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static void set(Properties p) {
        props = p;
    }

    public static Properties readProperties() {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(Constant.class.getResource("/")
                    .getPath().replace("%20", " ")
                    + UKETTLE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    public static LogLevel logger(String level) {
        LogLevel logLevel = null;
        if ("basic".equals(level)) {
            logLevel = LogLevel.BASIC;
        } else if ("detail".equals(level)) {
            logLevel = LogLevel.DETAILED;
        } else if ("error".equals(level)) {
            logLevel = LogLevel.ERROR;
        } else if ("debug".equals(level)) {
            logLevel = LogLevel.DEBUG;
        } else if ("minimal".equals(level)) {
            logLevel = LogLevel.MINIMAL;
        } else if ("rowlevel".equals(level)) {
            logLevel = LogLevel.ROWLEVEL;
        } else if ("Nothing".endsWith(level)){
            logLevel = LogLevel.NOTHING;
        }else {
            logLevel = AMOS_LOGLEVEL;
        }
        return logLevel;
    }

    private static String uKettle() {
        String classPath = Constant.class.getResource("/").getPath()
                .replace("%20", " ");
        String iQuartz = "";
        String index = "WEB-INF";
        if (classPath.indexOf("target") > 0) {
            index = "target";
        }
        // windows path
        if ("\\".equals(Constant.FILE_SEPARATOR)) {
            iQuartz = classPath.substring(1, classPath.indexOf(index));
            iQuartz = iQuartz.replace("/", "\\");
        }
        // linux path
        if ("/".equals(Constant.FILE_SEPARATOR)) {
            iQuartz = classPath.substring(0, classPath.indexOf(index));
            iQuartz = iQuartz.replace("\\", "/");
        }
        return iQuartz;
    }
}
