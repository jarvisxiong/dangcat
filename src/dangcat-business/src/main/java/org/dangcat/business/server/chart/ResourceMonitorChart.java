package org.dangcat.business.server.chart;

import org.apache.log4j.Logger;
import org.dangcat.business.server.ServerInfoService;
import org.dangcat.business.server.ServerResourceLog;
import org.dangcat.chart.TimeData;
import org.dangcat.chart.TimeRange;
import org.dangcat.chart.TimeType;
import org.dangcat.chart.jfreechart.*;
import org.dangcat.chart.jfreechart.data.ColumnDataModule;
import org.dangcat.chart.jfreechart.data.DataModule;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.persistence.DataReader;
import org.dangcat.persistence.entity.EntityDataReader;
import org.dangcat.persistence.entity.EntityManager;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

class ResourceMonitorChart
{
    private static final Logger logger = Logger.getLogger(ResourceMonitorChart.class);
    private String activeArea = null;
    private byte[] chartImg = null;
    private EntityManager entityManager = null;
    private int height = 360;
    private Integer serverId;
    private ServerInfoService serverInfoService = null;
    private TimeRange timeRange = new TimeRange(TimeType.Hour);
    private long totalPhysicalMemory = 0;
    private int width = 600;

    protected static ResourceMonitorChart createInstance(ServerInfoService serverInfoService, Integer id, Integer width, Integer height)
    {
        ResourceMonitorChart resourceMonitorChart = ServiceContext.getInstance().getSession(ResourceMonitorChart.class);
        if (resourceMonitorChart == null)
        {
            resourceMonitorChart = new ResourceMonitorChart();
            ServiceContext.getInstance().addSession(ResourceMonitorChart.class, resourceMonitorChart);
            resourceMonitorChart.setServerInfoService(serverInfoService);
            resourceMonitorChart.setServerId(id);
            if (width != null)
                resourceMonitorChart.setWidth(width);
            if (height != null)
                resourceMonitorChart.setHeight(height);
            resourceMonitorChart.create();
        }
        return resourceMonitorChart;
    }

    protected void create()
    {
        ChartBase chartBase = this.createChart();

        ByteArrayOutputStream byteArrayOutputStream = null;
        try
        {
            byteArrayOutputStream = new ByteArrayOutputStream();
            chartBase.render(byteArrayOutputStream);
            this.chartImg = byteArrayOutputStream.toByteArray();
            this.activeArea = chartBase.getImageMap();
        }
        catch (IOException e)
        {
            logger.error(this, e);
        }
        finally
        {
            FileUtils.close(byteArrayOutputStream);
        }
    }

    private TimeChart createBarTimeChart(DataReader dataReader)
    {
        DataModule memoryDataModule = new ColumnDataModule(dataReader, new String[] { ServerResourceLog.ProcessUsageMemory, ServerResourceLog.OtherUsageMemory },
                new String[] { ServerResourceLog.DateTime });
        memoryDataModule.setMaxValue(this.totalPhysicalMemory);
        BarTimeChart barTimeChart = new BarTimeChart();
        barTimeChart.setRangeTitle(dataReader.getTitle("UsageMemoryRatio"));
        barTimeChart.setDataModule(memoryDataModule);
        return barTimeChart;
    }

    private ChartBase createChart()
    {
        TimeRange timeRange = this.getTimeRange();
        timeRange.setBaseTime(DateUtils.now());
        DataReader dataReader = this.loadServerStatusLogs();

        CombinedChart combinedChart = new CombinedChart();
        combinedChart.addTimeChart(this.createBarTimeChart(dataReader));
        combinedChart.addTimeChart(this.createDiffLineChart(dataReader));
        combinedChart.setBeginTime(timeRange.getBeginTime());
        combinedChart.setEndTime(timeRange.getEndTime());
        combinedChart.setTimeStep((int) timeRange.getTimeStep());
        combinedChart.setHeight(this.getHeight());
        combinedChart.setWidth(this.getWidth());
        combinedChart.setImageMapId("ImgMap");
        combinedChart.setLegendVisible(true);
        return combinedChart;
    }

    private TimeChart createDiffLineChart(DataReader dataReader)
    {
        DataModule cpuDataModule = new ColumnDataModule(dataReader, new String[] { ServerResourceLog.ProcessCpuRatio, ServerResourceLog.TotalCpuRatio }, new String[] { ServerResourceLog.DateTime });
        cpuDataModule.setRowMaxValue(100.0);
        LineChart lineChart = new LineChart();
        lineChart.setRangeTitle(dataReader.getTitle("CpuRatio"));
        lineChart.setDataModule(cpuDataModule);
        return lineChart;
    }

    protected String getActiveArea()
    {
        return activeArea;
    }

    protected EntityManager getEntityManager()
    {
        return entityManager;
    }

    protected void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected int getHeight()
    {
        return height;
    }

    protected void setHeight(int height) {
        this.height = height;
    }

    protected Integer getServerId()
    {
        return serverId;
    }

    protected void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public ServerInfoService getServerInfoService()
    {
        return serverInfoService;
    }

    public void setServerInfoService(ServerInfoService serverInfoService) {
        this.serverInfoService = serverInfoService;
    }

    public TimeRange getTimeRange()
    {
        return timeRange;
    }

    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    protected int getWidth()
    {
        return width;
    }

    protected void setWidth(int width) {
        this.width = width;
    }

    private DataReader loadServerStatusLogs()
    {
        this.totalPhysicalMemory = this.serverInfoService.getTotalPhysicalMemory(this.getServerId());
        TimeData<ServerResourceLog> timeData = this.serverInfoService.loadServerResourceLogs(this.getServerId(), this.getTimeRange(), null);
        return new EntityDataReader<ServerResourceLog>((List<ServerResourceLog>) timeData.getData(), ServerResourceLog.class);
    }

    protected boolean render() throws ServiceException
    {
        boolean valid = this.chartImg != null && this.chartImg.length > 0;
        OutputStream outputStream = null;
        try
        {
            if (valid)
            {
                HttpServletResponse httpServletResponse = ServiceContext.getInstance().getParam(HttpServletResponse.class);
                if (httpServletResponse != null)
                {
                    outputStream = httpServletResponse.getOutputStream();
                    outputStream.write(this.chartImg);
                }
            }
        }
        catch (IOException e)
        {
            logger.error(this, e);
        }
        finally
        {
            FileUtils.close(outputStream);
            ServiceContext.getInstance().removeSession(ResourceMonitorChart.class);
        }
        return valid;
    }
}
