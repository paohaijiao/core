package org.pentaho.di.paohaijiao.preview;

import org.pentaho.di.core.ProgressNullMonitorListener;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.paohaijiao.bean.Constant;
import org.pentaho.di.paohaijiao.bean.ParamBean;
import org.pentaho.di.paohaijiao.bean.ResultBean;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: core
 * @description: 描述
 * @author: 泡海椒
 * @create: 2020-04-20 15:21
 **/
public class KettleService {
    public ResultBean getFieldFromPreviousStep(ParamBean paramBean)throws Exception{

            String[] typeCodes =Constant.typeCodes;
                RepositoryDirectoryInterface directory = paramBean.getKettleDatabaseRepository().loadRepositoryDirectoryTree()
                        .findDirectory(paramBean.getDiretory());
                TransMeta transMeta = paramBean.getKettleDatabaseRepository().loadTransformation(paramBean.getName(), directory,
                        new ProgressNullMonitorListener(), true, null);
                RowMetaInterface rowMetaInterface= transMeta.getPrevStepFields(paramBean.getStepName());
                List<ValueMetaInterface> previousList=rowMetaInterface.getValueMetaList();
                List<org.pentaho.di.paohaijiao.bean.ValueMetaInterface> list=new ArrayList<>();
                previousList.forEach(e->{
                    org.pentaho.di.paohaijiao.bean.ValueMetaInterface valueMetaInterface=new org.pentaho.di.paohaijiao.bean.ValueMetaInterface();
                    valueMetaInterface.setName(e.getName());
                    valueMetaInterface.setComments(e.getComments());
                    valueMetaInterface.setType(typeCodes[e.getType()]);
                    list.add(valueMetaInterface);
                });
                ResultBean bean=new ResultBean();
                bean.setCode(200);
                bean.setMessage("请求预览接口成功了");
                bean.setData(list);
                return  bean;
    }
    public ResultBean preview(ParamBean paramBean,Integer row) throws Exception {
        RepositoryDirectoryInterface directory = paramBean.getKettleDatabaseRepository().loadRepositoryDirectoryTree()
                .findDirectory(paramBean.getDiretory());
        TransMeta transMeta = paramBean.getKettleDatabaseRepository().loadTransformation(paramBean.getName(), directory,
                new ProgressNullMonitorListener(), true, null);
        Trans trans = new Trans(transMeta);
        trans.setPreview(true);
        ResultBean bean = new ResultBean();
        bean.setCode(200);
        bean.setMessage("请求预览接口成功了");
        ResultBean resultBean = Preview.preview(transMeta, paramBean.getStepName(), row);
        return resultBean;
    }


}
