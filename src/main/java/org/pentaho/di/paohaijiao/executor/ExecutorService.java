package org.pentaho.di.paohaijiao.executor;

import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.ProgressNullMonitorListener;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.paohaijiao.bean.Constant;
import org.pentaho.di.paohaijiao.bean.ParamBean;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.cluster.TransSplitter;
import org.pentaho.di.www.SlaveServerJobStatus;

/**
 * @program: core
 * @description: 描述
 * @author: 泡海椒
 * @create: 2020-04-20 14:55
 **/
public class ExecutorService {
    /**
     * 执行转换
     * @param paramBean
     * @return
     * @throws Exception
     */
    public static String executorTrans(ParamBean paramBean)throws Exception{
            RepositoryDirectoryInterface directory = paramBean.getKettleDatabaseRepository().loadRepositoryDirectoryTree()
                    .findDirectory(paramBean.getDiretory());
            TransMeta transMeta = paramBean.getKettleDatabaseRepository().loadTransformation(paramBean.getName(), directory,
                    new ProgressNullMonitorListener(), true, null);
            Trans trans = new Trans(transMeta);
            trans.setLogLevel(null==paramBean.getLogLevel()?LogLevel.DEBUG:paramBean.getLogLevel());
            try {
                if (null == paramBean.getClusterExecutor() || !paramBean.getClusterExecutor()){
                    trans.execute(null);
                    trans.waitUntilFinished();
                }else{
                    TransExecutionConfiguration config = new TransExecutionConfiguration();
                    config.setExecutingClustered(true);
                    config.setExecutingLocally(false);
                    config.setExecutingRemotely(false);
                    config.setClusterPosting(true);
                    config.setClusterPreparing(true);
                    config.setClusterStarting(true);
                    TransSplitter transSplitter = Trans.executeClustered(transMeta, config);
                    System.out.println(transSplitter.getCarteObjectMap());
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                if (trans.isFinished()) {
                    if (trans.getErrors() > 0) {
                        if (null == trans.getResult().getLogText() || "".equals(trans.getResult().getLogText())){
                        }
                    }
                }
            }

        return Constant.SUCCESS;
    }

    /**
     * 执行作业
     * @param paramBean
     * @return
     * @throws Exception
     */
    public static String executorJobs(ParamBean paramBean)throws Exception{
        try {

            RepositoryDirectoryInterface directory = paramBean.getKettleDatabaseRepository().loadRepositoryDirectoryTree()
                    .findDirectory(paramBean.getDiretory());
            if(null==paramBean.getClusterExecutor()||!paramBean.getClusterExecutor()){
                JobMeta jobMeta = paramBean.getKettleDatabaseRepository().loadJob(paramBean.getName(), directory, new ProgressNullMonitorListener(),
                        null);
                org.pentaho.di.job.Job job = new org.pentaho.di.job.Job(paramBean.getKettleDatabaseRepository(), jobMeta);
                job.setDaemon(true);
                job.run();
                job.waitUntilFinished();
            }else{
                JobMeta jobMeta = paramBean.getKettleDatabaseRepository().loadJob(paramBean.getName(), directory, new ProgressNullMonitorListener(),
                        null);
                JobExecutionConfiguration jobExecutionConfiguration = new JobExecutionConfiguration();
                jobExecutionConfiguration.setRemoteServer(paramBean.getSlaveServer());// 配置远程服务
                String lastCarteObjectId = Job.sendToSlaveServer(jobMeta, jobExecutionConfiguration, null, null);
                System.out.println("lastCarteObjectId=" + lastCarteObjectId);
                SlaveServerJobStatus jobStatus = null;
                do {
                    Thread.sleep(5000);
                    jobStatus = paramBean.getSlaveServer().getJobStatus(jobMeta.getName(), lastCarteObjectId, 0);
                } while (jobStatus != null && jobStatus.isRunning());
                System.out.println(jobStatus);
                if (jobStatus.getResult() != null) {
                    return Constant.SUCCESS;
                }

            }

        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new KettleException(e.getMessage());
        }

        return Constant.SUCCESS;
    }
}
