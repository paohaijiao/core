package org.pentaho.di.paohaijiao.bean;

import lombok.Data;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;

/**
 * @program: core
 * @description: 描述
 * @author: 泡海椒
 * @create: 2020-04-20 12:28
 **/
@Data
public class ParamBean {
    private String userName;
    private String password;
    private String diretory;
    private String name;
    private String stepName;
    private KettleDatabaseRepository kettleDatabaseRepository;
    private Boolean clusterExecutor;
    private LogLevel logLevel;
    private SlaveServer slaveServer;
}
