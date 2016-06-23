package org.dangcat.chart.highcharts;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dangcat.boot.ApplicationContext;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.web.servlet.ContentType;

public class ChartExportServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private File baseDir = null;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        System.out.println(request.getParameter("options"));
        String options = request.getParameter("options");
        String data = request.getParameter("data");
        String type = request.getParameter("type");
        Integer width = ValueUtils.parseInt(request.getParameter("width"));
        Integer height = ValueUtils.parseInt(request.getParameter("height"));
        if (ValueUtils.isEmpty(options) || ValueUtils.isEmpty(type))
            return;

        File output = File.createTempFile("highcharts", "." + type);
        ChartExporter chartExporter = new ChartExporter(this.getBaseDir());
        chartExporter.setWidth(width);
        chartExporter.setHeight(height);
        chartExporter.export(options, data, output);
        if (output.exists())
        {
            OutputStream outputStream = response.getOutputStream();
            try
            {
                String fileName = "chart." + type;
                response.setContentType(ContentType.getType(fileName));
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
                response.setStatus(HttpServletResponse.SC_OK);
                FileUtils.copy(output, outputStream);
            }
            finally
            {
                outputStream.flush();
                outputStream.close();
                output.delete();
            }
        }
    }

    private File getBaseDir()
    {
        File baseDir = this.baseDir;
        if (baseDir == null)
        {
            if (!ValueUtils.isEmpty(this.getInitParameter("baseDir")))
                baseDir = new File(this.getInitParameter("baseDir"));
            if (baseDir == null || !baseDir.exists())
                baseDir = new File("../dangcat-web/src/main/webapp");
            if (baseDir == null || !baseDir.exists())
                baseDir = new File(ApplicationContext.getInstance().getContextPath().getWebApps());
            if (baseDir != null && baseDir.exists())
                this.baseDir = baseDir;
        }
        return baseDir;
    }
}
