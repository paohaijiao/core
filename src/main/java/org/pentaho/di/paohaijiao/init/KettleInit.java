package org.pentaho.di.paohaijiao.init;

import org.pentaho.di.core.JndiUtil;
import org.pentaho.di.core.KettleVariablesList;
import org.pentaho.di.core.auth.AuthenticationConsumerPluginType;
import org.pentaho.di.core.auth.AuthenticationProviderPluginType;
import org.pentaho.di.core.compress.CompressionPluginType;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.lifecycle.KettleLifecycleSupport;
import org.pentaho.di.core.logging.ConsoleLoggingEventListener;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogTablePluginType;
import org.pentaho.di.core.plugins.*;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.paohaijiao.bean.Constant;
import org.pentaho.di.trans.step.RowDistributionPluginType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @program: core
 * @description: 描述
 * @author: 泡海椒
 * @create: 2020-04-20 12:14
 **/
public class KettleInit {
    private static Class<?> PKG = Constant.class;
    private static Boolean initialized;


    public static void init() throws KettleException {
        init(true);
    }
    public static void init(boolean simpleJndi) throws KettleException {
        if (initialized == null) {
            // 创建一个Kettle "home" 的目录
            // createKettleHome();
            // 初始化 kettle.properties 初始化其他属性等
            environmentInit();
            // 初始化日志
            KettleLogStore.init();
            // 设置控制台日志级用来调试
            KettleLogStore.getAppender().addLoggingEventListener(new ConsoleLoggingEventListener());
            // 配置简单的JNDI 仅供我们在单机模式运行
            if (simpleJndi) {
                JndiUtil.initJNDI();
            }
            // 注册原生类型和各个所需的插件
            PluginRegistry.addPluginType(RowDistributionPluginType.getInstance());
            PluginRegistry.addPluginType(LogTablePluginType.getInstance());
            PluginRegistry.addPluginType(CartePluginType.getInstance());
            PluginRegistry.addPluginType(CompressionPluginType.getInstance());
            PluginRegistry.addPluginType(AuthenticationProviderPluginType.getInstance());
            PluginRegistry.addPluginType(AuthenticationConsumerPluginType.getInstance());
            PluginRegistry.addPluginType(StepPluginType.getInstance());
            PluginRegistry.addPluginType(PartitionerPluginType.getInstance());
            PluginRegistry.addPluginType(JobEntryPluginType.getInstance());
            PluginRegistry.addPluginType(RepositoryPluginType.getInstance());
            PluginRegistry.addPluginType(DatabasePluginType.getInstance());
            PluginRegistry.addPluginType(LifecyclePluginType.getInstance());
            PluginRegistry.addPluginType(KettleLifecyclePluginType.getInstance());
            PluginRegistry.addPluginType(ImportRulePluginType.getInstance());
            PluginRegistry.init();
//			StepPluginType.getInstance().getPluginFolders().add(new PluginFolder("E:\\haha\\plugins\\pentaho-big-data-plugin",false,true));
            // 初始化读取的变量列表。
            KettleVariablesList.init();
            // 初始化生命周期监听器
            initLifecycleListeners();
            initialized = true;
        }
    }


    private static void initLifecycleListeners() throws KettleException {
        final KettleLifecycleSupport s = new KettleLifecycleSupport();
        s.onEnvironmentInit();
        // 注册关闭hook监听的调用OnExit()方法
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    s.onEnvironmentShutdown();
                } catch (Exception e) {
                    System.err.println(BaseMessages
                            .getString(PKG,
                                    "LifecycleSupport.ErrorInvokingKettleEnvironmentShutdownListeners"));
                    e.printStackTrace();
                }
            };
        });

    }

    /**
     * 创建Kettle home,这是一个目录,其中包含一个默认的 kettle.properties 属性文件等
     */
    public static void createKettleHome() {
        // 尝试创建目录
        String directory = Constant.getKettleDirectory();
        File dir = new File(directory);
        try {
            dir.mkdirs();
            // 创建属性文件 kettle.properties
            createDefaultKettleProperties(directory);
        } catch (Exception e) {
        }
    }

    /**
     * 创建默认的 kettle properties 文件
     *
     * @param
     *
     */
    private static void createDefaultKettleProperties(String directory) {

        String kpFile = directory + Constant.FILE_SEPARATOR + Constant.UKETTLE;
        File file = new File(kpFile);
        if (!file.exists()) {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                out.write(Constant.getKettlePropertiesFileHeader().getBytes());
            } catch (IOException e) {
                System.err
                        .println(BaseMessages
                                .getString(
                                        PKG,
                                        "Props.Log.Error.UnableToCreateDefaultKettleProperties.Message",
                                        Constant.UKETTLE, kpFile));
                System.err.println(e.getStackTrace());
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        System.err
                                .println(BaseMessages
                                        .getString(
                                                PKG,
                                                "Props.Log.Error.UnableToCreateDefaultKettleProperties.Message",
                                                Constant.UKETTLE, kpFile));
                        System.err.println(e.getStackTrace());
                    }
                }
            }
        }
    }

    /**
     * Checks if the Kettle environment has been initialized.
     *
     * @return true if initialized, false otherwise
     */
    /**
     * 检查Kettle 环境初始化。
     *
     * @return 返回 true or false
     */
    public static boolean isInitialized() {
        if (initialized == null)
            return false;
        else
            return true;
    }

    /**
     * 加载插件注册表。
     *
     * @throws KettlePluginException
     *             加载插件如果遇到任何错误返回KettlePluginException
     */
    public void loadPluginRegistry() throws KettlePluginException {

    }

    /**
     * 初始化环境变量。
     *
     * @throws KettleException
     *             初始化如果遇到任何错误返回KettleException
     */
    public static void environmentInit() throws KettleException {
        if (Thread.currentThread().getContextClassLoader() == null) {
            Thread.currentThread().setContextClassLoader(
                    ClassLoader.getSystemClassLoader());
        }
        Map<?, ?> prop = Constant.readProperties();
        Variables variables = new Variables();
        for (Object key : prop.keySet()) {
            String variable = (String) key;
            String value = variables.environmentSubstitute((String) prop
                    .get(key));
            variables.setVariable(variable, value);
        }
        for (String variable : variables.listVariables()) {
            System.setProperty(variable, variables.getVariable(variable));
        }

        System.getProperties().put("KETTLE_HOME", Constant.AMOS_HOME);
        System.getProperties().put("KETTLE_PLUGIN_BASE_FOLDERS",
                Constant.AMOS_PLUGIN);
        System.getProperties().put("KETTLE_JS_HOME", Constant.AMOS_SCRIPT);
        System.getProperties()
                .put(Constant.INTERNAL_VARIABLE_CLUSTER_SIZE, "1");
        System.getProperties().put(
                Constant.INTERNAL_VARIABLE_SLAVE_SERVER_NUMBER, "0");
        System.getProperties().put(
                Constant.INTERNAL_VARIABLE_SLAVE_SERVER_NAME,
                "slave-trans-name");
        System.getProperties().put(Constant.INTERNAL_VARIABLE_STEP_COPYNR, "0");
        System.getProperties().put(Constant.INTERNAL_VARIABLE_STEP_NAME,
                "step-name");
        System.getProperties().put(
                Constant.INTERNAL_VARIABLE_STEP_PARTITION_ID, "partition-id");
        System.getProperties().put(
                Constant.INTERNAL_VARIABLE_STEP_PARTITION_NR, "0");
        System.getProperties().put(
                Constant.INTERNAL_VARIABLE_STEP_UNIQUE_COUNT, "1");
        System.getProperties().put(
                Constant.INTERNAL_VARIABLE_STEP_UNIQUE_NUMBER, "0");
    }
}
