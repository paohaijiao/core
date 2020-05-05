package org.pentaho.di.paohaijiao.preview;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.paohaijiao.bean.PreviewBean;
import org.pentaho.di.paohaijiao.bean.ResultBean;
import org.pentaho.di.paohaijiao.bean.ValueMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.debug.BreakPointListener;
import org.pentaho.di.trans.debug.StepDebugMeta;
import org.pentaho.di.trans.debug.TransDebugMeta;
import org.pentaho.di.trans.step.StepMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: core
 * @description: 描述
 * @author: 泡海椒
 * @create: 2020-04-20 12:20
 **/
public class Preview {
    public static ResultBean preview(TransMeta transMeta, String stepName, Integer limited) throws KettleException {
        try {
            Trans trans = new Trans(transMeta);
            trans.setPreview(true);
            trans.prepareExecution(null);
            TransDebugMeta transDebugMeta = new TransDebugMeta(transMeta);
            StepMeta stepMeta = transMeta.findStep(stepName);
            StepDebugMeta stepDebugMeta = new StepDebugMeta(stepMeta);
            stepDebugMeta.setReadingFirstRows(true);
            stepDebugMeta.setRowCount(limited);
            transDebugMeta.getStepDebugMetaMap().put(stepMeta, stepDebugMeta);
            final List<String> previewComplete = new ArrayList<String>();
            transDebugMeta.addBreakPointListers(new BreakPointListener() {
                public void breakPointHit(TransDebugMeta transDebugMeta, StepDebugMeta stepDebugMeta,
                                          RowMetaInterface rowBufferMeta, List<Object[]> rowBuffer) {
                    String stepName = stepDebugMeta.getStepMeta().getName();
                    previewComplete.add(stepName);
                }
            });
            PreviewBean copy=new PreviewBean();
            transDebugMeta.addRowListenersToTransformation(trans);
            trans.startThreads();
            while(true){
                if(trans.isFinished()){ //drill没有设置finish状态
                    return   makeReturnData(trans,transDebugMeta);
                }else {//如果是drill数据源看是否有数据
                    for ( StepDebugMeta debugMeta : transDebugMeta.getStepDebugMetaMap().values() ) {
                        if(0!=debugMeta.getRowBuffer().size()){
                            return   makeReturnData(trans,transDebugMeta);
                        }
                    }
                }
            }


        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }
    private static ResultBean makeReturnData(  Trans trans,TransDebugMeta transDebugMeta){
        PreviewBean copy=new PreviewBean();
        for ( StepDebugMeta debugMeta : transDebugMeta.getStepDebugMetaMap().values() ) {
            List<Object[]>   data =new ArrayList<>(0);
            data.addAll(debugMeta.getRowBuffer());
            ((ArrayList<Object[]>) data).trimToSize();
            copy.setRowBuffer(data);
            List list=new ArrayList(0);
            debugMeta.getRowBufferMeta().getValueMetaList().forEach(e->{
                String name=e.getName();
                String comments=e.getComments();
                ValueMetaInterface comp=new ValueMetaInterface();
                comp.setName(name);
                comp.setComments(comments);
                list.add(comp);
            });
            copy.setRowBufferMeta(list);
            String loggingText =
                    KettleLogStore.getAppender().getBuffer(trans.getLogChannel().getLogChannelId(), true).toString();
            ResultBean resultBean = new ResultBean();
            resultBean.setData(copy);
            resultBean.setCode(200);
            resultBean.setMessage(loggingText);
            return resultBean;
        }
        ResultBean resultBean = new ResultBean();
        resultBean.setData(copy);
        resultBean.setCode(400);
        resultBean.setMessage("暂无数据");
        return resultBean;
    }
}
