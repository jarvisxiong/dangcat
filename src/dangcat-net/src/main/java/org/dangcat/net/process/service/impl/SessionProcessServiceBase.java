package org.dangcat.net.process.service.impl;

import org.dangcat.boot.service.DataProcessService;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.net.process.service.AfterDataProcess;
import org.dangcat.net.process.service.BeforeDataProcess;
import org.dangcat.net.process.service.ErrorDataProcess;
import org.dangcat.net.process.service.SessionBase;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 报文处理服务基础类。
 *
 * @author dangcat
 */
public class SessionProcessServiceBase<T extends SessionBase> extends ServiceBase implements BeforeDataProcess<T>, DataProcessService<T>, AfterDataProcess<T>, ErrorDataProcess<T> {
    private Collection<AfterDataProcess<T>> afterDataProcesses = new LinkedList<AfterDataProcess<T>>();
    private Collection<BeforeDataProcess<T>> beforeDataProcesses = new LinkedList<BeforeDataProcess<T>>();
    private Collection<DataProcessService<T>> dataProcessServices = new LinkedList<DataProcessService<T>>();
    private Collection<ErrorDataProcess<T>> errorDataProcesses = new LinkedList<ErrorDataProcess<T>>();

    /**
     * 构建服务。
     *
     * @param parent
     */
    public SessionProcessServiceBase(ServiceProvider parent) {
        super(parent);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addService(Class<?> classType, Object service) {
        super.addService(classType, service);

        // 预处理事件接口。
        if (service instanceof BeforeDataProcess) {
            BeforeDataProcess<T> beforeDataProcessService = (BeforeDataProcess<T>) service;
            if (beforeDataProcessService != null)
                this.beforeDataProcesses.add(beforeDataProcessService);
        }

        // 处理事件接口。
        if (service instanceof DataProcessService) {
            DataProcessService<T> dataProcessService = (DataProcessService<T>) service;
            if (dataProcessService != null)
                this.dataProcessServices.add(dataProcessService);
        }

        // 处理后事件接口。
        if (service instanceof AfterDataProcess) {
            AfterDataProcess<T> afterDataProcessService = (AfterDataProcess<T>) service;
            if (afterDataProcessService != null)
                this.afterDataProcesses.add(afterDataProcessService);
        }

        // 处理错误接口。
        if (service instanceof ErrorDataProcess) {
            ErrorDataProcess<T> errorDataProcessService = (ErrorDataProcess<T>) service;
            if (errorDataProcessService != null)
                this.errorDataProcesses.add(errorDataProcessService);
        }
    }

    @Override
    public void afterProcess(T session) throws ServiceException {
        for (AfterDataProcess<T> afterDataProcess : this.afterDataProcesses) {
            afterDataProcess.afterProcess(session);
            session.logTimeCost(afterDataProcess.toString());
        }
    }

    @Override
    public void beforeProcess(T session) throws ServiceException {
        for (BeforeDataProcess<T> beforeDataProcess : this.beforeDataProcesses) {
            if (!session.isCancel()) {
                beforeDataProcess.beforeProcess(session);
                session.logTimeCost(beforeDataProcess.toString());
            }
        }
    }

    @Override
    public void onProcessError(T session, ServiceException exception) {
        for (ErrorDataProcess<T> errorDataProcess : this.errorDataProcesses) {
            errorDataProcess.onProcessError(session, exception);
            session.logTimeCost(errorDataProcess.toString());
        }
    }

    @Override
    public void process(T session) throws ServiceException {
        for (DataProcessService<T> dataProcessService : this.dataProcessServices) {
            if (!session.isHandle()) {
                dataProcessService.process(session);
                session.logTimeCost(dataProcessService.toString());
            }
        }
    }
}
