package org.dangcat.web.invoke;

import org.dangcat.framework.service.impl.ServiceInfo;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class InvokeProcess {
    public static final String UPLOAD = "upload";
    private Map<String, InvokeStep> invokeStepMap = new LinkedHashMap<String, InvokeStep>();
    private ServiceInfo serviceInfo = null;

    public InvokeProcess(String... names) {
        if (names != null) {
            for (String name : names)
                this.invokeStepMap.put(name, new InvokeStep(name));
        }
    }

    public void cancel() {
        for (InvokeStep invokeStep : this.invokeStepMap.values())
            invokeStep.cancel();
    }

    public Collection<InvokeResult> getInvokeResults() {
        Collection<InvokeResult> invokeResults = new LinkedList<InvokeResult>();
        for (InvokeStep invokeStep : this.invokeStepMap.values()) {
            InvokeResult invokeResult = new InvokeResult();
            invokeResult.setName(this.getStepTitle(invokeStep.getName()));
            invokeResult.setProcess(invokeStep.getPercent());
            invokeResult.setStatus(invokeStep.getStatus());
            invokeResult.setFinished(invokeStep.isFinished());
            invokeResults.add(invokeResult);
        }
        return invokeResults;
    }

    public InvokeStep getInvokeStep(String name) {
        return this.invokeStepMap.get(name);
    }

    public Collection<InvokeStep> getInvokeSteps() {
        return this.invokeStepMap.values();
    }

    private String getStepTitle(String name) {
        return this.serviceInfo.getResourceReader().getText(name + ".title");
    }

    public long getTimeCost() {
        long timeCost = 0;
        for (InvokeStep invokeStep : this.invokeStepMap.values())
            timeCost += invokeStep.getTimeCost();
        return timeCost;
    }

    public boolean isFinishedAll() {
        for (InvokeStep invokeStep : this.getInvokeSteps()) {
            if (!invokeStep.isFinished())
                return false;
        }
        return true;
    }

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }
}
